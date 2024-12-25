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

    // Getters and Setters

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
}
