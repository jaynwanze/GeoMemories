package com.example.ca3.model;

import com.google.firebase.firestore.GeoPoint;

public class Memory {
    private String id;
    private String photoUrl;
    private String description;
    private GeoPoint location;
    private long timestamp;
    private String weatherInfo;

    public Memory() {
        // Default constructor required for calls to DataSnapshot.getValue(Memory.class)
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) { this.weatherInfo = weatherInfo; }
}
