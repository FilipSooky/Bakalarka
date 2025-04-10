package com.example.languageguide.utils;

import android.content.ContentValues;
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

    public void insertEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, event.getTitle());
        values.put(DBHelper.COLUMN_DESCRIPTION, event.getDescription());
        values.put(DBHelper.COLUMN_DATE, event.getDate());
        values.put(DBHelper.COLUMN_TIME_FROM, event.getTimeFrom());
        values.put(DBHelper.COLUMN_TIME_TO, event.getTimeTo());

        db.insert(DBHelper.TABLE_EVENTS, null, values);
    }

    public void deleteAllEvents() {
        db.delete(DBHelper.TABLE_EVENTS, null, null);
    }

    public List<Event> getAllEvents() {
        dbHelper.updateEvents();
        List<Event> events = new ArrayList<>();
        String[] columns = {
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_TITLE,
                DBHelper.COLUMN_DESCRIPTION,
                DBHelper.COLUMN_DATE,
                DBHelper.COLUMN_TIME_FROM,
                DBHelper.COLUMN_TIME_TO
        };
        Cursor cursor = db.query(DBHelper.TABLE_EVENTS, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DATE));
            String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME_FROM));
            String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME_TO));

            Event event = new Event(id, title, description, date, timeFrom, timeTo);
            events.add(event);
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }
}