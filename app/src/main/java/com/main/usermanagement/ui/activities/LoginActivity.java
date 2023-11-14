package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.AuthenticationCallback;
import com.main.usermanagement.databinding.ActivityLoginBinding;
import com.main.usermanagement.services.AuthService;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;

    private AuthService authService;

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

        this.authService = new AuthService(this);

        this.btnLogin.setOnClickListener(view -> {
            authService.authenticate(inputEmail.getText().toString(), inputPassword.getText().toString(), new AuthenticationCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getBaseContext(), "Login successfully " + user.getEmail(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getBaseContext(), "Failed to login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}