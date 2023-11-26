package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityLoginBinding;
import com.main.usermanagement.services.UserService;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;

    private UserService userService;

    private TextInputEditText inputEmail, inputPassword;

    private ActivityLoginBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        this.btnLogin = binding.btnLogin;
        this.inputEmail = binding.inputEmail;
        this.inputPassword = binding.inputPassword;

        this.userService = new UserService(this);

        this.btnLogin.setOnClickListener(view -> {
            binding.layoutLoading.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.inputPassword.getWindowToken(), 0);
            userService.authenticate(inputEmail.getText().toString(), inputPassword.getText().toString(), new ActionCallback<FirebaseUser>() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(getBaseContext(), "Login successfully " + user.getEmail(), Toast.LENGTH_LONG).show();
                    finish();
                }
                @Override
                public void onError(Exception e) {
                    binding.layoutLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(getBaseContext(), "Failed to login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}