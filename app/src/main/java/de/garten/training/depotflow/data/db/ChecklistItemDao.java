package de.garten.training.depotflow.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.data.db.green.ChecklistItem;

public class ChecklistItemDao {

    public static final String TABLE_NAME = "CHECKLIST_ITEM";

    public static final class Properties {
        public static final String Id = "_id";
        public static final String WorkOrderId = "WORK_ORDER_ID";
        public static final String RemoteId = "REMOTE_ID";
        public static final String Label = "LABEL";
        public static final String Checked = "IS_CHECKED";
        public static final String Mandatory = "IS_MANDATORY";
        public static final String Note = "NOTE";
    }

    private final SQLiteOpenHelper helper;

    public ChecklistItemDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public long insert(ChecklistItem item) {
        long id = helper.getWritableDatabase().insert(TABLE_NAME, null, toValues(item));
        item.setId(id);
        return id;
    }

    public void replaceForWorkOrder(long workOrderId, List<ChecklistItem> items) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, Properties.WorkOrderId + " = ?", new String[]{String.valueOf(workOrderId)});
            for (ChecklistItem item : items) {
                item.setWorkOrderId(workOrderId);
                db.insert(TABLE_NAME, null, toValues(item));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ChecklistItem> loadForWorkOrder(long workOrderId) {
        Cursor cursor = helper.getReadableDatabase().query(
                TABLE_NAME,
                null,
                Properties.WorkOrderId + " = ?",
                new String[]{String.valueOf(workOrderId)},
                null,
                null,
                Properties.Mandatory + " DESC, " + Properties.Label + " ASC"
        );
        try {
            List<ChecklistItem> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    public int deleteForWorkOrder(long workOrderId) {
        return helper.getWritableDatabase().delete(TABLE_NAME, Properties.WorkOrderId + " = ?", new String[]{String.valueOf(workOrderId)});
    }

    private ContentValues toValues(ChecklistItem item) {
        ContentValues values = new ContentValues();
        if (item.getId() != null) {
            values.put(Properties.Id, item.getId());
        }
        values.put(Properties.WorkOrderId, item.getWorkOrderId());
        values.put(Properties.RemoteId, item.getRemoteId());
        values.put(Properties.Label, item.getLabel());
        values.put(Properties.Checked, item.isChecked() ? 1 : 0);
        values.put(Properties.Mandatory, item.isMandatory() ? 1 : 0);
        values.put(Properties.Note, item.getNote());
        return values;
    }

    private ChecklistItem fromCursor(Cursor cursor) {
        ChecklistItem item = new ChecklistItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Properties.Id)));
        item.setWorkOrderId(cursor.getLong(cursor.getColumnIndexOrThrow(Properties.WorkOrderId)));
        item.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow(Properties.RemoteId)));
        item.setLabel(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Label)));
        item.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Checked)) == 1);
        item.setMandatory(cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Mandatory)) == 1);
        item.setNote(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Note)));
        return item;
    }
}
