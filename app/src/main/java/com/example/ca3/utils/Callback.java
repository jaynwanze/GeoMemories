package com.example.ca3.utils;

import com.example.ca3.model.Memory;
import com.example.ca3.model.Place;
import com.example.ca3.model.PlacesResponse;
import com.example.ca3.model.User;

import java.util.List;

public interface Callback {
    public interface MemoryListCallback {
        void onSuccess(List<Memory> memoryList);
        void onFailure(Exception e);
    }

    public interface MemorySaveCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface PhotoUploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    public interface MemorySingleCallback {
        void onSuccess(Memory memory);
        void onFailure(Exception e);
    }

    public interface UserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface getUserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface PlacesCallback {
        void onSuccess(List<Place> places);
        void onFailure(Throwable t);
    }
    public interface CreateMemoryCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
