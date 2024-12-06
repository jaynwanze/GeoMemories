package com.example.ca3.model;

public class User {

    private String id;
    private String email;
    private String name;
    private String dob;
    private String password;

    public User() {

    }

    public User(String id, String email, String name, String dob, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
