package com.example.ca3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ca3.R;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authStateListener;
    UserPreferencesManager userPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (getIntent().getBooleanExtra("PROCESSING_MEMORY", false)) {
            Toast.makeText(this, "Processing memory...", Toast.LENGTH_SHORT).show();
        }


        Log.d("MainActivity", "User ID: " + currentUser.getUid());
        userPreferencesManager= UserPreferencesManager.getInstance(this);
        userPreferencesManager.saveUserId(currentUser.getUid());

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        // Setup NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
