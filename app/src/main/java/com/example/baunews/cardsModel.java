package com.example.baunews;

public class cardsModel {
    private String title;
    private String date;
    private int image;
    private String description;

    public cardsModel() {

    }

    public cardsModel(String title, String date, int image, String description) {
        this.title = title;
        this.date = date;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
