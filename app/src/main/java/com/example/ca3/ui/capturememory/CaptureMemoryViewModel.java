package com.example.ca3.ui.capturememory;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ca3.model.Memory;
import com.example.ca3.model.WeatherResponse;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.LocationUtils;
import com.example.ca3.utils.WeatherUtils;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class CaptureMemoryViewModel extends AndroidViewModel {

    private final LocationUtils locationUtils;
    private final WeatherUtils weatherutils;
    private final MutableLiveData<String> currentWeather = new MutableLiveData<>();

    @Inject
    public CaptureMemoryViewModel(@NonNull Application application) {
        super(application);
        this.locationUtils = LocationUtils.getInstance(application);
        this.weatherutils = WeatherUtils.getInstance(application);
        fetchCurrentLocation();
    }

    public Location getCurrentLocation() {
        return locationUtils.getLastKnownLocation();
    }

    public void fetchCurrentLocation() {
        locationUtils.getCurrentLocation();
    }
    public MutableLiveData<String> getCurrentWeather() {
        return currentWeather;
    }

    public void fetchCurrentWeather(double lat, double lon) {
        weatherutils.getWeather(lat, lon, new WeatherUtils.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                if (weatherResponse.getMain() != null &&
                        weatherResponse.getWeather() != null &&
                        !weatherResponse.getWeather().isEmpty()) {


                    double temperature = weatherResponse.getMain().getTemp();
                    String weatherDescription = weatherResponse.getWeather().get(0).getDescription();

                    String weatherInfo = "Temp: " + temperature + "°C\n" + "Weather: " + weatherDescription;
                    currentWeather.setValue(weatherInfo);
                } else {
                    currentWeather.setValue(null);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to fetch weather data.", t);
                currentWeather.setValue(null);
            }
        });
    }

    public void saveMemory(String uid, Memory memory, Uri photoUri, Callback.SaveCallback callback) {
        Log.d("CaptureMemoryViewModel", "Saving memory with photo URI: " + photoUri);
        // Upload photo to Firebase Storage
        FirebaseUtils.uploadPhoto(uid, memory.getId() ,photoUri, new Callback.PhotoUploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                memory.setPhotoUrl(downloadUrl);
                FirebaseUtils.saveMemory(memory, new Callback.MemorySaveCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
