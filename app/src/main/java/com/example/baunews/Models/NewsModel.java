package com.example.baunews.Models;

public class NewsModel {
    private String id;
    private String title;
    private String date;
    private String description;
    private String image;
    private String imageName;
    private String pdf;
    private String pdfName;
    private String url;
    private String category;
    private String resourceName;
    private String resourceLink;


    public NewsModel() {

    }

    public NewsModel(String id, String title, String date, String description, String image, String imageName, String pdf, String pdfName, String url, String category) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.image = image;
        this.imageName = imageName;
        this.pdf = pdf;
        this.pdfName = pdfName;
        this.url = url;
        this.category = category;
    }

    public NewsModel(String id, String title, String date, String description, String image, String imageName, String pdf, String pdfName, String url, String category, String resourceName, String resourceLink) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.image = image;
        this.imageName = imageName;
        this.pdf = pdf;
        this.pdfName = pdfName;
        this.url = url;
        this.category = category;
        this.resourceName = resourceName;
        this.resourceLink = resourceLink;
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

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceLink() {
        return resourceLink;
    }

    public void setResourceLink(String resourceLink) {
        this.resourceLink = resourceLink;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
