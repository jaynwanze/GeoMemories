package com.example.ca3.ui.home;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.ca3.model.*;
import com.example.ca3.ui.auth.AuthViewModel;
import com.example.ca3.utils.*;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> recentMemories = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> memoriesStatistics = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    @Inject
    public HomeViewModel(@NonNull Application application) {
        super(application);
        userPreferencesManager = UserPreferencesManager.getInstance(application);
        fetchCurrentUser();
        loadRecentMemories();
        loadMemoriesStatistics();
    }

    public LiveData<List<Memory>> getRecentMemories() {
        return recentMemories;
    }

    public LiveData<Map<String, Integer>> getMemoriesStatistics() {
        return memoriesStatistics;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void fetchCurrentUser() {
        String currentUserId = userPreferencesManager.getUserId();
        if (currentUserId == null) {
            errorMessage.postValue("User not logged in");
            AuthViewModel authViewModel = new ViewModelProvider(this.getApplication()).get(AuthViewModel.class);
            userPreferencesManager.clearUserId();
            authViewModel.logout();
        }

        FirebaseUtils.getUser(currentUserId, new Callback.getUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.postValue(user);
            }

            @Override
            public void onFailure(Exception e) {
               Log.d("HomeViewModel", "Error fetching current user: " + e.getMessage());
            }
        });
    }

    private void loadRecentMemories() {
        String currentUserId = userPreferencesManager.getUserId();
        if (currentUserId == null) {
            errorMessage.postValue("User not logged in");
            return;
        }
        FirebaseUtils.getRecentMemories(10, currentUserId, new Callback.MemoryListCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
                recentMemories.postValue(memoryList);
            }


            @Override
            public void onFailure(Exception e) {
                Log.d("HomeViewModel", "Error fetching recent memories: " + e.getMessage());
            }
        });
    }

    private void loadMemoriesStatistics() {
        String currentUserId = userPreferencesManager.getUserId();
        if (currentUserId == null) {
            errorMessage.postValue("User not logged in");
            return;
        }
        FirebaseUtils.getAllMemories(currentUserId, new Callback.MemoryCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
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
                }
                memoriesStatistics.postValue(stats);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("loadMemoriesStatistics", "Failed to load memories statistics", e);
                errorMessage.postValue("Failed to load memories statistics: " + e.getMessage());
            }
        });
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
