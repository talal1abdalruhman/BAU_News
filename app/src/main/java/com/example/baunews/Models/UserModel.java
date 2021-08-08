package com.example.baunews.Models;

public class UserModel {
    String email, password, collage, collageId, admin;
    String device_token;

    public UserModel() {
    }

    public UserModel(String email, String password, String collage, String collageId, String admin) {
        this.email = email;
        this.password = password;
        this.collage = collage;
        this.collageId = collageId;
        this.admin = admin;
    }

    public UserModel(String email, String password, String collage, String collageId, String device_token, String admin) {
        this.email = email;
        this.password = password;
        this.collage = collage;
        this.collageId = collageId;
        this.device_token = device_token;
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCollage() {
        return collage;
    }

    public void setCollage(String collage) {
        this.collage = collage;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getCollageId() {
        return collageId;
    }

    public void setCollageId(String collageId) {
        this.collageId = collageId;
    }
}