package com.example.mymeteo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<TimeFrame> timeFrames;

    public List<TimeFrame> getTimeFrames() {
        return timeFrames;
    }

    public static class TimeFrame {

        @SerializedName("dt_txt")
        private String dateTime;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        public String getDateTime() {
            return dateTime;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }
    }

    public static class Main {

        @SerializedName("temp")
        private double temperature;

        public double getTemperature() {
            return temperature;
        }
    }

    public static class Weather {

        @SerializedName("icon")
        private String iconCode;

        public String getIconCode() {
            return iconCode;
        }
    }
}

