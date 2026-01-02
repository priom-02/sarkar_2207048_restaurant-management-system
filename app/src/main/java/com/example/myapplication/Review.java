package com.example.myapplication;

public class Review {
    private String userId;
    private float rating;

    public Review() {
        // Required for Firebase
    }

    public Review(String userId, float rating) {
        this.userId = userId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
