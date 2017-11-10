package net.codejack.autoamp2.data;

import net.codejack.autoamp2.helpers.StringStuff;

import java.util.Date;

/**
 * Created by Degausser on 7/7/2017.
 */

public class Reservations {

    private Date datetime;
    private String UserID;
    private Student student;

    public Reservations() {
        UserID = "NULL";
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getUserID() {
        return (UserID == null) ? "NULL": UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String toString() {
        return StringStuff.dateToStringMinute(datetime);
    }

    public void setTimeExtended(String s) {
        datetime = StringStuff.stringToDate(s);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
