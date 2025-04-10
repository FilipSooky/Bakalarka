package com.example.languageguide.utils.locations;
import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.languageguide.utils.Utils;

import java.util.List;
public class Floor {
    private String name;
    private String imageResource;
    private List<Room> rooms;

    public Floor(String name, String imageResource, List<Room> rooms) {
        this.name = name;
        this.imageResource = imageResource;
        this.rooms = rooms;
    }
    public String getName(Context context) {
        return Utils.getTranslatedString(context, name);
    }

    public String getImageResource() { return imageResource; }
    public List<Room> getRooms() { return rooms; }
}

