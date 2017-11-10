package net.codejack.autoamp2.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Degausser on 7/11/2017.
 */

public class Lesson implements Comparable {

    private Date date;
    private Student student;
    private Date dates;
    private boolean open = true;

    public Lesson() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Date getDates() {
        return dates;
    }

    public void setDates(Date dates) {
        this.dates = dates;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Lesson lesson = (Lesson) o;
        return this.getDate().compareTo(lesson.getDate());
    }



}
