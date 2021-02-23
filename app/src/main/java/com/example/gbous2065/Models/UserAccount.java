package com.example.gbous2065.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAccount {
    @SerializedName("id_original")
    private String id;
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String place;
    private String functionality;
    private Map<String, UserDoc>[] docs;
    @SerializedName("docs_history")
    private Map<String, UserDocHistory>[] docsHistory;
    private String error;

    public UserAccount(){
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, UserDocHistory>[] getDocsHistory() {
        return docsHistory;
    }

    public void setDocsHistory(Map<String, UserDocHistory>[] docsHistory) {
        this.docsHistory = docsHistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getFunctionality() {
        return functionality;
    }

    public void setFunctionality(String functionality) {
        this.functionality = functionality;
    }

    public Map<String, UserDoc>[] getDocs() {
        return docs;
    }

    public void setDocs(Map<String, UserDoc>[] docs) {
        this.docs = docs;
    }
}
