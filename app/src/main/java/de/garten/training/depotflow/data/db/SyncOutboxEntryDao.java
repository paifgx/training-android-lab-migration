package de.garten.training.depotflow.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.data.db.green.SyncOutboxEntry;

public class SyncOutboxEntryDao {

    public static final String TABLE_NAME = "SYNC_OUTBOX";

    public static final class Properties {
        public static final String Id = "_id";
        public static final String AggregateType = "AGGREGATE_TYPE";
        public static final String AggregateId = "AGGREGATE_ID";
        public static final String Operation = "OPERATION";
        public static final String Payload = "PAYLOAD";
        public static final String CreatedAt = "CREATED_AT";
        public static final String Attempts = "ATTEMPTS";
        public static final String LastError = "LAST_ERROR";
    }

    private final SQLiteOpenHelper helper;

    public SyncOutboxEntryDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public long insert(SyncOutboxEntry entry) {
        long id = helper.getWritableDatabase().insert(TABLE_NAME, null, toValues(entry));
        entry.id = id;
        return id;
    }

    public List<SyncOutboxEntry> loadPending() {
        Cursor cursor = helper.getReadableDatabase().query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Properties.CreatedAt + " ASC"
        );
        try {
            List<SyncOutboxEntry> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    public int countPending() {
        return (int) DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), TABLE_NAME);
    }

    public void delete(long id) {
        helper.getWritableDatabase().delete(TABLE_NAME, Properties.Id + " = ?", new String[]{String.valueOf(id)});
    }

    public void markFailed(long id, String error) {
        ContentValues values = new ContentValues();
        values.put(Properties.LastError, error);
        helper.getWritableDatabase().update(TABLE_NAME, values, Properties.Id + " = ?", new String[]{String.valueOf(id)});
    }

    public void increaseAttempts(long id, String error) {
        SyncOutboxEntry entry = load(id);
        if (entry == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Properties.Attempts, entry.attempts + 1);
        values.put(Properties.LastError, error);
        helper.getWritableDatabase().update(TABLE_NAME, values, Properties.Id + " = ?", new String[]{String.valueOf(id)});
    }

    private SyncOutboxEntry load(long id) {
        Cursor cursor = helper.getReadableDatabase().query(
                TABLE_NAME,
                null,
                Properties.Id + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        try {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    private ContentValues toValues(SyncOutboxEntry entry) {
        ContentValues values = new ContentValues();
        if (entry.id != null) {
            values.put(Properties.Id, entry.id);
        }
        values.put(Properties.AggregateType, entry.aggregateType);
        values.put(Properties.AggregateId, entry.aggregateId);
        values.put(Properties.Operation, entry.operation);
        values.put(Properties.Payload, entry.payload);
        values.put(Properties.CreatedAt, entry.createdAt);
        values.put(Properties.Attempts, entry.attempts);
        values.put(Properties.LastError, entry.lastError);
        return values;
    }

    private SyncOutboxEntry fromCursor(Cursor cursor) {
        SyncOutboxEntry entry = new SyncOutboxEntry();
        entry.id = cursor.getLong(cursor.getColumnIndexOrThrow(Properties.Id));
        entry.aggregateType = cursor.getString(cursor.getColumnIndexOrThrow(Properties.AggregateType));
        entry.aggregateId = cursor.getString(cursor.getColumnIndexOrThrow(Properties.AggregateId));
        entry.operation = cursor.getString(cursor.getColumnIndexOrThrow(Properties.Operation));
        entry.payload = cursor.getString(cursor.getColumnIndexOrThrow(Properties.Payload));
        entry.createdAt = cursor.getString(cursor.getColumnIndexOrThrow(Properties.CreatedAt));
        entry.attempts = cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Attempts));
        entry.lastError = cursor.getString(cursor.getColumnIndexOrThrow(Properties.LastError));
        return entry;
    }
}
