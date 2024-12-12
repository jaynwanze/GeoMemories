package com.example.ca3.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ca3.R;
import com.example.ca3.ui.memorydetail.MemoryDetailFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MemoryDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtity_memory_detail);

        if (savedInstanceState == null) {
            String memoryId = getIntent().getStringExtra("memory_id");
            MemoryDetailFragment fragment = MemoryDetailFragment.newInstance(memoryId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.memory_detail_container, fragment)
                    .commit();
        }
    }
}
