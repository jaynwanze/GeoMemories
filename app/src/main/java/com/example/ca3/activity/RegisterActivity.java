package com.example.ca3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ca3.R;
import com.example.ca3.databinding.ActivityRegisterBinding;
import com.example.ca3.ui.auth.AuthViewModel;
import com.example.ca3.utils.TextChangeHandler;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate view using View Binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe registration status
        authViewModel.getRegistrationStatus().observe(this, status -> {
            if (status == AuthViewModel.RegistrationStatus.SUCCESS) {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                // Navigate to MainActivity or LoginActivity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (status == AuthViewModel.RegistrationStatus.ERROR) {
                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

        TextChangeHandler tch = new TextChangeHandler(null,this);
        binding.editTextRegisterPassword.addTextChangedListener(tch);

        // Set up button click listener
        binding.buttonRegister.setOnClickListener(v -> {
            registerUser();
        });

        // Set up login redirect
        binding.textViewLoginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void validateTextChange() {
        if (binding.editTextRegisterPassword.length() <= 5) {
            binding.passwordValidationRegister.setText("Weak Password");
        }
        else if (binding.editTextRegisterPassword.length() > 6) {
            binding.passwordValidationRegister.setText("Strong Password");
        }
        else if (!binding.editTextRegisterPassword.equals(binding.editTextRegisterConfirmPassword)) {
            binding.passwordValidationRegister.setText("Passwords do not match");
        }
        else{
            binding.passwordValidationRegister.setText("");
        }
    }

    private void registerUser() {
        String email = binding.editTextRegisterEmail.getText().toString().trim();
        String name = binding.editTextRegisterName.getText().toString().trim();
        String dob = binding.editTextRegisterDOB.getText().toString().trim();
        String password = binding.editTextRegisterPassword.getText().toString().trim();
        String confirmPassword = binding.editTextRegisterConfirmPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            binding.editTextRegisterEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            binding.editTextRegisterName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(dob)) {
            binding.editTextRegisterDOB.setError("Date of Birth is required");
            return;
        }
        if (!dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
            binding.editTextRegisterDOB.setError("Invalid date format. Use MM/DD/YYYY");
            return;
        }
        if (dob.length() != 10) {
            binding.editTextRegisterDOB.setError("Invalid date format. Use MM/DD/YYYY");
            return;
        }
        if (dob.charAt(2) != '/' || dob.charAt(5) != '/') {
            binding.editTextRegisterDOB.setError("Invalid date format. Use MM/DD/YYYY");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.editTextRegisterPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            binding.editTextRegisterPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editTextRegisterConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Proceed with registration via ViewModel
        authViewModel.register(email, password, name, dob);
    }
}
