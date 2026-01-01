package com.example.myapplication;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Order {
    private String key;
    private String userId;
    private String userName;
    private String userPhone;
    private String userAddress;
    private List<CartItem> items;
    private String totalPrice;
    private long timestamp;
    private String status;

    public Order() {
        // Required for Firebase
    }

    public Order(String userId, String userName, String userPhone, String userAddress, List<CartItem> items, String totalPrice, long timestamp, String status) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.items = items;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.status = status;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Other Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
