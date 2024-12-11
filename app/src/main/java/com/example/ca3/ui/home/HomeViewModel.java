package com.example.ca3.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ca3.model.*;
import com.example.ca3.utils.*;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> recentMemories = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> memoriesStatistics = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;

    @Inject
    public HomeViewModel(@NonNull Application application) {
        super(application);
        userPreferencesManager = UserPreferencesManager.getInstance(application);
        loadRecentMemories();
        loadMemoriesStatistics();
    }

    public LiveData<List<Memory>> getRecentMemories() {
        return recentMemories;
    }

    public LiveData<Map<String, Integer>> getMemoriesStatistics() {
        return memoriesStatistics;
    }

    private void loadRecentMemories() {
        String currentUserId = userPreferencesManager.getUserId();
        FirebaseUtils.getRecentMemories(10, currentUserId, new Callback.MemoryListCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
                recentMemories.postValue(memoryList);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure, e.g., log or show a message
            }
        });
    }

    private void loadMemoriesStatistics() {
        String currentUserId = userPreferencesManager.getUserId();
        FirebaseUtils.getAllMemories(currentUserId, new Callback.MemoryCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
                Map<String, Integer> stats = new HashMap<>();
                for (Memory memory : memoryList) {
                    String location = memory.getLocation().getLatitude() + "," + memory.getLocation().getLongitude();
                    stats.put(location, stats.getOrDefault(location, 0) + 1);
                }
                memoriesStatistics.postValue(stats);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });

    }
}
