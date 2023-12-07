package com.example.mymeteo;

public class ForecastItem {
    private String day;
    private String icon;
    private double temperature;

    // Constructor
    public ForecastItem(String day, String icon, double temperature) {
        this.day = day;
        this.icon = icon;
        this.temperature = temperature;
    }

    // Getters
    public String getDay() {
        return day;
    }

    public String getIcon() {
        return icon;
    }

    public double getTemperature() {
        return temperature;
    }
}

