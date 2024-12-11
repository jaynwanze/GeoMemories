package com.example.ca3.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ca3.model.User;
import com.example.ca3.utils.FirebaseUtils;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends AndroidViewModel {

    private final FirebaseUtils firebaseUtils;
    private final UserPreferencesManager userPreferencesManager;

    // LiveData for registration status
    public enum RegistrationStatus { SUCCESS, ERROR }
    private final MutableLiveData<RegistrationStatus> registrationStatus = new MutableLiveData<>();

    // LiveData for login status
    public enum LoginStatus { SUCCESS, ERROR }
    private final MutableLiveData<LoginStatus> loginStatus = new MutableLiveData<>();

    @Inject
    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.firebaseUtils = new FirebaseUtils();
        this.userPreferencesManager = new UserPreferencesManager(application);
    }

    public LiveData<RegistrationStatus> getRegistrationStatus() {
        return registrationStatus;
    }

    public LiveData<LoginStatus> getLoginStatus() {
        return loginStatus;
    }

    // Method to register a new user
    public void register(String email, String password, String name, String dob) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Registration failed
                        registrationStatus.postValue(RegistrationStatus.ERROR);
                    } // Registration successful
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        registrationStatus.postValue(RegistrationStatus.ERROR);
                    }

                    // Create a new user document in Firestore
                    User user = new User(firebaseUser.getUid(), email, name, dob, password);
                    userPreferencesManager.saveUserId(user.getId());
                    firebaseUtils.createUser(user, new FirebaseUtils.UserCallback() {
                        @Override
                        public void onSuccess() {
                            registrationStatus.postValue(RegistrationStatus.SUCCESS);

                        }
                        @Override
                        public void onFailure(Exception e) {
                            registrationStatus.postValue(RegistrationStatus.ERROR);
                        }
                    });
                });
    }

    // Method to log in an existing user
    public void login(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginStatus.postValue(LoginStatus.SUCCESS);
                    } else {
                        loginStatus.postValue(LoginStatus.ERROR);
                    }
                });
    }

    public void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        userPreferencesManager.clearUserId();
    }

}
