package com.example.gbous2065.Models;

import java.util.ArrayList;
import java.util.List;

public class Weekdays {
    private String weekday;
    private List<LessonInfo> lessons;

    public Weekdays() {
        lessons = new ArrayList<>();
    }

    public List<LessonInfo> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonInfo> lessons) {
        this.lessons = lessons;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

}
