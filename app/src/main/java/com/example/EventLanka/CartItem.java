package com.example.EventLanka;

public class CartItem {
    private String eventName;
    private String eventDate;
    private String eventTime;

    public CartItem(String eventName, String eventDate, String eventTime) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
}
