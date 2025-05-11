package com.example.languageguide.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.languageguide.utils.locations.InfoItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 8;
    private static final String JSON_URL = "http://192.168.1.23/arrays.json"; // 192.168.1.23
    private static final String FLOORS_JSON_URL = "http://192.168.1.23/floors.json";
    private static final String INFO_JSON_URL = "http://192.168.1.23/info.json";

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME_FROM = "time_from";
    public static final String COLUMN_TIME_TO = "time_to";

    // Table and column names for floors, rooms, and schedule
    private static final String TABLE_FLOORS = "floors";
    private static final String FLOOR_NAME = "name";
    private static final String FLOOR_IMAGE = "imageResource";

    private static final String TABLE_ROOMS = "rooms";
    private static final String ROOM_NAME = "name";

    private static final String TABLE_SCHEDULE = "schedule";
    private static final String SCHEDULE_SUBJECT = "subject";
    private static final String SCHEDULE_TYPE = "type";
    private static final String SCHEDULE_SEMESTER = "semester";
    private static final String SCHEDULE_TIME_FROM = "time_from";
    private static final String SCHEDULE_TIME_TO = "time_to";


    private static final String TABLE_INFO = "info";
    private static final String INFO_TEXT = "key";
    private static final String INFO_FONT_SIZE = "font_size";
    private static final String INFO_FONT_STYLE = "font_style";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        db.execSQL("CREATE TABLE " + TABLE_FLOORS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FLOOR_NAME + " TEXT, " +
                FLOOR_IMAGE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ROOMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ROOM_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SCHEDULE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCHEDULE_SUBJECT + " TEXT, " +
                SCHEDULE_TYPE + " TEXT, " +
                SCHEDULE_SEMESTER + " INTEGER, " +
                SCHEDULE_TIME_FROM + " TEXT, " +
                SCHEDULE_TIME_TO + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_INFO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                INFO_TEXT + " TEXT, " +
                INFO_FONT_STYLE + " TEXT, " +
                INFO_FONT_SIZE + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
        onCreate(db);
    }

    public void refreshEvents(DataLoadCallback callback) {
        new FetchEventsTask(this, callback).execute();
    }

    public void refreshFloors(FloorLoadCallback callback) {
        new FetchFloorsTask(this, callback).execute();
    }

    public void loadInfoItemsFromServerAndSaveToDB(SQLiteDatabase db) {
        try {
            URL url = new URL(INFO_JSON_URL);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                Iterator<String> keys = jsonObject.keys();

                db.beginTransaction();

                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject valueObject = jsonObject.getJSONObject(key);
                    int fontSize = valueObject.optInt("font_size", 16);
                    String fontStyle = valueObject.optString("font_style", null);
                    Log.d("DBHelper", "Kontrolujem/vkladám info item: " + key + " " + fontSize + " " + fontStyle);

                    // Skontrolujeme, či záznam s daným kľúčom už existuje
                    Cursor cursor = db.query(
                            "info",
                            new String[]{"key"},
                            "key = ?",
                            new String[]{key},
                            null,
                            null,
                            null
                    );

                    if (cursor != null && cursor.getCount() == 0) {
                        // Záznam neexistuje, môžeme ho vložiť
                        ContentValues values = new ContentValues();
                        values.put("key", key);
                        values.put("font_size", fontSize);
                        values.put("font_style", fontStyle);

                        db.insert("info", null, values);
                        Log.d("DBHelper", "Pridaný info item: " + key);
                    } else {
                        Log.d("DBHelper", "Info item s kľúčom " + key + " už existuje, nepridávam.");
                    }

                    if (cursor != null) {
                        cursor.close();
                    }
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshInfoFromServer() {
        new Thread(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            loadInfoItemsFromServerAndSaveToDB(db);
        }).start();
    }
    public List<InfoItem> getAllInfoItems() {
        List<InfoItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("info", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndexOrThrow("key"));
                int fontSize = cursor.getInt(cursor.getColumnIndexOrThrow("font_size"));
                String fontStyle = cursor.getString(cursor.getColumnIndexOrThrow("font_style"));
                list.add(new InfoItem(key, fontSize, fontStyle));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }



    private static class FetchFloorsTask extends AsyncTask<Void, Void, Boolean> {
        private DBHelper dbHelper;
        private FloorLoadCallback callback;

        FetchFloorsTask(DBHelper dbHelper, FloorLoadCallback callback) {
            this.dbHelper = dbHelper;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(FLOORS_JSON_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonResponse = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }

                JSONArray floorsArray = new JSONArray(jsonResponse.toString());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();

                db.delete("floors", null, null);
                db.delete("rooms", null, null);
                db.delete("schedule", null, null);

                for (int i = 0; i < floorsArray.length(); i++) {
                    JSONObject floorObj = floorsArray.getJSONObject(i);
                    String floorName = floorObj.getString("name");
                    String imageRes = floorObj.getString("imageResource");

                    ContentValues floorValues = new ContentValues();
                    floorValues.put("name", floorName);
                    floorValues.put("imageResource", imageRes);
                    long floorId = db.insert("floors", null, floorValues);

                    JSONArray roomsArray = floorObj.getJSONArray("rooms");
                    for (int j = 0; j < roomsArray.length(); j++) {
                        JSONObject roomObj = roomsArray.getJSONObject(j);
                        String roomName = roomObj.getString("name");

                        ContentValues roomValues = new ContentValues();
                        roomValues.put("name", roomName);
                        long roomId = db.insert("rooms", null, roomValues);

                        JSONArray scheduleArray = roomObj.getJSONArray("scheduleHours");
                        for (int k = 0; k < scheduleArray.length(); k++) {
                            JSONObject sched = scheduleArray.getJSONObject(k);

                            ContentValues schedValues = new ContentValues();
                            schedValues.put("subject", sched.getString("subject"));
                            schedValues.put("type", sched.getString("type"));
                            schedValues.put("semester", sched.getInt("semester"));
                            schedValues.put("time_from", sched.getString("timeFrom"));
                            schedValues.put("time_to", sched.getString("timeTo"));
                            db.insert("schedule", null, schedValues);
                        }
                    }
                }

                db.setTransactionSuccessful();
                return true;

            } catch (IOException | JSONException e) {
                Log.e("DBHelper", "Error loading floors", e);
                return false;

            } finally {
                if (connection != null) connection.disconnect();
                if (reader != null) {
                    try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
                }
                dbHelper.getWritableDatabase().endTransaction();
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            callback.onFloorsLoaded(success);
        }
    }

    private static class FetchEventsTask extends AsyncTask<Void, Void, List<Event>> {
        private DBHelper dbHelper;
        private DataLoadCallback callback;

        FetchEventsTask(DBHelper dbHelper, DataLoadCallback callback) {
            this.dbHelper = dbHelper;
            this.callback = callback;
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            List<Event> events = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(JSON_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonResponse = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }

                JSONArray jsonArray = new JSONArray(jsonResponse.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject eventObj = jsonArray.getJSONObject(i);
                    events.add(new Event(
                            i,
                            eventObj.getString("title"),
                            eventObj.getString("description"),
                            eventObj.getString("date"),
                            eventObj.getString("time_from"),
                            eventObj.getString("time_to")
                    ));
                }
            } catch (IOException | JSONException e) {
                Log.e("DBHelper", "Error loading events", e);
                return null;
            } finally {
                if (connection != null) connection.disconnect();
                if (reader != null) {
                    try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
                }
            }
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            if (events != null) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    db.beginTransaction();
                    db.delete(TABLE_EVENTS, null, null);
                    for (Event event : events) {
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_TITLE, event.getTitle());
                        values.put(COLUMN_DESCRIPTION, event.getDescription());
                        values.put(COLUMN_DATE, event.getDate());
                        values.put(COLUMN_TIME_FROM, event.getTimeFrom());
                        values.put(COLUMN_TIME_TO, event.getTimeTo());
                        db.insert(TABLE_EVENTS, null, values);
                    }
                    db.setTransactionSuccessful();
                    callback.onDataLoaded(true, events);
                } finally {
                    db.endTransaction();
                }
            } else {
                callback.onDataLoaded(false, null);
            }
        }
    }
    public interface FloorLoadCallback {
        void onFloorsLoaded(boolean success);
    }
    public interface DataLoadCallback {
        void onDataLoaded(boolean success, List<Event> events);
    }
}