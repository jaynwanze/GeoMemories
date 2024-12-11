package com.example.ca3.ui.analytics;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
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

    @Inject
    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        userPreferencesManager = UserPreferencesManager.getInstance(application);
        loadMemoriesStatistics();
    }

    public LiveData<Map<String, Integer>> getMemoriesPerMonth() {
        return memoriesPerMonth;
    }

    private void loadMemoriesStatistics() {
        String currentUserId = userPreferencesManager.getUserId();
        FirebaseUtils.getAllMemories(currentUserId, new Callback.MemoryCallback() {
            @Override
            public void onSuccess(List<Memory> memoryList) {
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

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    private String convertTimestampToMonth(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        return sdf.format(date);
    }
}
