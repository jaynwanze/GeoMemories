package com.example.ca3.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Place {
    @SerializedName("name")
    private String name;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("types")
    private List<String> types;
    private String mapsUrl; // New field for Google Maps URL

    // Constructors
    public Place() {}

    public Place(String name, String vicinity, Geometry geometry, List<String> types) {
        this.name = name;
        this.vicinity = vicinity;
        this.geometry = geometry;
        this.types = types;
        this.mapsUrl = generateGoogleMapsUrl();
    }

    // Getters and Setters

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.mapsUrl = generateGoogleMapsUrl();
    }

    public String getVicinity() { return vicinity; }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
        this.mapsUrl = generateGoogleMapsUrl();
    }

    public Geometry getGeometry() { return geometry; }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        this.mapsUrl = generateGoogleMapsUrl();
    }

    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }

    public String getMapsUrl() { return mapsUrl; }

    // Helper method to generate Google Maps URL
    private String generateGoogleMapsUrl() {
        if (geometry != null && geometry.getLocation() != null) {
            double lat = geometry.getLocation().getLat();
            double lng = geometry.getLocation().getLng();
            String encodedName = Uri.encode(name);
            return "https://www.google.com/maps/search/?api=1&query=" + encodedName + "+" + lat + "," + lng;
        }
        return null;
    }

    // Nested Geometry and Location Classes
    public static class Geometry {
        @SerializedName("location")
        private Location location;

        // Getters and Setters
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        // Getters and Setters
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }
}
