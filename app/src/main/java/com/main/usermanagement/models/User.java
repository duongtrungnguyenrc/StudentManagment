package com.main.usermanagement.models;

import com.main.usermanagement.models.enumerations.ERole;

import lombok.Data;

@Data
public class User {
    private String email;
    private String password;
    private ERole role;

    public User(String email, String password, ERole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public ERole getRole() {
        return role;
    }
}
