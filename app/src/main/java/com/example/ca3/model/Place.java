package com.example.ca3.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Place {
    private String name;
    private String vicinity;
    private PlacesResponse.Geometry geometry;
    private List<String> types;

    public Place() {
    }

    public Place(String name, String vicinity, PlacesResponse.Geometry geometry, List<String> types) {
        this.name = name;
        this.vicinity = vicinity;
        this.geometry = geometry;
        this.types = types;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVicinity() { return vicinity; }
    public void setVicinity(String vicinity) { this.vicinity = vicinity; }

    public PlacesResponse.Geometry getGeometry() { return geometry; }
    public void setGeometry(PlacesResponse.Geometry geometry) { this.geometry = geometry; }

    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }
}