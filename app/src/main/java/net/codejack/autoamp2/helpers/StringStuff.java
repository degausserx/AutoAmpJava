package net.codejack.autoamp2.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Degausser on 7/8/2017.
 */

public class StringStuff {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static Date midnight() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static Date timeToDate(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date timeToDateMinute(int year, int month, int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date stringToDate(String string) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = null;
        try {
            d = df.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Date stringToDateDay(String string) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = df.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Date endOfDay() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 55);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static String dateToString(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String reportDate = df.format(d);
        return reportDate;
    }

    public static String dateToStringMinute(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String reportDate = df.format(d);
        return reportDate;
    }

    public static String dateToStringHourOnly(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String reportDate = df.format(d);
        return reportDate;
    }

    public static String dateToStringMidnight(Date d) {
        Date date = midnightPerfect(d);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String reportDate = df.format(date);
        return reportDate;
    }

    public static String dateToStringBeforeMidnight(Date d) {
        Date date = midnightWith(d);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String reportDate = df.format(date);
        return reportDate;
    }

    public static Date midnightFrom(String s) {
        Date d = stringToDate(s);
        return midnightWith(d);
    }

    public static Date midnightFromPerfect(String s) {
        Date d = stringToDate(s);
        return midnightPerfect(d);
    }

    public static Date endOfDayFrom(String s) {
        Date d = stringToDate(s);
        return endOfDayWith(d);
    }

    public static Date midnightWith(Date d) {
        Calendar date = new GregorianCalendar();
        date.setTime(d);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 001);
        date.set(Calendar.MINUTE, -1);
        return date.getTime();
    }

    public static Date endOfDayWith(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date midnightPerfect(Date d) {
        Calendar date = new GregorianCalendar();
        date.setTime(d);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 000);
        return date.getTime();
    }

}
