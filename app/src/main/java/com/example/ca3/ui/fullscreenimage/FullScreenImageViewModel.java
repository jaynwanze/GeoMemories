package com.example.ca3.ui.fullscreenimage;


import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ca3.model.Memory;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.DownloadManager;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class FullScreenImageViewModel extends AndroidViewModel {

    private final MutableLiveData<Memory> memory = new MutableLiveData<>();
    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final DownloadManager downloadManager;

    @Inject
    public FullScreenImageViewModel(@NonNull Application application) {
        super(application);
        this.userPreferencesManager = UserPreferencesManager.getInstance(application);
        this.downloadManager = DownloadManager.getInstance(application);
    }

    public LiveData<Memory> getMemory() {
        return memory;
    }

    public void saveImageToExternalStorageLegacy(Bitmap bitmap) {
        downloadManager.saveImageToExternalStorageLegacy(bitmap);

    }

    public void saveImageToMediaStore(Bitmap bitmap) {
        downloadManager.saveImageToMediaStore(bitmap);
    }
}
