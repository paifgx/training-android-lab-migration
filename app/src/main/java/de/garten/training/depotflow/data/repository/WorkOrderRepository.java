package de.garten.training.depotflow.data.repository;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.core.DateTimeProvider;
import de.garten.training.depotflow.core.LegacyExecutors;
import de.garten.training.depotflow.core.LegacyLogger;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.data.api.DepotApiClient;
import de.garten.training.depotflow.data.api.dto.ChecklistItemDto;
import de.garten.training.depotflow.data.api.dto.StopDto;
import de.garten.training.depotflow.data.api.dto.WorkOrderDto;
import de.garten.training.depotflow.data.db.LegacyDatabase;
import de.garten.training.depotflow.data.db.green.ChecklistItem;
import de.garten.training.depotflow.data.db.green.Stop;
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.data.mapper.ChecklistMapper;
import de.garten.training.depotflow.data.mapper.OutboxMapper;
import de.garten.training.depotflow.data.mapper.StopMapper;
import de.garten.training.depotflow.data.mapper.WorkOrderMapper;
import de.garten.training.depotflow.domain.WorkOrderStatus;

public class WorkOrderRepository {

    private static final String DEFAULT_DEPOT_ID = "north-01";

    private final LegacyDatabase database;
    private final DepotApiClient apiClient;
    private final LegacyExecutors executors;
    private final DateTimeProvider dateTimeProvider;
    private final WorkOrderMapper workOrderMapper = new WorkOrderMapper();
    private final StopMapper stopMapper = new StopMapper();
    private final ChecklistMapper checklistMapper = new ChecklistMapper();
    private final OutboxMapper outboxMapper = new OutboxMapper();
    private final Gson gson = new Gson();

    public WorkOrderRepository(LegacyDatabase database, DepotApiClient apiClient, LegacyExecutors executors, DateTimeProvider dateTimeProvider) {
        this.database = database;
        this.apiClient = apiClient;
        this.executors = executors;
        this.dateTimeProvider = dateTimeProvider;
    }

    public void loadLocalOrders(final ResultCallback<List<WorkOrder>> callback) {
        executors.disk(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<WorkOrder> orders = database.workOrderDao().loadAll();
                    executors.main(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(orders);
                        }
                    });
                } catch (final Throwable error) {
                    deliverError(callback, error);
                }
            }
        });
    }

    public void refreshOrders(final ResultCallback<List<WorkOrder>> callback) {
        apiClient.loadWorkOrders(DEFAULT_DEPOT_ID, null, new ResultCallback<List<WorkOrderDto>>() {
            @Override
            public void onSuccess(final List<WorkOrderDto> remoteOrders) {
                executors.disk(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (remoteOrders != null) {
                                for (WorkOrderDto dto : remoteOrders) {
                                    saveRemoteOrder(dto);
                                }
                            }
                            final List<WorkOrder> orders = database.workOrderDao().loadAll();
                            executors.main(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(orders);
                                }
                            });
                        } catch (final Throwable error) {
                            deliverError(callback, error);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                LegacyLogger.e("repo", "refresh failed", error);
                callback.onError(error);
            }
        });
    }

    public void loadDetails(final long localId, final ResultCallback<WorkOrderDetails> callback) {
        executors.disk(new Runnable() {
            @Override
            public void run() {
                try {
                    final WorkOrder order = database.workOrderDao().load(localId);
                    if (order == null) {
                        throw new IllegalArgumentException("Unknown work order id " + localId);
                    }
                    final List<Stop> stops = database.stopDao().loadForWorkOrder(localId);
                    final List<ChecklistItem> items = database.checklistItemDao().loadForWorkOrder(localId);
                    executors.main(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(new WorkOrderDetails(order, stops, items));
                        }
                    });
                } catch (final Throwable error) {
                    deliverError(callback, error);
                }
            }
        });
    }

    public void completeOffline(final String serverId, final ResultCallback<WorkOrder> callback) {
        executors.disk(new Runnable() {
            @Override
            public void run() {
                try {
                    WorkOrder before = database.workOrderDao().loadByServerId(serverId);
                    if (before == null) {
                        throw new IllegalArgumentException("Unknown work order " + serverId);
                    }

                    String updatedAt = dateTimeProvider.nowIsoUtc();
                    database.workOrderDao().updateLocalStatus(serverId, WorkOrderStatus.DONE, updatedAt);

                    SyncOutboxEntry outboxEntry = new SyncOutboxEntry();
                    outboxEntry.setAggregateType("WORK_ORDER");
                    outboxEntry.setAggregateId(serverId);
                    outboxEntry.setOperation("COMPLETE");
                    outboxEntry.setCreatedAt(updatedAt);
                    outboxEntry.setAttempts(0);
                    outboxEntry.setPayload(gson.toJson(new CompletionPayload(serverId, updatedAt, "android-legacy")));
                    database.syncOutboxEntryDao().insert(outboxEntry);

                    final WorkOrder after = database.workOrderDao().loadByServerId(serverId);

                    // Legacy smell: repository triggers an optimistic network call although the method name says offline.
                    apiClient.completeWorkOrder(serverId, outboxMapper.toDto(outboxEntry), new ResultCallback<WorkOrderDto>() {
                        @Override
                        public void onSuccess(final WorkOrderDto remote) {
                            executors.disk(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        saveRemoteOrder(remote);
                                        database.workOrderDao().markClean(serverId, remote.updatedAt);
                                    } catch (Throwable error) {
                                        LegacyLogger.e("repo", "remote completion merge failed", error);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable error) {
                            LegacyLogger.e("repo", "optimistic completion failed; outbox keeps pending change", error);
                        }
                    });

                    executors.main(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(after);
                        }
                    });
                } catch (final Throwable error) {
                    deliverError(callback, error);
                }
            }
        });
    }

    private void saveRemoteOrder(WorkOrderDto dto) {
        WorkOrder local = database.workOrderDao().loadByServerId(dto.id);
        if (!workOrderMapper.shouldOverwriteLocal(local, dto)) {
            return;
        }

        WorkOrder entity = workOrderMapper.fromDto(dto);
        if (local != null) {
            entity.setId(local.getId());
        }
        long localId = database.workOrderDao().insertOrReplace(entity);

        List<Stop> stops = new ArrayList<>();
        if (dto.stops != null) {
            for (StopDto stopDto : dto.stops) {
                stops.add(stopMapper.fromDto(stopDto));
            }
        }
        database.stopDao().replaceForWorkOrder(localId, stops);

        List<ChecklistItem> checklist = new ArrayList<>();
        if (dto.checklist != null) {
            for (ChecklistItemDto itemDto : dto.checklist) {
                checklist.add(checklistMapper.fromDto(itemDto));
            }
        }
        database.checklistItemDao().replaceForWorkOrder(localId, checklist);
    }

    private void deliverError(final ResultCallback<?> callback, final Throwable error) {
        executors.main(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }

    private static class CompletionPayload {
        final String serverId;
        final String completedAt;
        final String source;

        CompletionPayload(String serverId, String completedAt, String source) {
            this.serverId = serverId;
            this.completedAt = completedAt;
            this.source = source;
        }
    }
}
