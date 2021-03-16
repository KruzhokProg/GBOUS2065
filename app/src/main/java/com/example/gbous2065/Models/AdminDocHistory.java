package com.example.gbous2065.Models;

import com.google.gson.annotations.SerializedName;

public class AdminDocHistory {
    @SerializedName("doc_id")
    private int id;
    private String date;
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
}
