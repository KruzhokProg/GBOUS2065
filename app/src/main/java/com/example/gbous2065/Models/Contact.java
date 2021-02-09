package com.example.gbous2065.Models;

public class Contact {

    private String position;
    private String name;
    private String email;
    private String phone;
    private String adress;
    private Boolean expanded;

    public Contact(){

    }

    public Contact(String position, String name, String email, String phone, String adress) {
        this.position = position;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.adress = adress;
        expanded = false;
    }

    public boolean isExpanded(){
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
