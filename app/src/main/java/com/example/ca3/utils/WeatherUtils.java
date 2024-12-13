package com.example.ca3.utils;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.ca3.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.ca3.services.WeatherService;


public class WeatherUtils {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private final WeatherService weatherService;
    private static WeatherUtils instance;
    private String apiKey;

    private WeatherUtils( Application application) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherService.class);
        apiKey = com.example.ca3.BuildConfig.OPEN_WEATHER_API;
    }

    public static synchronized WeatherUtils getInstance( Application application) {
        if (instance == null) {
            instance = new WeatherUtils(application);
        }
        return instance;
    }



    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onFailure(Throwable t);
    }

    public void getWeather(double lat, double lon, WeatherCallback callback) {
        if (apiKey == null) {
            callback.onFailure(new Exception("API key not set"));
            return;
        }

        Call<WeatherResponse> call = weatherService.getCurrentWeather(lat, lon, "metric", apiKey);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Response unsuccessful"));
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                callback.onFailure(t);
                Log.e("WeatherUtils", "Failed to fetch weather data", t);
            }
        });
    }
}
