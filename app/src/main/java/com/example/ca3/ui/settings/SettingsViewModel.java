package com.example.ca3.ui.settings;

import static androidx.core.content.ContextCompat.getSystemService;
import static org.chromium.base.ContextUtils.getApplicationContext;
import static dagger.hilt.android.internal.Contexts.getApplication;

import android.app.Application;
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
        userPreferencesManager.saveUserPreferences(newPreferences);
        userPreferencesLiveData.setValue(newPreferences);
    }

    public void setSilentMode(boolean isSilentMode) {
        UserPreferences preferences = userPreferencesLiveData.getValue();
        if (preferences != null) {
            preferences.setSilentMode(isSilentMode);
            updateUserPreferences(preferences);
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (isSilentMode) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    // Restore to previous or default ringer mode
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
