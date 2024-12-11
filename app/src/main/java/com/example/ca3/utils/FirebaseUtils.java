package com.example.ca3.utils;

import android.net.Uri;
import com.example.ca3.model.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

public class FirebaseUtils {

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

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

    // Create a new user document in Firestore
    public void createUser(User user, FirebaseUtils.UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Method to create a new Memory
    public void createMemory(Memory memory, CreateMemoryCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .document(memory.getId()) // Assuming 'id' is generated externally
                .set(memory)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    public static void getAllMemories(MemoryCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Memory> memories = queryDocumentSnapshots.toObjects(Memory.class);
                    callback.onSuccess(memories);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getRecentMemories(int limit, MemoryListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Memory> memories = queryDocumentSnapshots.toObjects(Memory.class);
                    callback.onSuccess(memories);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void saveMemory(Memory memory, MemorySaveCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create unique memory id
        String memoryId = db.collection("memories").document().getId();
        memory.setId(memoryId);
        db.collection("memories")
                .add(memory)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void uploadPhoto(String uid, String memoryId, Uri photoUri, PhotoUploadCallback callback) {
        if (photoUri == null) {
            callback.onFailure(new Exception("Photo URI is null"));
            return;
        }
        // Construct the storage path: memories/{uid}/{memoryId}
        String storagePath = "memories/" + uid + "/" + memoryId;
        StorageReference photoRef = storage.getReference().child(storagePath);

        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }


    public static void getMemoryById(String memoryId, MemorySingleCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .document(memoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Memory memory = documentSnapshot.toObject(Memory.class);
                        callback.onSuccess(memory);
                    } else {
                        callback.onFailure(new Exception("Memory not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
