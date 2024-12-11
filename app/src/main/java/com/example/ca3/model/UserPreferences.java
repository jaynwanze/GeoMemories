package com.example.ca3.model;

public class UserPreferences {
    private String mapType; // e.g., satellite, terrain, hybrid, normal
    private String galleryDisplay; // e.g., grid, timeline
    private String userId; // User ID

    public UserPreferences() {
        // Default constructor
    }

    // Getters and Setters

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) { this.mapType = mapType; }

    public String getGalleryDisplay() {
        return galleryDisplay;
    }

    public void setGalleryDisplay(String galleryDisplay) { this.galleryDisplay = galleryDisplay;
    }

    public String getUserId() {
        return userId;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
