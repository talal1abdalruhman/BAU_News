package com.example.baunews.Models;

public class PressKitModel {
    private String id;
    private String title;
    private String resource;
    private String date;
    private String description;
    private String image;
    private String pdf;
    private String url;
    private String category;

    public PressKitModel() {
    }

    public PressKitModel(String id, String title, String resource, String date, String description, String image, String pdf, String url, String category) {
        this.id = id;
        this.title = title;
        this.resource = resource;
        this.date = date;
        this.description = description;
        this.image = image;
        this.pdf = pdf;
        this.url = url;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
