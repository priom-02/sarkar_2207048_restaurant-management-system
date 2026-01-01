package com.example.myapplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    private static CartManager instance;
    private Map<String, CartItem> cartItems = new LinkedHashMap<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        String itemName = item.getName();
        if (cartItems.containsKey(itemName)) {
            CartItem cartItem = cartItems.get(itemName);
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItems.put(itemName, new CartItem(item, 1));
        }
    }

    public void removeItem(MenuItem item) {
        String itemName = item.getName();
        if (cartItems.containsKey(itemName)) {
            CartItem cartItem = cartItems.get(itemName);
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            } else {
                cartItems.remove(itemName);
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }

    public void clearCart() {
        cartItems.clear();
    }
}
