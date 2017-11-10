package net.codejack.autoamp2.collections;

import android.util.Log;

import net.codejack.autoamp2.data.DayPlan;
import net.codejack.autoamp2.data.Reservations;
import net.codejack.autoamp2.data.Schedule;
import net.codejack.autoamp2.helpers.StringStuff;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Created by Rome07 on 11-07-17.
 */

public class ReservationsManager {

    private Schedule schedule;
    private HashMap<String, Reservations> list;

    public ReservationsManager() {
        list = new HashMap<>();
        schedule = new Schedule();
    }

    public void putSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void putReservationsList(HashMap<String, Reservations> map) {
        list = map;
    }

    public HashMap<String, Reservations> getAll() {
        return list;
    }

    public void setToUpload() {
        schedule = null;
    }

    public void mergeWith() {
        // make registration list from schedule
        for (int i = 0; i < schedule.getSchedule().size(); i++) {

            // dates and stuff :E
            Days days = schedule.getSchedule().get(i);
            if (validDay(days)) {
                List<DayPlan> dp = days.getPlan();
                int year = days.getDate_year();
                int month = days.getDate_month();
                int day = days.getDate_day();

                for (int j = 0; j < dp.size(); j++) {
                    int hour = Integer.parseInt(dp.get(j).getStart_hour());
                    int minute = Integer.parseInt(dp.get(j).getStart_minute());
                    Date d = StringStuff.timeToDateMinute(year, month, day, hour, minute);
                    String dt = StringStuff.dateToStringMinute(d);

                    Reservations r = new Reservations();
                    r.setDatetime(d);
                    if (list.containsKey(dt)) {
                        Reservations key = list.get(dt);
                        if (key.getUserID() != null) {
                            String user = key.getUserID();
                            r.setUserID(user);
                            list.put(dt, r);
                        }
                    }
                    else {
                        list.put(dt, r);
                    }
                }
            }
            else {

            }

        }
    }

    private boolean validDay(Days days) {
        int year = days.getDate_year();
        int month = days.getDate_month();
        int day = days.getDate_day();

        Date d = getDate(year, month, day);
        Date now = StringStuff.midnight();

        return d.compareTo(now) >= 0;
    }

    private Date getDate(int year, int month, int day) {
        Date d = new GregorianCalendar(year, month, day).getTime();
        return d;
    }

}
