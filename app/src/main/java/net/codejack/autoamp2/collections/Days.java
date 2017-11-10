package net.codejack.autoamp2.collections;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.codejack.autoamp2.data.DayPlan;

import java.util.List;

/**
 * Created by Rome07 on 10-07-17.
 */

public class Days implements Comparable<Days> {

    private List<DayPlan> plan;
    private int date_day;
    private int date_month;
    private int date_year;
    private String name;
    private String color;
    private boolean safe = false;

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    public Days() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("schedule");
    }

    @Override
    public int compareTo(@NonNull Days o) {
        if (date_year == o.getDate_year()) {
            if (date_month == o.getDate_month()) {
                if (date_day == o.getDate_day()) return 0;
                if (date_day < o.getDate_day()) return -1;
                return 1;
            }
            else if (date_month < o.getDate_month()) return -1;
            return 1;
        }
        else if (date_year < o.getDate_year()) return -1;
        return 1;
    }

    public List<DayPlan> getPlan() {
        return plan;
    }

    public void setPlan(List<DayPlan> plan) {
        this.plan = plan;
    }

    public int getDate_day() {
        return date_day;
    }

    public void setDate_day(int date_day) {
        this.date_day = date_day;
    }

    public int getDate_month() {
        return date_month;
    }

    public void setDate_month(int date_month) {
        this.date_month = date_month;
    }

    public int getDate_year() {
        return date_year;
    }

    public void setDate_year(int date_year) {
        this.date_year = date_year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public void update() {
        mDatabase.child("days").child(name).setValue(this);
    }

    public String toString() {
        if (!safe) return name;
        else return "" + hashCode();
    }

    public boolean equals(Days obj) {
        String thisObject = date_year + ":" + date_month + ":" + date_day;
        Log.wtf("OBJ", date_year + ":" + date_month + ":" + date_day);
        String thatObject = obj.getDate_year() + ":" + obj.getDate_month() + ":" + obj.getDate_day();
        if (thisObject.equals(thatObject)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        String month = "" + date_month;
        if (month.length() < 2) month = "0" + month;
        String day = "" + date_day;
        if (day.length() < 2) day = "0" + day;
        String fin = "" + date_year + month + day;
        return Integer.parseInt(fin);
    }
}
