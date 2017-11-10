package net.codejack.autoamp2.data;

import net.codejack.autoamp2.collections.Days;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Degausser on 7/7/2017.
 */

public class Schedule {

    private List<Days> schedule;

    public Schedule() {
        schedule = new LinkedList<>();
    }

    public List<Days> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Days> schedule) {
        this.schedule = schedule;
    }
}
