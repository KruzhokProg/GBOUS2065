package com.example.gbous2065.Models;

public class ContactMapCoordinates {
    private float latitude;
    private float longitude;
    private String adress;
    private String type;

    public ContactMapCoordinates() {
    }

    public ContactMapCoordinates(float latitude, float longitude, String adress, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
