package com.example.ca3.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ca3.R;
import com.example.ca3.ui.settings.SettingsFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }
}
