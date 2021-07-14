package com.example.baunews.HelperClasses;

public class User {
    String email, password, collage, collageId;
    Token device_token;

    public User() {
    }

    public User(String email, String password, String collage, String collageId) {
        this.email = email;
        this.password = password;
        this.collage = collage;
        this.collageId = collageId;
    }

    public User(String email, String password, String collage, String collageId, Token device_token) {
        this.email = email;
        this.password = password;
        this.collage = collage;
        this.collageId = collageId;
        this.device_token = device_token;
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

    public Token getDevice_token() {
        return device_token;
    }

    public void setDevice_token(Token device_token) {
        this.device_token = device_token;
    }

    public String getCollageId() {
        return collageId;
    }

    public void setCollageId(String collageId) {
        this.collageId = collageId;
    }
}
