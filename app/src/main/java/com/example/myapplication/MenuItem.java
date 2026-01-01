package com.example.myapplication;

import com.google.firebase.database.Exclude;

public class MenuItem {
    private String key;
    private String name;
    private String price;
    private String category;
    private String description;
    private String status;
    private String imageUrl;

    // Required for Firebase
    public MenuItem() {
    }

    public MenuItem(String name, String price, String category, String description, String status, String imageUrl) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
