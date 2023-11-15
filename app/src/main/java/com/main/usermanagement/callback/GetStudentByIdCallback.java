package com.main.usermanagement.callback;

import com.main.usermanagement.models.Student;

public interface GetStudentByIdCallback {
    void onSuccess(Student student);
    void onError(Exception e);
}
