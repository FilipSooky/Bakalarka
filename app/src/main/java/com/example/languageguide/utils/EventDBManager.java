package com.example.languageguide.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class EventDBManager {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public EventDBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void refreshEvents(DBHelper.DataLoadCallback callback) {
        dbHelper.refreshEvents(callback);
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                events.add(new Event(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME_FROM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME_TO))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }
}