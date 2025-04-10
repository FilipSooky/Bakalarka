package com.example.languageguide.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.languageguide.R;
import com.example.languageguide.utils.locations.Floor;
import com.example.languageguide.utils.locations.Room;
import com.example.languageguide.utils.locations.ScheduleHour;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Retrieves the translated string for the given key.
     * If the key does not exist in the resources, the key itself is returned as a fallback.
     *
     * @param context The context to access resources.
     * @param key     The key to look up in the string resources.
     * @return The translated string or the key itself if not found.
     */
    public static String getTranslatedString(Context context, String key) {
        // Get the resource ID for the key
        int resourceId = context.getResources().getIdentifier(key, "string", context.getPackageName());
        return (resourceId != 0) ? context.getString(resourceId) : key;
    }

    public static List<Floor> loadFloorsFromJson(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("floors.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Floor>>() {}.getType();
            return gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void showScheduleDialog(Context context, Room room, Floor floor) {
        StringBuilder schedule = new StringBuilder();
        for (ScheduleHour hour : room.getScheduleHours()) {
            schedule.append(hour.getSubject(context))
                    .append(" (")
                    .append(hour.getType(context))
                    .append(") - ")
                    .append(hour.getTimeFrom())
                    .append(" - ")
                    .append(hour.getTimeTo())
                    .append("\n");
        }

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.subjects_in_room) + " " + room.getName())
                .setMessage(schedule.toString().trim())
                .setPositiveButton(context.getString(R.string.ok), null)
                .show();
    }

    public static void showNoRoomsDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.not_found))
                .setMessage(context.getString(R.string.no_rooms_for_selected_semester))
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}