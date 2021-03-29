package com.example.gbous2065.Models;

public class NotificationSender {
    private String to;

    private Notification notification;

    public NotificationSender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public NotificationSender() {
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
