package com.example.ca3.utils;



import android.text.Editable;
import android.text.TextWatcher;

import com.example.ca3.activity.LoginActivity;
import com.example.ca3.activity.RegisterActivity;

public class TextChangeHandler implements TextWatcher
{

    private final LoginActivity loginActivity;
    private final RegisterActivity registerActivity;


    public TextChangeHandler(LoginActivity loginActivity, RegisterActivity registerActivity) {
        this.loginActivity = loginActivity;
        this.registerActivity = registerActivity;
    }

    public void afterTextChanged (Editable e) {
        if (loginActivity != null){
            loginActivity.validateTextChange();
        }
        if (registerActivity != null){
            registerActivity.validateTextChange();
        }
    }
    public void beforeTextChanged (CharSequence s, int start, int count, int
            after ) {
    }
    public void onTextChanged (CharSequence s, int start, int before, int after
    ){
    }
}
