package com.example.baunews.Models;

public class NewsModel {
    private String id;
    private String title;
    private String date;
    private String description;
    private String image;
    private String pdf;
    private String url;
    private String category;


    public NewsModel() {

    }

    public NewsModel(String id, String title, String date, String description, String image, String pdf, String url, String category) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.image = image;
        this.pdf = pdf;
        this.url = url;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
