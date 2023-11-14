package com.main.usermanagement.callback;

import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(String message);
    void onError(Exception e);
}
