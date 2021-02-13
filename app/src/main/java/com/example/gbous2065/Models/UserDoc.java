package com.example.gbous2065.Models;

import com.google.gson.annotations.SerializedName;

public class UserDoc {
    @SerializedName("doc_id")
    private int id;
    private String title;
    @SerializedName("doc_type")
    private String type;
    @SerializedName("doc_subject")
    private String subject;
    @SerializedName("doc_functionality")
    private String functionality;
    @SerializedName("doc_places")
    private String places;
    @SerializedName("date_end")
    private String endDate;
    @SerializedName("is_subscribe")
    private Integer isSubscribe;
    @SerializedName("is_unsubscribe")
    private Integer isUnsubscribe;
    private String file;

    public UserDoc(){}

    public Integer getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(Integer isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public Integer getIsUnsubscribe() {
        return isUnsubscribe;
    }

    public void setIsUnsubscribe(Integer isUnsubscribe) {
        this.isUnsubscribe = isUnsubscribe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
