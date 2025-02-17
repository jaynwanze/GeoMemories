package com.example.ca3.ui.map;

import android.app.Application;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ca3.model.Memory;
import com.example.ca3.model.Place;
import com.example.ca3.model.UserPreferences;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.LocationUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class MapViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final LocationUtils locationUtils;
    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<Place> selectedPlace = new MutableLiveData<>();

    @Inject
    public MapViewModel(@NonNull Application application) {
        super(application);
        userPreferencesManager = UserPreferencesManager.getInstance(application);
        this.locationUtils = LocationUtils.getInstance(application);
        loadMemories();
        fetchCurrentLocation();
    }

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<Place> getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(Place place) {
        selectedPlace.setValue(place);
    }


    public void fetchCurrentLocation() {
        locationUtils.getCurrentLocation();
        locationUtils.getCurrentLocationLiveData().observeForever(currentLocation::postValue);
    }

    private void loadMemories() {
        String currentUserId = userPreferencesManager.getUserId();
        FirebaseUtils.getAllMemories(currentUserId,new Callback.MemoryListCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
                memories.postValue(memoryList);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure, e.g., log or show a message
                e.printStackTrace();
            }
        });
    }


    public Memory getMemoryById(String memoryId) {
        if (memories.getValue() != null) {
            for (Memory memory : memories.getValue()) {
                if (memory.getId().equals(memoryId)) {
                    return memory;
                }
            }
        }
        return null;
    }

    public String getMapTypeUserPreferences() {
        UserPreferences preferences = userPreferencesManager.getUserPreferences();
        if (preferences != null) {
            return preferences.getMapType();
        }
        return null;
    }

    public void refreshMemories() {
        loadMemories();
    }
}
