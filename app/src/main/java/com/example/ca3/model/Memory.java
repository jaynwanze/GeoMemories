package com.example.ca3.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

public class Memory {
    private String id;
    private String photoUrl;
    private String description;
    private GeoPoint location;
    private Timestamp  timestamp;
    private String weatherInfo;
    private String userId;

    public Memory() {
    }

    public Memory(String id, String photoUrl, String description, GeoPoint location, Timestamp timestamp, String weatherInfo, String userId) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.description = description;
        this.location = location;
        this.timestamp = timestamp;
        this.weatherInfo = weatherInfo;
        this.userId = userId;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) { this.location = location; }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) { this.weatherInfo = weatherInfo; }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) { this.userId = userId; }
}
