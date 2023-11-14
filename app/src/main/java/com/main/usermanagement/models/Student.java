package com.main.usermanagement.models;

import com.main.usermanagement.models.enumerations.EStatus;

import java.util.Date;
import java.util.List;

public class Student {
    private String id;
    private String name;
    private int age;
    private String phone;
    private EStatus status;

    private List<Certificate> certificates;

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

    public Student(String id, String name, int age, String phone, EStatus status, List<Certificate> certificates) {
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
}
