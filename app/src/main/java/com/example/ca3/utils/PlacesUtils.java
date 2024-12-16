// package com.example.ca3.utils;

package com.example.ca3.utils;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ca3.BuildConfig;
import com.example.ca3.model.PlacesResponse;
import com.example.ca3.services.PlacesService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesUtils {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private final PlacesService placesService;
    private static PlacesUtils instance;
    private final String apiKey;

    private PlacesUtils(Application application) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placesService = retrofit.create(PlacesService.class);
        apiKey = BuildConfig.PLACES_API_KEY;
    }

    public static synchronized PlacesUtils getInstance(Application application) {
        if (instance == null) {
            instance = new PlacesUtils(application);
        }
        return instance;
    }

    public void getNearbyPlaces(double latitude, double longitude, int radius, String type, com.example.ca3.utils.Callback.PlacesCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onFailure(new Exception("API key not set"));
            return;
        }

        String location = latitude + "," + longitude;

        Call<PlacesResponse> call = placesService.getNearbyPlaces(location, radius, type, apiKey);
        Log.d("PlacesUtils", "API URL: " + call.request().url());
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlacesResponse placesResponse = response.body();
                    if ("OK".equals(placesResponse.getStatus()) && !placesResponse.getResults().isEmpty()) {
                        callback.onSuccess(placesResponse.getResults());
                        Log.d("PlacesUtils", "Nearby places fetched successfully");
                        Log.d("PlacesUtils", "Response: " + placesResponse.toString());
                    } else {
                        callback.onFailure(new Exception("No places found or API error: " + placesResponse.getStatus()));
                        Log.d("PlacesUtils", "Response: " + placesResponse.getErrorMessage() + placesResponse.getHtmlAttributions());
                    }
                } else {
                    callback.onFailure(new Exception("Response unsuccessful or empty"));
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                callback.onFailure(t);
                Log.e("PlacesUtils", "Failed to fetch nearby places", t);
            }
        });
    }
}
