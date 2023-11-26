package com.main.usermanagement.models.entities;


import android.net.Uri;

import com.main.usermanagement.models.enumerations.ERole;
import com.main.usermanagement.models.enumerations.EStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfile {
    private String name;
    private int age;
    private String phone;
    private EStatus status;
    private ERole role;
    private String image;
    private List<Date> accessHistory = new ArrayList<>();

    public UserProfile(){}

    public UserProfile(String name, int age, String phone, EStatus status, ERole role, String image) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.role = role;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public ERole getRole() {
        return role;
    }


    public void setRole(ERole role) {
        this.role = role;
    }

    public void setAccessHistory(List<Date> accessHistory) {
        this.accessHistory = accessHistory;
    }

    public List<Date> getAccessHistory() {
        return accessHistory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
