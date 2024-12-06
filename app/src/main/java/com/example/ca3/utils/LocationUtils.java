package com.example.ca3.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationUtils {

    private final FusedLocationProviderClient fusedLocationClient;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    @Inject
    public LocationUtils(Application application) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLocation.postValue(location);
                    }
                });
    }

    public LiveData<Location> getCurrentLocationLiveData() {
        return currentLocation;
    }

    public Location getLastKnownLocation() {
        return currentLocation.getValue();
    }
}
