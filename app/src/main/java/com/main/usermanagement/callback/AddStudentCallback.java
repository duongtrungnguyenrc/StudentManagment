package com.main.usermanagement.callback;

import com.google.firebase.auth.FirebaseUser;

public interface AddStudentCallback {
    void onSuccess();
    void onError(Exception e);
}
