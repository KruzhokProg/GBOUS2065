package com.example.gbous2065.Models;

import java.util.List;

public class ScheduleByBuilding {
    private String building;
    private List<Schedule> scheduleList;

    public ScheduleByBuilding(String building, List<Schedule> scheduleList) {
        this.building = building;
        this.scheduleList = scheduleList;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
