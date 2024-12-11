package com.example.ca3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ca3.model.UserPreferences;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPreferencesManager {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_MAP_TYPE = "map_type";
    private static final String KEY_GALLERY_DISPLAY = "gallery_display";
    private static final String KEY_USER_ID = "user_id";
    private static UserPreferencesManager instance;

    private final SharedPreferences sharedPreferences;

    private UserPreferencesManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferencesManager(context);
        }
        return instance;
    }


    public void saveUserPreferences(UserPreferences preferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MAP_TYPE, preferences.getMapType());
        editor.putString(KEY_GALLERY_DISPLAY, preferences.getGalleryDisplay());
        editor.putString(KEY_USER_ID, preferences.getUserId());
        editor.apply();
    }

    public UserPreferences getUserPreferences() {
        UserPreferences preferences = new UserPreferences();
        preferences.setMapType(sharedPreferences.getString(KEY_MAP_TYPE, "normal"));
        preferences.setGalleryDisplay(sharedPreferences.getString(KEY_GALLERY_DISPLAY, "grid"));
        preferences.setUserId(sharedPreferences.getString(KEY_USER_ID, null));
        return preferences;
    }

    public void saveUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void clearUserId() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply();
    }
}
