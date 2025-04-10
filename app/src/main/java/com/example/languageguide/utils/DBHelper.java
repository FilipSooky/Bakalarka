package com.example.languageguide.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.languageguide.R;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 2;
    private final Resources resources;

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME_FROM = "time_from";
    public static final String COLUMN_TIME_TO = "time_to";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        resources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_EVENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TIME_FROM + " TEXT, "
                + COLUMN_TIME_TO + " TEXT)";
        db.execSQL(CREATE_TABLE);
        insertEventsFromXml(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void updateEvents() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EVENTS);
        insertEventsFromXml(db);
    }

    private void insertEventsFromXml(SQLiteDatabase db) {
        List<Event> events = readEventsFromXml();
        for (Event event : events) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, event.getTitle());
            values.put(COLUMN_DESCRIPTION, event.getDescription());
            values.put(COLUMN_DATE, event.getDate());
            values.put(COLUMN_TIME_FROM, event.getTimeFrom());
            values.put(COLUMN_TIME_TO, event.getTimeTo());
            db.insert(TABLE_EVENTS, null, values);
        }
    }

    public List<Event> readEventsFromXml() {
        List<Event> events = new ArrayList<>();
        String[] titles = resources.getStringArray(R.array.event_titles);
        String[] descriptions = resources.getStringArray(R.array.event_descriptions);
        String[] dates = resources.getStringArray(R.array.event_date);
        String[] timesFrom = resources.getStringArray(R.array.event_times_from);
        String[] timesTo = resources.getStringArray(R.array.event_times_to);

        for (int i = 0; i < titles.length; i++) {
            events.add(new Event(i, titles[i], descriptions[i], dates[i], timesFrom[i], timesTo[i]));
        }
        return events;
    }
}