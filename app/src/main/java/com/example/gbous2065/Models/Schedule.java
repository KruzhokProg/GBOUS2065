package com.example.gbous2065.Models;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String grade;
    private String letter;
    private List<Weekdays> weekdays;

    public Schedule() {
        weekdays = new ArrayList<>();
    }

    public List<Weekdays> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Weekdays> weekdays) {
        this.weekdays = weekdays;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }


}
