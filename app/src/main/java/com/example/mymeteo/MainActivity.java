package com.example.mymeteo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private myAdapter adapter;
    private ArrayList<ForecastItem> forecastList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText searchCity = findViewById(R.id.search_city);
        recyclerView =findViewById(R.id.my_rv);
        forecastList = new ArrayList<>();
        adapter = new myAdapter(this, forecastList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);


        searchCity.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform search when the "Done" button is pressed
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        // Fetch and display weather for Tunis on app launch
        new FetchWeatherTask().execute("Rades");
        new FetchForecastTask().execute("Rades");

    }

    private void performSearch() {
        EditText searchCity = findViewById(R.id.search_city);
        String searchTerm = searchCity.getText().toString().trim();

        if (!searchTerm.isEmpty()) {
            new FetchWeatherTask().execute(searchTerm);
            new FetchForecastTask().execute(searchTerm);
            searchCity.getText().clear();

        } else {
            Toast toast = Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0); // Center the toast
            toast.show();
        }
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            return WeatherService.getWeatherData(city);

        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse the JSON response and update UI
                updateUI(result);
            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0); // Center the toast
                toast.show();
            }
        }

    }
    private class FetchForecastTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            return ForecastService.getForecastData(city);
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse the JSON response and update RecyclerView for forecast
                updateForecast(result);
            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Failed to fetch forecast data", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0); // Center the toast
                toast.show();
            }
        }
    }

    private void updateUI(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Extract required information from jsonObject
            String cityName = jsonObject.getString("name");

            // Temperature formatting (convert from Kelvin to Celsius)
            double temperatureKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            double temperatureCelsius = temperatureKelvin - 273.15;
            String formattedTemperature = String.format("%.0f°C", temperatureCelsius);

            // Date formatting
            long timestamp = jsonObject.getLong("dt") * 1000; // Convert seconds to milliseconds
            String formattedDate = formatDate(timestamp);

            // Weather description
            String description = jsonObject.getJSONArray("weather")
                    .getJSONObject(0).getString("description");

            // Humidity, Wind Speed, and Rain formatting
            int humidity = jsonObject.getJSONObject("main").getInt("humidity");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            double rainVolume = 0; // Replace with the actual rain volume if available

            // Update TextViews
            TextView cityTextView = findViewById(R.id.city_name);
            cityTextView.setText(cityName);

            TextView temperatureTextView = findViewById(R.id.weather);
            temperatureTextView.setText(formattedTemperature);

            LottieAnimationView weatherAnimationView = findViewById(R.id.weather_image);
            setWeatherAnimation(weatherAnimationView, description);

            TextView dateTextView = findViewById(R.id.date);
            dateTextView.setText(formattedDate);

            TextView descriptionTextView = findViewById(R.id.discription);
            descriptionTextView.setText(description);

            TextView humidityTextView = findViewById(R.id.humidity_number);
            humidityTextView.setText(String.format("%d%%", humidity));

            TextView windSpeedTextView = findViewById(R.id.wind_number);
            windSpeedTextView.setText(String.format("%.2f m/s", windSpeed));

            TextView rainTextView = findViewById(R.id.rain_number);
            rainTextView.setText(String.format("%.2f mm", rainVolume));

            // Update other UI components...

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateForecast(String jsonData) {
        try {
            // Parse the JSON response for forecast data
            List<ForecastItem> forecastItems = parseForecastData(jsonData);

            // Update the RecyclerView with the forecast data
            forecastList.clear();
            forecastList.addAll(forecastItems);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<ForecastItem> parseForecastData(String jsonData) throws JSONException {
        List<ForecastItem> forecastItems = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonData);

        // Extract the forecast list from jsonObject
        JSONArray forecastList = jsonObject.getJSONArray("list");

        // Iterate through the forecast list
        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject forecastObject = forecastList.getJSONObject(i);

            // Extract required information from forecastObject
            long timestamp = forecastObject.getLong("dt") * 1000; // Convert seconds to milliseconds
            String formattedDay = formatDateForecast(timestamp);

            JSONObject mainObject = forecastObject.getJSONObject("main");
            double temperatureKelvin = mainObject.getDouble("temp");
            double temperatureCelsius = temperatureKelvin - 273.15;
            String formattedTemperature = String.format("%.0f°C", temperatureCelsius);

            JSONArray weatherArray = forecastObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            String icon ="a" + weatherObject.getString("icon");
            Log.d("IconDebug", "Icon value: " + icon);

            // Create a ForecastItem and add it to the list
            ForecastItem forecastItem = new ForecastItem(formattedDay, icon, temperatureCelsius);
            forecastItems.add(forecastItem);
        }

        return forecastItems;
    }

    private String formatDateForecast(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE | hh:mm a", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }



    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM dd | hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Africa/Tunis"));
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    private void setWeatherAnimation(LottieAnimationView animationView, String weatherDescription) {
        String animationFileName = getAnimationFileNameForWeather(weatherDescription);

        // Get the resource identifier for the animation file
        int animationResId = getResources().getIdentifier(animationFileName, "raw", getPackageName());

        // Set the animation resource
        animationView.setAnimation(animationResId);

        // Play the animation
        animationView.playAnimation();
    }

    private String getAnimationFileNameForWeather(String weatherDescription) {
        // Convert the description to lowercase for case-insensitive matching
        String lowercaseDescription = weatherDescription.toLowerCase();

        // Map weather descriptions to corresponding Lottie animation files
        if (lowercaseDescription.contains("rain")) {
            return "rain";
        }  else if (lowercaseDescription.contains("sunny") || lowercaseDescription.contains("sun")) {
            return "sunny";
        }else {
            return "clouds";
        }


    }
}