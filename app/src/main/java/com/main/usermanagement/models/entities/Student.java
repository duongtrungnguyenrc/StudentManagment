package com.main.usermanagement.models.entities;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.main.usermanagement.models.enumerations.EStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {
    private String id;
    private String name;
    private int age;
    private String phone;
    private EStatus status;

    public List<DocumentReference> getCertificates() {
        return certificates;
    }

    private List<DocumentReference> certificates = new ArrayList<>();

    public Student(String id, String name, int age, String phone, EStatus status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
    }

    public Student(String name, int age, String phone, EStatus status) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
    }

    public Student(String id, String name, int age, String phone, EStatus status, List<DocumentReference> certificates) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.certificates = certificates;
    }

    public Student() {
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
    public String getPhone() {
        return phone;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }
}
