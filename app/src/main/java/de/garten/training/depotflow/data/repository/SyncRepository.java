package de.garten.training.depotflow.data.repository;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.core.DateTimeProvider;
import de.garten.training.depotflow.core.LegacyExecutors;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.data.api.DepotApiClient;
import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.api.dto.SyncResponseDto;
import de.garten.training.depotflow.data.db.LegacyDatabase;
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry;
import de.garten.training.depotflow.data.mapper.OutboxMapper;

public class SyncRepository {

    private final LegacyDatabase database;
    private final DepotApiClient apiClient;
    private final LegacyExecutors executors;
    private final DateTimeProvider dateTimeProvider;
    private final OutboxMapper outboxMapper = new OutboxMapper();

    public SyncRepository(LegacyDatabase database, DepotApiClient apiClient, LegacyExecutors executors, DateTimeProvider dateTimeProvider) {
        this.database = database;
        this.apiClient = apiClient;
        this.executors = executors;
        this.dateTimeProvider = dateTimeProvider;
    }

    public void loadSummary(final ResultCallback<String> callback) {
        executors.disk(new Runnable() {
            @Override
            public void run() {
                try {
                    int pending = database.syncOutboxEntryDao().countPending();
                    int dirty = database.workOrderDao().queryDirty().size();
                    final String summary = "Ausstehende Outbox-Einträge: " + pending + "\n" +
                            "Dirty Work Orders: " + dirty + "\n" +
                            "Letzter lokaler Check: " + dateTimeProvider.nowIsoUtc();
                    executors.main(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(summary);
                        }
                    });
                } catch (final Throwable error) {
                    deliverError(callback, error);
                }
            }
        });
    }

    public void pushPending(final ResultCallback<String> callback) {
        executors.disk(new Runnable() {
            @Override
            public void run() {
                final List<SyncOutboxEntry> pending = database.syncOutboxEntryDao().loadPending();
                if (pending.isEmpty()) {
                    executors.main(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess("Keine lokalen Änderungen vorhanden.");
                        }
                    });
                    return;
                }

                final List<OutboxDto> payload = new ArrayList<>();
                for (SyncOutboxEntry entry : pending) {
                    payload.add(outboxMapper.toDto(entry));
                }

                apiClient.pushOutbox(payload, new ResultCallback<SyncResponseDto>() {
                    @Override
                    public void onSuccess(final SyncResponseDto response) {
                        executors.disk(new Runnable() {
                            @Override
                            public void run() {
                                for (SyncOutboxEntry entry : pending) {
                                    if (response.success || response.acceptedAggregateIds == null || response.acceptedAggregateIds.contains(entry.getAggregateId())) {
                                        database.syncOutboxEntryDao().delete(entry.getId());
                                        database.workOrderDao().markClean(entry.getAggregateId(), response.serverTime);
                                    }
                                }
                                executors.main(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess("Sync abgeschlossen. Serverzeit: " + response.serverTime);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable error) {
                        executors.disk(new Runnable() {
                            @Override
                            public void run() {
                                for (SyncOutboxEntry entry : pending) {
                                    database.syncOutboxEntryDao().increaseAttempts(entry.getId(), error.getMessage());
                                    database.workOrderDao().markSyncFailed(entry.getAggregateId(), error.getMessage());
                                }
                                deliverError(callback, error);
                            }
                        });
                    }
                });
            }
        });
    }

    private void deliverError(final ResultCallback<?> callback, final Throwable error) {
        executors.main(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }
}
