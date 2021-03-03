package com.example.gbous2065.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeDifference {
    public static Boolean startLaterThanEnd(String startTimeStr, String endTimeStr, String mode) {

        String startTimeStrT =startTimeStr;
        String endTimeStrT = endTimeStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(mode.equals("adapter")) {
            startTimeStrT += " 00:00:00";
            endTimeStrT += " 00:00:00";
        }

        try {

            LocalDateTime startTime = LocalDateTime.parse(startTimeStrT,
                    formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStrT, formatter);

            Duration d = Duration.between(startTime, endTime);

            System.out.println("dur " + d.getSeconds());
            if (d.getSeconds() == 0)
                return false;
            else if (d.getSeconds() > 0)
                return false;
            else
                return true;

        } catch (DateTimeParseException e) {
            System.out.println("Invalid Input" + e.getMessage());

        }
        return false;
    }
}
