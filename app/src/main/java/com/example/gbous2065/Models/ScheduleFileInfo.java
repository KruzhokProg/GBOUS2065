package com.example.gbous2065.Models;

public class ScheduleFileInfo {
    private String building;
    private String url;

    public ScheduleFileInfo(String building, String url) {
        this.building = building;
        this.url = url;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
