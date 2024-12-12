package com.example.ca3.ui.capturememory;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.Callback;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.LocationUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class CaptureMemoryViewModel extends AndroidViewModel {

    private final LocationUtils locationUtils;

    @Inject
    public CaptureMemoryViewModel(@NonNull Application application) {
        super(application);
        this.locationUtils = LocationUtils.getInstance(application);
        fetchCurrentLocation();
    }

    public Location getCurrentLocation() {
        return locationUtils.getLastKnownLocation();
    }

    public void fetchCurrentLocation() {
        locationUtils.getCurrentLocation();
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void saveMemory(String uid, Memory memory, Uri photoUri, SaveCallback callback) {
        Log.d("CaptureMemoryViewModel", "Saving memory with photo URI: " + photoUri);
        // Upload photo to Firebase Storage
        FirebaseUtils.uploadPhoto(uid, memory.getId() ,photoUri, new Callback.PhotoUploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                memory.setPhotoUrl(downloadUrl);
                FirebaseUtils.saveMemory(memory, new Callback.MemorySaveCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
