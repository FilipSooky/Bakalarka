package com.example.languageguide.utils;

import com.example.languageguide.utils.locations.Floor;
import com.example.languageguide.utils.locations.Room;
import com.example.languageguide.utils.locations.ScheduleHour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class FloorDBManager {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public FloorDBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void refreshFloors(DBHelper.FloorLoadCallback callback) {
        dbHelper.refreshFloors(callback);
    }

    public List<Floor> getAllFloors() {
        List<Floor> floors = new ArrayList<>();

        Cursor floorCursor = db.query("floors", null, null, null, null, null, null);
        while (floorCursor.moveToNext()) {
            String floorName = floorCursor.getString(floorCursor.getColumnIndexOrThrow("name"));
            String imageRes = floorCursor.getString(floorCursor.getColumnIndexOrThrow("imageResource"));

            List<Room> rooms = new ArrayList<>();
            Cursor roomCursor = db.query("rooms", null, null, null, null, null, null);
            while (roomCursor.moveToNext()) {
                String roomName = roomCursor.getString(roomCursor.getColumnIndexOrThrow("name"));

                List<ScheduleHour> scheduleHours = new ArrayList<>();
                Cursor schedCursor = db.query("schedule", null, null, null, null, null, null);
                while (schedCursor.moveToNext()) {
                    scheduleHours.add(new ScheduleHour(
                            schedCursor.getString(schedCursor.getColumnIndexOrThrow("subject")),
                            schedCursor.getString(schedCursor.getColumnIndexOrThrow("type")),
                            schedCursor.getInt(schedCursor.getColumnIndexOrThrow("semester")),
                            schedCursor.getString(schedCursor.getColumnIndexOrThrow("time_from")),
                            schedCursor.getString(schedCursor.getColumnIndexOrThrow("time_to"))
                    ));
                }
                schedCursor.close();

                rooms.add(new Room(roomName, scheduleHours));
            }
            roomCursor.close();

            floors.add(new Floor(floorName, imageRes, rooms));
        }
        floorCursor.close();

        return floors;
    }
}