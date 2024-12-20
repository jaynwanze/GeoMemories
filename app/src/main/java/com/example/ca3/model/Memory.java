package com.example.ca3.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Memory {
    private String id;
    private String photoUrl;
    private String title;
    private String description;
    private GeoPoint location;
    private Timestamp  timestamp;
    private String weatherInfo;
    private List<Place> places;
    private String userId;

    public Memory() {
    }

    public Memory(String id,String photoUrl,String title, String description, GeoPoint location, Timestamp timestamp, String weatherInfo,List<Place> places, String userId) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.title = title;
        this.description = description;
        this.location = location;
        this.timestamp = timestamp;
        this.weatherInfo = weatherInfo;
        this.userId = userId;
        this.places = places;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

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

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) { this.userId = userId; }
}
