package com.example.taskmanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherTask extends AppCompatActivity {
    private EditText editTextCity;
    private Button buttonFetchWeather;

    private static final String OPENWEATHERMAP_GEOCODING_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=";
    private static final String OPENWEATHERMAP_WEATHER_FORECASE_URL = "https://api.open-meteo.com/v1/forecast?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_display);

        editTextCity = findViewById(R.id.editTextCity);
        buttonFetchWeather = findViewById(R.id.buttonFetchWeather);

        buttonFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    // Construct the geocoding API URL and start the geocoding task
                    String geocodingApiUrl = OPENWEATHERMAP_GEOCODING_API_URL + city + "&count=1&language=en&format=json";
                    new GeocodingTask().execute(geocodingApiUrl);
                } else {
                    // Show a Toast message indicating that the city name is empty
                    Toast.makeText(WeatherTask.this, "Please enter a city name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GeocodingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Create a URL object with the geocoding API URL
                URL url = new URL(params[0]);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the HTTP request method to GET
                connection.setRequestMethod("GET");

                // Read the response from the geocoding API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log the response
                Log.d("GeocodingResponse", response.toString());

                // Parse the JSON response as an object
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");

                if (results.length() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    double latitude = firstResult.getDouble("latitude");
                    double longitude = firstResult.getDouble("longitude");

                    // Return latitude and longitude as a string
                    String latLonString = latitude + "," + longitude;
                    Log.d("GeocodingResponse", "Latitude and Longitude: " + latLonString);

                    return latLonString;

                } else {
                    return null; // Handle the case where no results were found
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Handle the case where an exception occurs
            }
        }


        @Override
        protected void onPostExecute(String latLonString) {
            if (latLonString != null) {
                // Split the latLon string into latitude and longitude
                String[] latLonParts = latLonString.split(",");
                double latitude = Double.parseDouble(latLonParts[0]);
                double longitude = Double.parseDouble(latLonParts[1]);

                // Format the latitude and longitude
                String formattedLatitude = String.format("%.2f", latitude);
                String formattedLongitude = String.format("%.2f", longitude);

                // Construct the weather API URL using latitude and longitude
                String weatherApiUrl = OPENWEATHERMAP_WEATHER_FORECASE_URL + "latitude=" + latitude + "&longitude="
                        + longitude + "&hourly=temperature_2m,rain&timezone=Asia%2FSingapore";

                Log.d("WeatherResponse", weatherApiUrl.toString());

                // Start the WeatherFetchTask to fetch weather data
                new WeatherFetchTask().execute(weatherApiUrl);
            } else {
                // Handle the case where geocoding data couldn't be fetched
                Toast.makeText(WeatherTask.this, "Failed to fetch geocoding data.", Toast.LENGTH_SHORT).show();
            }
        }

        private class WeatherFetchTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // Extract latitude and longitude from the params
                    String latLon = params[0];

                    // Split the latLng string into latitude and longitude
                    String[] latLonParts = latLon.split(",");
                    String latitude = latLonParts[0];
                    String longitude = latLonParts[1];

                    // Create the weather API URL with the extracted latitude and longitude
                    URL url = new URL(params[0]);
                    Log.d("apiUrl", String.valueOf(url));
                    // Create a URL object with the weather API URL

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set the HTTP request method to GET
                    connection.setRequestMethod("GET");

                    // Read the response from the weather API
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Return the weather data as a string
                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        // Print the entire JSON response to the log
                        Log.d("JsonResponse", result);

                        // Parse the JSON response as an object
                        JSONObject jsonResponse = new JSONObject(result);

                        // Access the "hourly" object
                        JSONObject hourlyData = jsonResponse.getJSONObject("hourly");

                        // Access the "temperature_2m" array within the "hourly" object
                        JSONArray temperatureArray = hourlyData.getJSONArray("temperature_2m");

                        // Access the "rain" array within the "hourly" object
                        JSONArray rainArray = hourlyData.getJSONArray("rain");

                        double temperature = temperatureArray.getDouble(0);
                        double rain = rainArray.getDouble(0);

                        String weatherText = "Temperature: " + temperature + " °C\nRain: " + rain + " mm";
                        TextView textViewWeather = findViewById(R.id.textViewWeather);
                        textViewWeather.setText(weatherText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the case where weather data couldn't be fetched
                    Toast.makeText(WeatherTask.this, "Failed to fetch weather data.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

