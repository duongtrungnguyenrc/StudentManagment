package com.main.usermanagement.models.entities;

public class Certificate {
    private String id;
    private String name;
    private String description;

    public Certificate(){};

    public Certificate(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Certificate(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }
}
