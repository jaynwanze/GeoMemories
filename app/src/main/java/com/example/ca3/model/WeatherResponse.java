package com.example.ca3.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("main")
    private Main main;
    @SerializedName("dt")
    private long timestamp;

    // Getters and Setters

    public List<Weather> getWeather() { return weather; }
    public void setWeather(List<Weather> weather) { this.weather = weather; }

    public Main getMain() { return main; }
    public void setMain(Main main) { this.main = main; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        public double getTemp() { return temp; }
        public void setTemp(double temp) { this.temp = temp; }
    }
}
