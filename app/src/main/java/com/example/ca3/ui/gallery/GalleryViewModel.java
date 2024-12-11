package com.example.ca3.ui.gallery;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class GalleryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();

    @Inject
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        loadMemories();
    }

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    private void loadMemories() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
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
}
