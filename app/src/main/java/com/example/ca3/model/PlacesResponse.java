
package com.example.ca3.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlacesResponse {
    @SerializedName("results")
    private List<Place> results;

    @SerializedName("status")
    private String status;

    @SerializedName("html_attributions")
    private List<String> htmlAttributions;

    @SerializedName("error_message")
    private String errorMessage;

    public List<Place> getResults() { return results; }
    public void setResults(List<Place> results) { this.results = results; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class Place {
        @SerializedName("name")
        private String name;

        @SerializedName("vicinity")
        private String vicinity;

        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("types")
        private List<String> types;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVicinity() { return vicinity; }
        public void setVicinity(String vicinity) { this.vicinity = vicinity; }

        public Geometry getGeometry() { return geometry; }
        public void setGeometry(Geometry geometry) { this.geometry = geometry; }

        public List<String> getTypes() { return types; }
        public void setTypes(List<String> types) { this.types = types; }
    }

    public static class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }
}
