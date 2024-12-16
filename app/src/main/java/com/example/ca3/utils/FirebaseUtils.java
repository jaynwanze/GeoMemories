package com.example.ca3.utils;

import android.net.Uri;
import android.util.Log;

import com.example.ca3.model.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

public class FirebaseUtils {

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a new user document in Firestore
    public static void createUser(User user, Callback.UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public static void getUser(String userId, Callback.getUserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnSuccessListener(
                documentSnapshot -> {
                    if (documentSnapshot.exists() ) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception("User not found"));
                        }
                }
        ).addOnFailureListener(
                e -> {
                    callback.onFailure(e);
                    Log.e("FirebaseUtils", "Error getting user", e);
                }
        );
    }

    // Method to create a new Memory
    public static void createMemory(Memory memory, Callback.CreateMemoryCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .document(memory.getId()) // Assuming 'id' is generated externally
                .set(memory)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    public static void getAllMemories(String userId, Callback.MemoryListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .whereEqualTo("userId", userId) // Filter by userId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Memory> memories = queryDocumentSnapshots.toObjects(Memory.class);
                    callback.onSuccess(memories);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // FirebaseUtils.java
    public static void getRecentMemories(int limit, String userId, Callback.MemoryListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .whereEqualTo("userId", userId) // Filter by userId
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Memory> memories = queryDocumentSnapshots.toObjects(Memory.class);
                    callback.onSuccess(memories);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching recent memories", e);
                    callback.onFailure(e);
                });
    }


    public static void saveMemory(Memory memory, Callback.MemorySaveCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Generate a unique memory ID
        String memoryId = db.collection("memories").document().getId();
        memory.setId(memoryId);
        db.collection("memories")
                .document(memoryId)
                .set(memory)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }


    public static void uploadPhoto(String userId, String memoryId, Uri photoUri, Callback.PhotoUploadCallback callback) {
        if (photoUri == null) {
            callback.onFailure(new Exception("Photo URI is null"));
            return;
        }
        // Construct the storage path: memories/{uid}/{memoryId}
        String storagePath = "memories/" + userId + "/" + memoryId;
        StorageReference photoRef = storage.getReference().child(storagePath);

        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }


    public static void getMemoryById(String memoryId, String userId, Callback.MemorySingleCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("memories")
                .document(memoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Memory memory = documentSnapshot.toObject(Memory.class);
                        if (memory != null && memory.getUserId().equals(userId)) {
                            callback.onSuccess(memory);
                        } else {
                            callback.onFailure(new Exception("Memory not found or does not belong to the user"));
                        }
                    } else {
                        callback.onFailure(new Exception("Memory not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
