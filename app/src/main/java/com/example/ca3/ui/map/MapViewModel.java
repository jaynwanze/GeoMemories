package com.example.ca3.ui.map;

import android.app.Application;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.LocationUtils;
import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class MapViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final LocationUtils locationUtils;
    private final FirebaseUtils firebaseUtils;

    @Inject
    public MapViewModel(@NonNull Application application) {
        super(application);
        locationUtils = new LocationUtils(application);
        this.firebaseUtils = new FirebaseUtils();
        loadMemories();
        fetchCurrentLocation();
    }

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public void fetchCurrentLocation() {
        locationUtils.getCurrentLocation();
        locationUtils.getCurrentLocationLiveData().observeForever(currentLocation::postValue);
    }

    private void loadMemories() {
        FirebaseUtils.getAllMemories(new FirebaseUtils.MemoryCallback() {
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


    // Method to create a new memory
    public void createMemory(Memory memory, FirebaseUtils.CreateMemoryCallback callback) {
        firebaseUtils.createMemory(memory, new FirebaseUtils.CreateMemoryCallback() {
            @Override
            public void onSuccess() {
                // Optionally, refresh the memories list
                loadMemories();
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
