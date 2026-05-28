package de.garten.training.depotflow.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.data.db.converter.StopTypeConverter;
import de.garten.training.depotflow.data.db.green.Stop;

public class StopDao {

    public static final String TABLE_NAME = "STOP";

    public static final class Properties {
        public static final String Id = "_id";
        public static final String WorkOrderId = "WORK_ORDER_ID";
        public static final String RemoteId = "REMOTE_ID";
        public static final String Sequence = "SEQUENCE_NO";
        public static final String Type = "TYPE";
        public static final String Name = "NAME";
        public static final String Address = "ADDRESS";
        public static final String Latitude = "LATITUDE";
        public static final String Longitude = "LONGITUDE";
        public static final String Status = "STATUS";
        public static final String ArrivalWindowFrom = "ARRIVAL_WINDOW_FROM";
        public static final String ArrivalWindowTo = "ARRIVAL_WINDOW_TO";
    }

    private final SQLiteOpenHelper helper;
    private final StopTypeConverter typeConverter = new StopTypeConverter();

    public StopDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public long insert(Stop stop) {
        long id = helper.getWritableDatabase().insert(TABLE_NAME, null, toValues(stop));
        stop.id = id;
        return id;
    }

    public void replaceForWorkOrder(long workOrderId, List<Stop> stops) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, Properties.WorkOrderId + " = ?", new String[]{String.valueOf(workOrderId)});
            for (Stop stop : stops) {
                stop.workOrderId = workOrderId;
                db.insert(TABLE_NAME, null, toValues(stop));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Stop> loadForWorkOrder(long workOrderId) {
        Cursor cursor = helper.getReadableDatabase().query(
                TABLE_NAME,
                null,
                Properties.WorkOrderId + " = ?",
                new String[]{String.valueOf(workOrderId)},
                null,
                null,
                Properties.Sequence + " ASC"
        );
        try {
            List<Stop> result = new ArrayList<>();
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

    private ContentValues toValues(Stop stop) {
        ContentValues values = new ContentValues();
        if (stop.id != null) {
            values.put(Properties.Id, stop.id);
        }
        values.put(Properties.WorkOrderId, stop.workOrderId);
        values.put(Properties.RemoteId, stop.remoteId);
        values.put(Properties.Sequence, stop.sequence);
        values.put(Properties.Type, typeConverter.convertToDatabaseValue(stop.type));
        values.put(Properties.Name, stop.name);
        values.put(Properties.Address, stop.address);
        values.put(Properties.Latitude, stop.latitude);
        values.put(Properties.Longitude, stop.longitude);
        values.put(Properties.Status, stop.status);
        values.put(Properties.ArrivalWindowFrom, stop.arrivalWindowFrom);
        values.put(Properties.ArrivalWindowTo, stop.arrivalWindowTo);
        return values;
    }

    private Stop fromCursor(Cursor cursor) {
        Stop stop = new Stop();
        stop.id = cursor.getLong(cursor.getColumnIndexOrThrow(Properties.Id));
        stop.workOrderId = cursor.getLong(cursor.getColumnIndexOrThrow(Properties.WorkOrderId));
        stop.remoteId = cursor.getString(cursor.getColumnIndexOrThrow(Properties.RemoteId));
        stop.sequence = cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Sequence));
        stop.type = typeConverter.convertToEntityProperty(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Type)));
        stop.name = cursor.getString(cursor.getColumnIndexOrThrow(Properties.Name));
        stop.address = cursor.getString(cursor.getColumnIndexOrThrow(Properties.Address));
        stop.latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Properties.Latitude));
        stop.longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Properties.Longitude));
        stop.status = cursor.getString(cursor.getColumnIndexOrThrow(Properties.Status));
        stop.arrivalWindowFrom = cursor.getString(cursor.getColumnIndexOrThrow(Properties.ArrivalWindowFrom));
        stop.arrivalWindowTo = cursor.getString(cursor.getColumnIndexOrThrow(Properties.ArrivalWindowTo));
        return stop;
    }
}
