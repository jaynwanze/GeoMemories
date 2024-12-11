package com.example.ca3.utils;

import com.example.ca3.model.Memory;

import java.util.List;

public interface Callback {
    public interface MemoryCallback {
        void onSuccess(List<Memory> memoryList);
        void onFailure(Exception e);
    }

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

    public interface CreateMemoryCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
