package com.example.ca3.ui.analytics;

import android.app.Application;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ca3.activity.LoginActivity;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class AnalyticsViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<String, Integer>> memoriesPerMonth = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<Map<String, Integer>> memoriesPerLocation = new MutableLiveData<>();
    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();

    @Inject
    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        userPreferencesManager = UserPreferencesManager.getInstance(application);
        memories.setValue(new ArrayList<>());
        fetchMemories();
        memories.observeForever(this::loadMemoriesStatistics);
        memories.observeForever(this::loadMemoriesByLocationStatistics);
    }

    public LiveData<Map<String, Integer>> getMemoriesPerMonth() {
        return memoriesPerMonth;
    }

    public LiveData<Map<String, Integer>> getMemoriesPerLocation() {
        return memoriesPerLocation;
    }

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    public void refreshData() {
        fetchMemories();
        memories.observeForever(this::loadMemoriesStatistics);
        memories.observeForever(this::loadMemoriesByLocationStatistics);
    }

    private void fetchMemories() {
        String currentUserId = userPreferencesManager.getUserId();
        if (currentUserId == null) {
            FirebaseAuth.getInstance().signOut();
            userPreferencesManager.clearUserId();
            Toast.makeText(this.getApplication(), "User not logged in", Toast.LENGTH_SHORT).show();
            Log.d("HomeViewModel", "User not logged in");
            Intent intent = new Intent(this.getApplication(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.getApplication().startActivity(intent);
            return;
        }
        FirebaseUtils.getAllMemories(currentUserId, new Callback.MemoryCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
                memories.postValue(memoryList);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    private void loadMemoriesStatistics(List<Memory> memoryList) {
        Map<String, Integer> stats = new HashMap<>();
        for (Memory memory : memoryList) {
            Timestamp timestamp = memory.getTimestamp();
            if (timestamp != null) {
                String month = convertTimestampToMonth(timestamp);
                stats.put(month, stats.getOrDefault(month, 0) + 1);
            } else {
                Log.e("loadMemoriesStatistics", "Memory with id " + memory.getId() + " has null timestamp.");
            }
        }
        memoriesPerMonth.postValue(stats);
    }


    private void loadMemoriesByLocationStatistics(List<Memory> memoryList) {
        Map<String, Integer> stats = new HashMap<>();
        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());

        for (Memory memory : memoryList) {
            Timestamp timestamp = memory.getTimestamp();
            if (timestamp != null) {
                String locationName = getLocationName(geocoder, memory.getLocation());
                if (locationName != null) {
                    stats.put(locationName, stats.getOrDefault(locationName, 0) + 1);
                } else {
                    Log.e("loadMemoriesStatistics", "Could not get location name for memory id " + memory.getId());
                }
            } else {
                Log.e("loadMemoriesStatistics", "Memory with id " + memory.getId() + " has null timestamp.");
            }
            memoriesPerLocation.postValue(stats);
        }
    }

    private String convertTimestampToMonth(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        return sdf.format(date);
    }

    private String getLocationName(Geocoder geocoder, GeoPoint geoPoint) {
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAdminArea();
            }
        } catch (IOException e) {
            Log.e("HomeViewModel", "Geocoder IOException: ", e);
        }
        return null;
    }
}
