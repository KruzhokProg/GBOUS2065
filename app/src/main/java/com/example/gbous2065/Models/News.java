package com.example.gbous2065.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable {
    private int id;
    private String publish_date;
    private String name;
    private String anons;
    private String content;

    public News(){}

    public News(String published_date, String name, String anons, String content) {
        this.publish_date = published_date;
        this.name = name;
        this.anons = anons;
        this.content = content;
    }

    protected News(Parcel in) {
        id = in.readInt();
        publish_date = in.readString();
        name = in.readString();
        anons = in.readString();
        content = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublished_date() {
        return publish_date;
    }

    public void setPublished_date(String published_date) {
        this.publish_date = published_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnons() {
        return anons;
    }

    public void setAnons(String anons) {
        this.anons = anons;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(publish_date);
        dest.writeString(name);
        dest.writeString(anons);
        dest.writeString(content);
    }
}
