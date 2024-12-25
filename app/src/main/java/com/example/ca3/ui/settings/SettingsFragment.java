package com.example.ca3.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.ca3.R;
import com.example.ca3.activity.LoginActivity;
import com.example.ca3.databinding.FragmentSettingsBinding;
import com.example.ca3.model.UserPreferences;
import com.example.ca3.ui.auth.AuthViewModel;
import com.example.ca3.utils.UserPreferencesManager;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModels
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Setup Map Type Spinner
        ArrayAdapter<CharSequence> mapTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.map_types_array, android.R.layout.simple_spinner_item);
        mapTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMapType.setAdapter(mapTypeAdapter);

        // Setup Gallery Display Spinner
        ArrayAdapter<CharSequence> galleryDisplayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gallery_display_array, android.R.layout.simple_spinner_item);
        galleryDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGalleryDisplay.setAdapter(galleryDisplayAdapter);

        // Observe User Preferences
        settingsViewModel.getUserPreferences().observe(getViewLifecycleOwner(), preferences -> {
            if (preferences != null) {
                int mapTypePosition = mapTypeAdapter.getPosition(preferences.getMapType());
                binding.spinnerMapType.setSelection(mapTypePosition);

                int galleryDisplayPosition = galleryDisplayAdapter.getPosition(preferences.getGalleryDisplay());
                binding.spinnerGalleryDisplay.setSelection(galleryDisplayPosition);
            }
        });

        // Set up Silent Mode Switch
        binding.swtichSilentMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsViewModel.setSilentMode(isChecked);
        });

        // Save Preferences Button
        binding.buttonSavePreferences.setOnClickListener(v -> savePreferences());

        // Set up Logout Button
        binding.buttonLogout.setOnClickListener(v -> {
            logoutUser();
        });

        return root;
    }

    private void savePreferences() {
        String selectedMapType = binding.spinnerMapType.getSelectedItem().toString();
        String selectedGalleryDisplay = binding.spinnerGalleryDisplay.getSelectedItem().toString();

        UserPreferences preferences = new UserPreferences();
        preferences.setMapType(selectedMapType);
        preferences.setGalleryDisplay(selectedGalleryDisplay);

        settingsViewModel.updateUserPreferences(preferences);
    }

    private void logoutUser() {
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        settingsViewModel.logout();
        // Navigate to LoginActivity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
