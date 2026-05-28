package de.garten.training.depotflow.core;

import android.content.Context;

import de.garten.training.depotflow.data.api.DepotApiClient;
import de.garten.training.depotflow.data.api.FakeDepotApiClient;
import de.garten.training.depotflow.data.api.RetrofitDepotApi;
import de.garten.training.depotflow.data.db.LegacyDatabase;
import de.garten.training.depotflow.data.repository.SyncRepository;
import de.garten.training.depotflow.data.repository.WorkOrderRepository;

public final class ServiceLocator {

    private static final String DEFAULT_BASE_URL = "https://example.invalid/depot-api/";
    private static final boolean USE_FAKE_API_FOR_TRAINING = true;

    private static ServiceLocator instance;

    private final LegacyExecutors executors;
    private final DateTimeProvider dateTimeProvider;
    private final LegacyDatabase database;
    private final DepotApiClient depotApiClient;
    private final WorkOrderRepository workOrderRepository;
    private final SyncRepository syncRepository;

    private ServiceLocator(Context context) {
        Context appContext = context.getApplicationContext();
        executors = new LegacyExecutors();
        dateTimeProvider = new DateTimeProvider();
        database = new LegacyDatabase(appContext, dateTimeProvider);
        depotApiClient = USE_FAKE_API_FOR_TRAINING
                ? new FakeDepotApiClient()
                : new RetrofitDepotApi(DEFAULT_BASE_URL);
        workOrderRepository = new WorkOrderRepository(database, depotApiClient, executors, dateTimeProvider);
        syncRepository = new SyncRepository(database, depotApiClient, executors, dateTimeProvider);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new ServiceLocator(context);
        }
    }

    public static ServiceLocator get() {
        if (instance == null) {
            throw new IllegalStateException("ServiceLocator not initialized");
        }
        return instance;
    }

    public LegacyDatabase database() {
        return database;
    }

    public WorkOrderRepository workOrderRepository() {
        return workOrderRepository;
    }

    public SyncRepository syncRepository() {
        return syncRepository;
    }

    public DateTimeProvider dateTimeProvider() {
        return dateTimeProvider;
    }
}
