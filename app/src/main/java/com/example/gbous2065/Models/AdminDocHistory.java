package com.example.gbous2065.Models;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdminDocHistory{
    @SerializedName("doc_id")
    private int id;
    private String date;
    private Date dateReal;
    private String title;
    @SerializedName("doc_type")
    private String type;
    @SerializedName("doc_subject")
    private String subject;
    @SerializedName("doc_functionality")
    private String functionality;
    @SerializedName("doc_places")
    private String places;
    @SerializedName("doc_users")
    private String receivers;
    @SerializedName("stat_is_subscribe")
    private String numOfSub;
    @SerializedName("stat_is_unsubscribe")
    private String numOfUnsub;
    private Boolean expanded;

    public AdminDocHistory() {
        expanded = false;
    }

    public Date getDateReal() {
        return dateReal;
    }

    public void setDateReal(Date dateReal) {
        this.dateReal = dateReal;
    }

    public Boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFunctionality() {
        return functionality;
    }

    public void setFunctionality(String functionality) {
        this.functionality = functionality;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getNumOfSub() {
        return numOfSub;
    }

    public void setNumOfSub(String numOfSub) {
        this.numOfSub = numOfSub;
    }

    public String getNumOfUnsub() {
        return numOfUnsub;
    }

    public void setNumOfUnsub(String numOfUnsub) {
        this.numOfUnsub = numOfUnsub;
    }

//    public static final Comparator<AdminDocHistory> compareByDate = new Comparator<AdminDocHistory>() {
//        @Override
//        public int compare(AdminDocHistory o1, AdminDocHistory o2) {

//            String[] dateMas1 = o1.getDate().split("-");
//            Integer day1 = Integer.parseInt(dateMas1[0]);
//            Integer month1 = Integer.parseInt(dateMas1[1]);
//            Integer year1 = Integer.parseInt(dateMas1[2]);
//
//            String[] dateMas2 = o1.getDate().split("-");
//            Integer day2 = Integer.parseInt(dateMas2[0]);
//            Integer month2 = Integer.parseInt(dateMas2[1]);
//            Integer year2 = Integer.parseInt(dateMas2[2]);
//
//            GregorianCalendar calendar1, calendar2;
//            calendar1 = new GregorianCalendar(year1, month1-1, day1);
//            Date date1 = calendar1.getTime();
//            calendar2 = new GregorianCalendar(year2, month2-1, day2);
//            Date date2 = calendar2.getTime();
//
//            return date1.compareTo(date2);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
//            Date date1 = null, date2 = null;
//
//            try {
//                date1 = sdf.parse(o1.getDate() + " 00:00:00");
//                date2 = sdf.parse(o2.getDate() + " 00:00:00");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            int tmp = date1.compareTo(date2);
//            return tmp;
//        }
//    };
}
