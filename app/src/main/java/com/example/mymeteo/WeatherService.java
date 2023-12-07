package com.example.mymeteo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private static final String API_KEY = "163ac7cdcabae8a682f45e541d26b01e";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static String getWeatherData(String city) {
        try {
            URL url = new URL(BASE_URL + "?q=" + city + "&mode=json&appid=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
