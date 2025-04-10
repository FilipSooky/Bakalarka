package com.example.languageguide.utils.locations;
import java.util.List;

public class Room {
    private String name;
    private List<ScheduleHour> scheduleHours;

    public Room(String name, List<ScheduleHour> scheduleHours) {
        this.name = name;
        this.scheduleHours = scheduleHours;
    }

    public String getName() { return name; }
    public List<ScheduleHour> getScheduleHours() { return scheduleHours; }

    public Floor getFloor(List<Floor> floors) {
        for (Floor floor : floors) {
            for (Room room : floor.getRooms()) {
                if (room.getName().equals(this.name)) {
                    return floor;
                }
            }
        }
        return null;
    }
}

