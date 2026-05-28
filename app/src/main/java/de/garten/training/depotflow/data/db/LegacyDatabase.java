package de.garten.training.depotflow.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.garten.training.depotflow.core.DateTimeProvider;
import de.garten.training.depotflow.domain.SyncStatus;
import de.garten.training.depotflow.domain.WorkOrderStatus;

public class LegacyDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "depotflow-legacy.db";
    public static final int DATABASE_VERSION = 4;

    private final DateTimeProvider dateTimeProvider;
    private final WorkOrderDao workOrderDao;
    private final StopDao stopDao;
    private final ChecklistItemDao checklistItemDao;
    private final SyncOutboxEntryDao syncOutboxEntryDao;

    public LegacyDatabase(Context context, DateTimeProvider dateTimeProvider) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dateTimeProvider = dateTimeProvider;
        workOrderDao = new WorkOrderDao(this);
        stopDao = new StopDao(this);
        checklistItemDao = new ChecklistItemDao(this);
        syncOutboxEntryDao = new SyncOutboxEntryDao(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSchema(db);
        insertSeedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE WORK_ORDER ADD COLUMN ASSIGNED_USER TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE WORK_ORDER ADD COLUMN SYNC_STATUS TEXT NOT NULL DEFAULT 'clean'");
            db.execSQL("ALTER TABLE WORK_ORDER ADD COLUMN LAST_ERROR TEXT");
            db.execSQL("ALTER TABLE WORK_ORDER ADD COLUMN DIRTY INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS SYNC_OUTBOX (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "AGGREGATE_TYPE TEXT NOT NULL, " +
                    "AGGREGATE_ID TEXT NOT NULL, " +
                    "OPERATION TEXT NOT NULL, " +
                    "PAYLOAD TEXT NOT NULL, " +
                    "CREATED_AT TEXT NOT NULL, " +
                    "ATTEMPTS INTEGER NOT NULL DEFAULT 0, " +
                    "LAST_ERROR TEXT)");
        }
    }

    private void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE WORK_ORDER (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SERVER_ID TEXT NOT NULL UNIQUE, " +
                "EXTERNAL_NUMBER TEXT NOT NULL, " +
                "TITLE TEXT NOT NULL, " +
                "CUSTOMER_NAME TEXT NOT NULL, " +
                "STATUS TEXT NOT NULL, " +
                "PRIORITY INTEGER NOT NULL DEFAULT 0, " +
                "DUE_AT TEXT, " +
                "UPDATED_AT TEXT, " +
                "ASSIGNED_USER TEXT, " +
                "SYNC_STATUS TEXT NOT NULL DEFAULT 'clean', " +
                "LAST_ERROR TEXT, " +
                "DIRTY INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE STOP (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WORK_ORDER_ID INTEGER NOT NULL, " +
                "REMOTE_ID TEXT, " +
                "SEQUENCE_NO INTEGER NOT NULL, " +
                "TYPE TEXT NOT NULL, " +
                "NAME TEXT NOT NULL, " +
                "ADDRESS TEXT, " +
                "LATITUDE REAL NOT NULL DEFAULT 0, " +
                "LONGITUDE REAL NOT NULL DEFAULT 0, " +
                "STATUS TEXT NOT NULL, " +
                "ARRIVAL_WINDOW_FROM TEXT, " +
                "ARRIVAL_WINDOW_TO TEXT)");

        db.execSQL("CREATE TABLE CHECKLIST_ITEM (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WORK_ORDER_ID INTEGER NOT NULL, " +
                "REMOTE_ID TEXT, " +
                "LABEL TEXT NOT NULL, " +
                "IS_CHECKED INTEGER NOT NULL DEFAULT 0, " +
                "IS_MANDATORY INTEGER NOT NULL DEFAULT 0, " +
                "NOTE TEXT)");

        db.execSQL("CREATE TABLE SYNC_OUTBOX (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "AGGREGATE_TYPE TEXT NOT NULL, " +
                "AGGREGATE_ID TEXT NOT NULL, " +
                "OPERATION TEXT NOT NULL, " +
                "PAYLOAD TEXT NOT NULL, " +
                "CREATED_AT TEXT NOT NULL, " +
                "ATTEMPTS INTEGER NOT NULL DEFAULT 0, " +
                "LAST_ERROR TEXT)");

        db.execSQL("CREATE INDEX IDX_STOP_WORK_ORDER ON STOP(WORK_ORDER_ID)");
        db.execSQL("CREATE INDEX IDX_CHECKLIST_WORK_ORDER ON CHECKLIST_ITEM(WORK_ORDER_ID)");
        db.execSQL("CREATE INDEX IDX_WORK_ORDER_STATUS ON WORK_ORDER(STATUS)");
        db.execSQL("CREATE INDEX IDX_SYNC_OUTBOX_AGGREGATE ON SYNC_OUTBOX(AGGREGATE_ID)");
    }

    private void insertSeedData(SQLiteDatabase db) {
        if (DatabaseUtils.queryNumEntries(db, "WORK_ORDER") > 0) {
            return;
        }

        long firstOrderId = insertWorkOrder(db,
                "wo-1001",
                "DF-2026-1001",
                "Torprüfung und Scanner-Tausch",
                "Norddepot Cloppenburg",
                WorkOrderStatus.ACCEPTED.toServerValue(),
                8,
                "2026-05-27T15:00:00Z",
                "2026-05-27T08:10:00Z",
                "m.schneider",
                SyncStatus.CLEAN.toDatabaseValue());

        insertStop(db, firstOrderId, "st-1001-1", 1, "service", "Wareneingang Tor 3", "Industriestraße 12", 52.847, 8.041, "open", "2026-05-27T12:00:00Z", "2026-05-27T13:00:00Z");
        insertStop(db, firstOrderId, "st-1001-2", 2, "return", "IT-Lager", "Industriestraße 12", 52.848, 8.039, "open", "2026-05-27T13:15:00Z", "2026-05-27T14:00:00Z");
        insertChecklistItem(db, firstOrderId, "ci-1001-1", "Scanner Seriennummer fotografieren", false, true, null);
        insertChecklistItem(db, firstOrderId, "ci-1001-2", "Torprüfung dokumentieren", false, true, null);

        long secondOrderId = insertWorkOrder(db,
                "wo-1002",
                "DF-2026-1002",
                "Ersatzteile an Außenlager liefern",
                "Servicepunkt Bremen",
                WorkOrderStatus.IN_PROGRESS.toServerValue(),
                5,
                "2026-05-28T09:30:00Z",
                "2026-05-27T07:25:00Z",
                "l.koenig",
                SyncStatus.DIRTY.toDatabaseValue());

        insertStop(db, secondOrderId, "st-1002-1", 1, "pickup", "Zentrallager", "Am Hafen 4", 53.082, 8.803, "done", "2026-05-28T07:00:00Z", "2026-05-28T07:30:00Z");
        insertStop(db, secondOrderId, "st-1002-2", 2, "delivery", "Servicepunkt Bremen", "Wartungsring 6", 53.073, 8.834, "open", "2026-05-28T09:00:00Z", "2026-05-28T09:30:00Z");
        insertChecklistItem(db, secondOrderId, "ci-1002-1", "Paketübergabe bestätigen lassen", false, true, null);
        insertChecklistItem(db, secondOrderId, "ci-1002-2", "Leergut mitnehmen", false, false, "optional wenn Platz im Fahrzeug");

        ContentValues outbox = new ContentValues();
        outbox.put("AGGREGATE_TYPE", "WORK_ORDER");
        outbox.put("AGGREGATE_ID", "wo-1002");
        outbox.put("OPERATION", "STATUS_CHANGED");
        outbox.put("PAYLOAD", "{\"status\":\"in_progress\",\"source\":\"seed\"}");
        outbox.put("CREATED_AT", dateTimeProvider.nowIsoUtc());
        outbox.put("ATTEMPTS", 1);
        outbox.putNull("LAST_ERROR");
        db.insert("SYNC_OUTBOX", null, outbox);
    }

    private long insertWorkOrder(SQLiteDatabase db, String serverId, String externalNumber, String title, String customerName,
                                 String status, int priority, String dueAt, String updatedAt, String assignedUser, String syncStatus) {
        ContentValues values = new ContentValues();
        values.put("SERVER_ID", serverId);
        values.put("EXTERNAL_NUMBER", externalNumber);
        values.put("TITLE", title);
        values.put("CUSTOMER_NAME", customerName);
        values.put("STATUS", status);
        values.put("PRIORITY", priority);
        values.put("DUE_AT", dueAt);
        values.put("UPDATED_AT", updatedAt);
        values.put("ASSIGNED_USER", assignedUser);
        values.put("SYNC_STATUS", syncStatus);
        values.put("DIRTY", SyncStatus.DIRTY.toDatabaseValue().equals(syncStatus) ? 1 : 0);
        return db.insert("WORK_ORDER", null, values);
    }

    private void insertStop(SQLiteDatabase db, long workOrderId, String remoteId, int sequence, String type, String name,
                            String address, double latitude, double longitude, String status, String from, String to) {
        ContentValues values = new ContentValues();
        values.put("WORK_ORDER_ID", workOrderId);
        values.put("REMOTE_ID", remoteId);
        values.put("SEQUENCE_NO", sequence);
        values.put("TYPE", type);
        values.put("NAME", name);
        values.put("ADDRESS", address);
        values.put("LATITUDE", latitude);
        values.put("LONGITUDE", longitude);
        values.put("STATUS", status);
        values.put("ARRIVAL_WINDOW_FROM", from);
        values.put("ARRIVAL_WINDOW_TO", to);
        db.insert("STOP", null, values);
    }

    private void insertChecklistItem(SQLiteDatabase db, long workOrderId, String remoteId, String label, boolean checked,
                                     boolean mandatory, String note) {
        ContentValues values = new ContentValues();
        values.put("WORK_ORDER_ID", workOrderId);
        values.put("REMOTE_ID", remoteId);
        values.put("LABEL", label);
        values.put("IS_CHECKED", checked ? 1 : 0);
        values.put("IS_MANDATORY", mandatory ? 1 : 0);
        values.put("NOTE", note);
        db.insert("CHECKLIST_ITEM", null, values);
    }

    public WorkOrderDao workOrderDao() {
        return workOrderDao;
    }

    public StopDao stopDao() {
        return stopDao;
    }

    public ChecklistItemDao checklistItemDao() {
        return checklistItemDao;
    }

    public SyncOutboxEntryDao syncOutboxEntryDao() {
        return syncOutboxEntryDao;
    }
}
