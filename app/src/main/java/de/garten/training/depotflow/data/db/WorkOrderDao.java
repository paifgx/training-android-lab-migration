package de.garten.training.depotflow.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.data.db.converter.SyncStatusConverter;
import de.garten.training.depotflow.data.db.converter.WorkOrderStatusConverter;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.domain.SyncStatus;
import de.garten.training.depotflow.domain.WorkOrderStatus;

public class WorkOrderDao {

    public static final String TABLE_NAME = "WORK_ORDER";

    public static final class Properties {
        public static final String Id = "_id";
        public static final String ServerId = "SERVER_ID";
        public static final String ExternalNumber = "EXTERNAL_NUMBER";
        public static final String Title = "TITLE";
        public static final String CustomerName = "CUSTOMER_NAME";
        public static final String Status = "STATUS";
        public static final String Priority = "PRIORITY";
        public static final String DueAt = "DUE_AT";
        public static final String UpdatedAt = "UPDATED_AT";
        public static final String AssignedUser = "ASSIGNED_USER";
        public static final String SyncStatus = "SYNC_STATUS";
        public static final String LastError = "LAST_ERROR";
        public static final String Dirty = "DIRTY";
    }

    private final SQLiteOpenHelper helper;
    private final WorkOrderStatusConverter statusConverter = new WorkOrderStatusConverter();
    private final SyncStatusConverter syncStatusConverter = new SyncStatusConverter();

    public WorkOrderDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public long insertOrReplace(WorkOrder entity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = toValues(entity);
        long id = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        entity.setId(id);
        return id;
    }

    public List<WorkOrder> loadAll() {
        return query(null, null, Properties.Priority + " DESC, " + Properties.DueAt + " ASC");
    }

    public WorkOrder load(long id) {
        List<WorkOrder> result = query(Properties.Id + " = ?", new String[]{String.valueOf(id)}, null);
        return result.isEmpty() ? null : result.get(0);
    }

    public WorkOrder loadByServerId(String serverId) {
        List<WorkOrder> result = query(Properties.ServerId + " = ?", new String[]{serverId}, null);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<WorkOrder> queryDirty() {
        return query(Properties.Dirty + " = 1", null, Properties.UpdatedAt + " ASC");
    }

    public List<WorkOrder> queryByStatus(WorkOrderStatus status) {
        return query(Properties.Status + " = ?", new String[]{status.toServerValue()}, Properties.DueAt + " ASC");
    }

    public void markClean(String serverId, String updatedAt) {
        ContentValues values = new ContentValues();
        values.put(Properties.SyncStatus, SyncStatus.CLEAN.toDatabaseValue());
        values.put(Properties.Dirty, 0);
        values.putNull(Properties.LastError);
        values.put(Properties.UpdatedAt, updatedAt);
        helper.getWritableDatabase().update(TABLE_NAME, values, Properties.ServerId + " = ?", new String[]{serverId});
    }

    public void markSyncFailed(String serverId, String error) {
        ContentValues values = new ContentValues();
        values.put(Properties.SyncStatus, SyncStatus.FAILED.toDatabaseValue());
        values.put(Properties.LastError, error);
        values.put(Properties.Dirty, 1);
        helper.getWritableDatabase().update(TABLE_NAME, values, Properties.ServerId + " = ?", new String[]{serverId});
    }

    public void updateLocalStatus(String serverId, WorkOrderStatus status, String updatedAt) {
        ContentValues values = new ContentValues();
        values.put(Properties.Status, status.toServerValue());
        values.put(Properties.SyncStatus, SyncStatus.DIRTY.toDatabaseValue());
        values.put(Properties.Dirty, 1);
        values.put(Properties.UpdatedAt, updatedAt);
        helper.getWritableDatabase().update(TABLE_NAME, values, Properties.ServerId + " = ?", new String[]{serverId});
    }

    public int deleteAll() {
        return helper.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), TABLE_NAME);
    }

    public QueryBuilder queryBuilder() {
        return new QueryBuilder();
    }

    private List<WorkOrder> query(String selection, String[] args, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, args, null, null, orderBy);
        try {
            List<WorkOrder> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    private ContentValues toValues(WorkOrder entity) {
        ContentValues values = new ContentValues();
        if (entity.getId() != null) {
            values.put(Properties.Id, entity.getId());
        }
        values.put(Properties.ServerId, entity.getServerId());
        values.put(Properties.ExternalNumber, entity.getExternalNumber());
        values.put(Properties.Title, entity.getTitle());
        values.put(Properties.CustomerName, entity.getCustomerName());
        values.put(Properties.Status, statusConverter.convertToDatabaseValue(entity.getStatus()));
        values.put(Properties.Priority, entity.getPriority());
        values.put(Properties.DueAt, entity.getDueAt());
        values.put(Properties.UpdatedAt, entity.getUpdatedAt());
        values.put(Properties.AssignedUser, entity.getAssignedUser());
        values.put(Properties.SyncStatus, syncStatusConverter.convertToDatabaseValue(entity.getSyncStatus()));
        values.put(Properties.LastError, entity.getLastError());
        values.put(Properties.Dirty, entity.isDirty() ? 1 : 0);
        return values;
    }

    private WorkOrder fromCursor(Cursor cursor) {
        WorkOrder entity = new WorkOrder();
        entity.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Properties.Id)));
        entity.setServerId(cursor.getString(cursor.getColumnIndexOrThrow(Properties.ServerId)));
        entity.setExternalNumber(cursor.getString(cursor.getColumnIndexOrThrow(Properties.ExternalNumber)));
        entity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Title)));
        entity.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(Properties.CustomerName)));
        entity.setStatus(statusConverter.convertToEntityProperty(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Status))));
        entity.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Priority)));
        entity.setDueAt(cursor.getString(cursor.getColumnIndexOrThrow(Properties.DueAt)));
        entity.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(Properties.UpdatedAt)));
        entity.setAssignedUser(cursor.getString(cursor.getColumnIndexOrThrow(Properties.AssignedUser)));
        entity.setSyncStatus(syncStatusConverter.convertToEntityProperty(cursor.getString(cursor.getColumnIndexOrThrow(Properties.SyncStatus))));
        entity.setLastError(cursor.getString(cursor.getColumnIndexOrThrow(Properties.LastError)));
        entity.setDirty(cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Dirty)) == 1);
        return entity;
    }

    public final class QueryBuilder {
        private final List<String> clauses = new ArrayList<>();
        private final List<String> args = new ArrayList<>();
        private String orderBy;

        public QueryBuilder whereStatus(WorkOrderStatus status) {
            clauses.add(Properties.Status + " = ?");
            args.add(status.toServerValue());
            return this;
        }

        public QueryBuilder whereDirty(boolean dirty) {
            clauses.add(Properties.Dirty + " = ?");
            args.add(dirty ? "1" : "0");
            return this;
        }

        public QueryBuilder orderAsc(String column) {
            orderBy = column + " ASC";
            return this;
        }

        public QueryBuilder orderDesc(String column) {
            orderBy = column + " DESC";
            return this;
        }

        public List<WorkOrder> list() {
            String selection = null;
            if (!clauses.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < clauses.size(); i++) {
                    if (i > 0) {
                        builder.append(" AND ");
                    }
                    builder.append(clauses.get(i));
                }
                selection = builder.toString();
            }
            return query(selection, args.toArray(new String[0]), orderBy);
        }
    }
}
