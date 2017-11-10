package net.codejack.autoamp2.data;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Degausser on 7/10/2017.
 */

public class DayPlan implements Comparable<DayPlan> {

    private String start_minute;
    private String start_hour;
    private String end_minute;
    private String end_hour;
    private String duration;

    public DayPlan() {

    }

    public DayPlan(String hour, String minute, int duration) {
        set(hour, minute, duration);
    }

    public void set(String hour, String minute, int duration) {
        this.duration = "" + duration;
        this.start_hour = hour;
        this.start_minute = minute;

        int int_hour = Integer.parseInt(hour);
        int int_minute = Integer.parseInt(minute);
        int hours = (duration / 60);
        int minutes = (duration % 60);

        int_hour += hours;
        int_minute += minutes;
        if (int_minute >= 60) {
            int_minute = (minutes % 60);
            int_hour++;
        }
        if (int_hour >= 24) {
            int_hour = (hours % 24);
        }

        this.end_hour = String.format(Locale.ENGLISH, "%02d", int_hour);
        this.end_minute = String.format(Locale.ENGLISH, "%02d", int_minute);
    }

    public String getStart_minute() {
        return start_minute;
    }

    public String getStart_hour() {
        return start_hour;
    }

    public String getEnd_minute() {
        return end_minute;
    }

    public String getEnd_hour() {
        return end_hour;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public int compareTo(@NonNull DayPlan o) {
        int this_hour = Integer.parseInt(start_hour);
        int this_minute = Integer.parseInt(start_minute);

        int other_hour = Integer.parseInt(o.getStart_hour());
        int other_minute = Integer.parseInt(o.getStart_minute());

        if (this_hour == other_hour && this_minute == other_minute) {
            if (this_minute < other_minute) return -1;
            else if (this_minute > other_minute) return 1;
            return 0;
        }
        if (this_hour < other_hour) return -1;
        return 1;
    }

    @Override
    public String toString() {
        return start_hour + ":" + start_minute;
    }

}
