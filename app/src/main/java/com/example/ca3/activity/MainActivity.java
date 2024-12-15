package com.example.ca3.activity;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ca3.R;
import com.example.ca3.ui.auth.AuthViewModel;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    UserPreferencesManager userPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // User is logged in, save user ID
        userPreferencesManager = UserPreferencesManager.getInstance(this);
        userPreferencesManager.saveUserId(currentUser.getUid());

        // Initialize view
        setContentView(R.layout.activity_main);
        
        //If processing memory, show toast
        if (getIntent().getBooleanExtra("PROCESSING_MEMORY", false)) {
            Toast.makeText(this, "Processing memory...", Toast.LENGTH_SHORT).show();
        }

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        // Setup NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            navigateToSettings();
            return true;
        } else if (id == R.id.action_info) {
            showInformation();
            return true;
        } else if (id == R.id.action_logout) {
            performLogout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToSettings() {
        // Navigate to SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showInformation() {
        new AlertDialog.Builder(this)
                .setTitle("GeoMemories 1.0")
                .setMessage("This app allows you to manage and view your memories efficiently.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void performLogout() {
        // Perform logout
        FirebaseAuth.getInstance().signOut();
        userPreferencesManager.clearUserId();
        Intent intent = new Intent(this.getApplication(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.getApplication().startActivity(intent);
        finish();
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
