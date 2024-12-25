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
import com.example.ca3.model.Place;
import com.example.ca3.model.PlacesResponse;
import com.example.ca3.model.WeatherResponse;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.LocationUtils;
import com.example.ca3.utils.PlacesUtils;
import com.example.ca3.utils.WeatherUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class CaptureMemoryViewModel extends AndroidViewModel {

    private final LocationUtils locationUtils;
    private final WeatherUtils weatherutils;
    private final PlacesUtils placesUtils;
    private final MutableLiveData<String> currentWeather = new MutableLiveData<>();
    private final MutableLiveData<List<Place>> nearbyPlaces = new MutableLiveData<>();

    @Inject
    public CaptureMemoryViewModel(@NonNull Application application) {
        super(application);
        this.locationUtils = LocationUtils.getInstance(application);
        this.weatherutils = WeatherUtils.getInstance(application);
        this.placesUtils = PlacesUtils.getInstance(application);
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

    public MutableLiveData<List<Place>> getNearbyPlaces() {
        return nearbyPlaces;
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

    public void fetchNearbyPlaces(double lat, double lon) {
        placesUtils.getNearbyPlaces(
                lat,
                lon,
                5000,
                "tourist_attraction",
                new Callback.PlacesCallback() {

                    @Override
                    public void onSuccess(List<Place> places) {
                        // Convert PlacesResponse.Place to Memory.Place
                        List<Place> nearbyPlacesList = new ArrayList<>();
                        for (Place place : places) {
                            Place memoryPlace = new Place(
                                    place.getName(),
                                    place.getVicinity(),
                                    place.getGeometry(),
                                    place.getTypes()
                            );
                            nearbyPlacesList.add(memoryPlace);
                        }
                        nearbyPlaces.setValue(nearbyPlacesList);
                        Log.d("CaptureMemoryViewModel", "Nearby places fetched and set.");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure to fetch nearby places
                        Log.e("GalleryFragment", "Failed to fetch nearby places", t);
                        nearbyPlaces.setValue(null);
                    }
                }
        );
    }

    public void saveMemory(String uid, Memory memory, Uri photoUri, Callback.SaveCallback callback) {
        // Save give memory an id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("memories").document();
        String memoryId = docRef.getId();
        memory.setId(memoryId);

        // Upload photo to Firebase Storage
        FirebaseUtils.uploadPhoto(uid, memory.getId(), photoUri, new Callback.PhotoUploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                memory.setPhotoUrl(downloadUrl);
                FirebaseUtils.saveMemory(memory, new Callback.MemorySaveCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                        Log.d(TAG, "Memory saved successfully.");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                        Log.e(TAG, "Failed to save memory.", e);
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
