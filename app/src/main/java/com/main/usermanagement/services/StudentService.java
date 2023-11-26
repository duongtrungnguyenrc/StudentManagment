package com.main.usermanagement.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.models.entities.Certificate;
import com.main.usermanagement.models.entities.Student;
import com.main.usermanagement.models.enumerations.ELockStudentAction;
import com.main.usermanagement.models.enumerations.ERole;
import com.main.usermanagement.models.enumerations.EStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {

    private static final String COLLECTION_NAME = "students";
    private final CollectionReference studentsCollection;
    private ERole currRole;

    public StudentService() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.studentsCollection = db.collection(COLLECTION_NAME);
        currRole = UserService.getCurrRole();
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
                        listener.onError(task.getException());
                    }
                });
    }


    public void getStudentById(String studentId, ActionCallback<Student> action) {
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

    public void addStudent(String name, int age, String phone, EStatus status, ActionCallback<Object> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
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
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void addStudent(String id, String name, int age, String phone, EStatus status, ActionCallback<Object> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
            Student student = new Student(name, age, phone, status);
            studentsCollection.document(id).set(student).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    action.onSuccess();
                } else {
                    action.onError(task.getException());
                }
            });
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void updateStudent(String studentId, String name, int age, String phone, ActionCallback<Object> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("age", age);
            updates.put("phone", phone);

            studentsCollection.document(studentId).update(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    action.onSuccess();
                } else {
                    action.onError(task.getException());
                }
            });
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void lockStudent(String studentId, ActionCallback<ELockStudentAction> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
            Map<String, Object> updates = new HashMap<>();
            getStudentById(studentId, new ActionCallback<Student>() {
                @Override
                public void onSuccess(Student student) {
                    updates.put("status", student.getStatus() == EStatus.NORMAL ? EStatus.LOCKED : EStatus.NORMAL);
                    studentsCollection.document(studentId).update(updates);
                    action.onSuccess(student.getStatus() == EStatus.NORMAL ? ELockStudentAction.LOCK : ELockStudentAction.UNLOCK);
                }

                @Override
                public void onError(Exception e) {
                    action.onError(e);
                }
            });
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void deleteStudent(String studentId, ActionCallback<Object> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
        studentsCollection.document(studentId).delete()
                .addOnSuccessListener(unused -> action.onSuccess())
                .addOnFailureListener(action::onError);
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void grantStudentCertificates(String studentID, List<Certificate> certificates, ActionCallback<Object> action) {
        if(currRole == ERole.ROLE_ADMIN || currRole == ERole.ROLE_MANAGER) {
            DocumentReference studentRef = studentsCollection.document(studentID);

            List<DocumentReference> certificateRefs = new ArrayList<>();
            for (Certificate certificate : certificates) {
                try {
                    DocumentReference certificateRef = studentsCollection.document(certificate.getId());
                    certificateRefs.add(certificateRef);
                } catch (Exception e) {

                }
            }

            studentRef.update("certificates", certificateRefs)
                    .addOnSuccessListener(aVoid -> {
                        action.onSuccess();
                    })
                    .addOnFailureListener(action::onError);
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }
}
