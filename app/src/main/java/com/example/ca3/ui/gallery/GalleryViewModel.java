package com.example.ca3.ui.gallery;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ca3.activity.LoginActivity;
import com.example.ca3.model.Memory;
import com.example.ca3.model.UserPreferences;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.DownloadManager;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class GalleryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;
    private final DownloadManager downloadManager;

    @Inject
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        this.userPreferencesManager = UserPreferencesManager.getInstance(application);
        this.downloadManager = DownloadManager.getInstance(application);
        loadMemories();
    }

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    private void loadMemories() {
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
        FirebaseUtils.getAllMemories(currentUserId, new Callback.MemoryListCallback() {
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

    public void refreshData() {
        loadMemories();
    }

    public String getGalleryTypeUserPreferences() {
        UserPreferences preferences = userPreferencesManager.getUserPreferences();
        if (preferences != null) {
            return preferences.getGalleryDisplay();
        }
        return null;
    }

    public void saveImageToExternalStorageLegacy(Bitmap bitmap) {
        downloadManager.saveImageToExternalStorageLegacy(bitmap);
    }

    public void saveImageToMediaStore(Bitmap bitmap) {
        downloadManager.saveImageToMediaStore(bitmap);
    }

    public void removeMemory(Memory memory) {
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
        FirebaseUtils.deleteMemory(currentUserId, memory.getId(), new Callback.DeleteMemoryCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "Memory removed successfully", Toast.LENGTH_SHORT).show();
                loadMemories();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplication(), "Failed to remove memory", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
