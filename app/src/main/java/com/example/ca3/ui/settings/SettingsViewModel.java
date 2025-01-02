package com.example.ca3.ui.settings;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ca3.activity.LoginActivity;
import com.example.ca3.model.UserPreferences;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends AndroidViewModel {

    private final UserPreferencesManager userPreferencesManager;
    private final MutableLiveData<UserPreferences> userPreferencesLiveData = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.userPreferencesManager = UserPreferencesManager.getInstance(application);
        loadUserPreferences();
    }

    private void loadUserPreferences() {
        UserPreferences preferences = userPreferencesManager.getUserPreferences();
        userPreferencesLiveData.setValue(preferences);
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return userPreferencesLiveData;
    }

    public void updateUserPreferences(UserPreferences newPreferences) {
        if (newPreferences == null) {
            return;
        }
        newPreferences.setUserId(userPreferencesManager.getUserId());
        userPreferencesManager.saveUserPreferences(newPreferences);
        userPreferencesLiveData.setValue(newPreferences);
    }


    public void setSilentMode(boolean isSilentMode) {
        UserPreferences preferences = userPreferencesLiveData.getValue();
        Context context = getApplication();
        if (preferences != null) {
            preferences.setSilentMode(isSilentMode);
            updateUserPreferences(preferences);
            if (context != null) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (audioManager != null && notificationManager != null) {
                    if (isSilentMode) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } else {
                            // Revert the change if permission not granted
                            preferences.setSilentMode(false);
                            updateUserPreferences(preferences);
                        }
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                }
            }
        }
    }

    public boolean isSilentMode() {
        UserPreferences preferences = userPreferencesLiveData.getValue();
        return preferences != null && preferences.isSilentMode();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        userPreferencesManager.clearUserId();
    }
}
