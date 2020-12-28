package com.example.gbous2065.Models;

public class LessonInfo {
    private String name;
    private String room;

    public LessonInfo() {}

    public LessonInfo(String name, String room) {
        this.name = name;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
