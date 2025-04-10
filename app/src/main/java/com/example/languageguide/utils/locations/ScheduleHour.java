package com.example.languageguide.utils.locations;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.example.languageguide.R;
import com.example.languageguide.utils.Utils;

public class ScheduleHour {
    private String subject;
    private String type;
    private int semester;
    private String timeFrom;
    private String timeTo;

    public ScheduleHour(String subject, String type, int semester, String timeFrom, String timeTo) {
        this.subject = subject;
        this.type = type;
        this.semester = semester;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public String getSubject(Context context) {
        return Utils.getTranslatedString(context, subject);
    }
    public String getType(Context context) {
        return Utils.getTranslatedString(context, type);
    }
    public int getSemester() { return semester; }
    public String getTimeFrom() { return timeFrom; }
    public String getTimeTo() { return timeTo; }
}


