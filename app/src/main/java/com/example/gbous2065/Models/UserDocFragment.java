package com.example.gbous2065.Models;

public class UserDocFragment {
    private int id;
    private String title;
    private String dateEnd;
    private String status;
    private String dateStatus;

    public int getId() {
        return id;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(String dateStatus) {
        this.dateStatus = dateStatus;
    }
}
