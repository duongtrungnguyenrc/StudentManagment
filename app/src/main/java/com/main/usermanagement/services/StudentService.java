package com.main.usermanagement.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.usermanagement.callback.AddStudentCallback;
import com.main.usermanagement.callback.AuthenticationCallback;
import com.main.usermanagement.callback.DeleteStudentCallback;
import com.main.usermanagement.callback.GetStudentByIdCallback;
import com.main.usermanagement.callback.UpdateStudentCallback;
import com.main.usermanagement.models.Student;
import com.main.usermanagement.models.enumerations.EStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {

    private static final String TAG = "StudentService";
    private static final String COLLECTION_NAME = "students";
    private FirebaseFirestore db;
    private CollectionReference studentsCollection;

    public StudentService() {
        this.db = FirebaseFirestore.getInstance();
        this.studentsCollection = db.collection(COLLECTION_NAME);
    }

    public interface OnDataRetrievedListener {
        void onDataRetrieved(List<Student> students);
        void onError(Exception e);
    }

    public void getAllStudents(final OnDataRetrievedListener listener) {
        List<Student> students = new ArrayList<>();

        studentsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Student student = document.toObject(Student.class);
                            student.setId(document.getId());
                            students.add(student);
                        }
                        listener.onDataRetrieved(students);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }


    public void getStudentById(String studentId, GetStudentByIdCallback action) {
        studentsCollection.document(studentId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Student student = task.getResult().toObject(Student.class);
                student.setId(task.getResult().getId());
                action.onSuccess(student);
            }
            else {
                action.onError(task.getException());
            }
        });
    }

    public void addStudent(String name, int age, String phone, EStatus status, AddStudentCallback action) {
        Student student = new Student(name, age, phone, status);
            studentsCollection.add(student).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    action.onSuccess();
                }
                else {
                    action.onError(task.getException());
                }
            });
    }

    public void addStudent(String id, String name, int age, String phone, EStatus status, AddStudentCallback action) {
        Student student = new Student(name, age, phone, status);
        studentsCollection.document(id).set(student).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                action.onSuccess();
            }
            else {
                action.onError(task.getException());
            }
        });
    }

    public void updateStudent(String studentId, String name, int age, String phone, UpdateStudentCallback action) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("age", age);
        updates.put("phone", phone);

        studentsCollection.document(studentId).update(updates).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                action.onSuccess();
            }
            else {
                action.onError(task.getException());
            }
        });
    }

    public void lockStudent(String studentId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", EStatus.LOCKED);

        studentsCollection.document(studentId).update(updates);
    }

    public void deleteStudent(String studentId, DeleteStudentCallback action) {
        studentsCollection.document(studentId).delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                action.onSuccess();
            }
            else {
                action.onError(task.getException());
            }
        });
    }
}
