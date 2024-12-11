package com.example.ca3.activity;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ca3.R;
import com.example.ca3.ui.capturememory.CaptureMemoryFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CaptureMemoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_memory);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.capture_memory_container, new CaptureMemoryFragment())
                    .commit();
        }
    }
}
