package com.example.ca3.activity;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ca3.R;
import com.example.ca3.ui.map.MapFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_container, new MapFragment())
                    .commit();
        }
    }
}
