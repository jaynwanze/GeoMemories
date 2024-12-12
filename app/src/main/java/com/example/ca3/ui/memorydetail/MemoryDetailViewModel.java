package com.example.ca3.ui.memorydetail;


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
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class MemoryDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Memory> memory = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public MemoryDetailViewModel(@NonNull Application application) {
        super(application);
        this.userPreferencesManager = UserPreferencesManager.getInstance(application);

    }

    public LiveData<Memory> getMemory() {
        return memory;
    }

    public void fetchMemoryDetails(String memoryId) {
        String currentUserId = userPreferencesManager.getUserId();
        FirebaseUtils.getMemoryById(memoryId, currentUserId, new Callback.MemorySingleCallback() {
            @Override
            public void onSuccess(Memory mem) {
                memory.postValue(mem);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
                error.postValue(e.getMessage());
            }
        });
    }
}
