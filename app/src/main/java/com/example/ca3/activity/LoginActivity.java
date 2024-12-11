package com.example.ca3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ca3.R;
import com.example.ca3.databinding.ActivityLoginBinding;
import com.example.ca3.ui.auth.AuthViewModel;
import com.example.ca3.utils.TextChangeHandler;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate view using View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe login status
        authViewModel.getLoginStatus().observe(this, status -> {
            if (status == AuthViewModel.LoginStatus.SUCCESS) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                // Navigate to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (status == AuthViewModel.LoginStatus.ERROR) {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        //Set up text change listener
        TextChangeHandler tch = new TextChangeHandler(this,null);
        binding.editTextLoginPassword.addTextChangedListener(tch);

        // Set up button click listener
        binding.buttonLogin.setOnClickListener(v -> {
            loginUser();
        });

        // Set up register redirect
        binding.textViewRegisterRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void validateTextChange() {
        if (binding.editTextLoginPassword.length() <=5) {
            binding.passwordValidationLogin.setText("Weak Password");
        }
        else if (binding.editTextLoginPassword.length() > 5) {
            binding.passwordValidationLogin.setText("Strong Password");
        }
        else{
            binding.passwordValidationLogin.setText("");
        }
    }

    private void loginUser() {
        String email = binding.editTextLoginEmail.getText().toString().trim();
        String password = binding.editTextLoginPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            binding.editTextLoginEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextLoginPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            binding.editTextLoginPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Proceed with login via ViewModel
        authViewModel.login(email, password);
    }
}
