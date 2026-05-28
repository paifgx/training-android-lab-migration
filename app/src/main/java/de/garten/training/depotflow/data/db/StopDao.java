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
        stop.setId(id);
        return id;
    }

    public void replaceForWorkOrder(long workOrderId, List<Stop> stops) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, Properties.WorkOrderId + " = ?", new String[]{String.valueOf(workOrderId)});
            for (Stop stop : stops) {
                stop.setWorkOrderId(workOrderId);
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
        if (stop.getId() != null) {
            values.put(Properties.Id, stop.getId());
        }
        values.put(Properties.WorkOrderId, stop.getWorkOrderId());
        values.put(Properties.RemoteId, stop.getRemoteId());
        values.put(Properties.Sequence, stop.getSequence());
        values.put(Properties.Type, typeConverter.convertToDatabaseValue(stop.getType()));
        values.put(Properties.Name, stop.getName());
        values.put(Properties.Address, stop.getAddress());
        values.put(Properties.Latitude, stop.getLatitude());
        values.put(Properties.Longitude, stop.getLongitude());
        values.put(Properties.Status, stop.getStatus());
        values.put(Properties.ArrivalWindowFrom, stop.getArrivalWindowFrom());
        values.put(Properties.ArrivalWindowTo, stop.getArrivalWindowTo());
        return values;
    }

    private Stop fromCursor(Cursor cursor) {
        Stop stop = new Stop();
        stop.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Properties.Id)));
        stop.setWorkOrderId(cursor.getLong(cursor.getColumnIndexOrThrow(Properties.WorkOrderId)));
        stop.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow(Properties.RemoteId)));
        stop.setSequence(cursor.getInt(cursor.getColumnIndexOrThrow(Properties.Sequence)));
        stop.setType(typeConverter.convertToEntityProperty(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Type))));
        stop.setName(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Name)));
        stop.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Address)));
        stop.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(Properties.Latitude)));
        stop.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(Properties.Longitude)));
        stop.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(Properties.Status)));
        stop.setArrivalWindowFrom(cursor.getString(cursor.getColumnIndexOrThrow(Properties.ArrivalWindowFrom)));
        stop.setArrivalWindowTo(cursor.getString(cursor.getColumnIndexOrThrow(Properties.ArrivalWindowTo)));
        return stop;
    }
}
