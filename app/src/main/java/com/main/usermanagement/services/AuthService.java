package com.main.usermanagement.services;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.usermanagement.callback.AuthenticationCallback;

public class AuthService {
    private FirebaseAuth auth;
    private Context context;

    public AuthService(Context context) {
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(context);
    }

    public void createUser(String email, String password) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(this.context, "Authentication success: " + user.getUid(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void authenticate(String email, String password, AuthenticationCallback action) {
        if(email.isEmpty() || password.isEmpty()) {
            action.onFailure("Please enter " + (email.isEmpty() ? "email" : "password"));
        }
        else {
            this.auth.signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    action.onSuccess(auth.getCurrentUser());
                } else {
                    action.onError(task.getException());
                }
            });
        }
    }
}
