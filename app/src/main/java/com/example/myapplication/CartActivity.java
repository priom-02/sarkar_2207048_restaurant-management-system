package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView emptyCartTextView;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.rvCartItems);
        emptyCartTextView = findViewById(R.id.tvEmptyCart);
        checkoutButton = findViewById(R.id.checkoutButton);

        setupRecyclerView();
        updateCartView();

        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(CartManager.getInstance().getCartItems());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void updateCartView() {
        List<CartItem> currentCartItems = CartManager.getInstance().getCartItems();
        adapter.setItems(currentCartItems);

        if (currentCartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyCartTextView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
            checkoutButton.setText("Proceed to Checkout");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCartTextView.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
            updateTotalAmount();
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            try {
                total += Double.parseDouble(item.getMenuItem().getPrice().replace("$", "")) * item.getQuantity();
            } catch (NumberFormatException e) {
                // Handle cases where price might not be a valid number
            }
        }
        checkoutButton.setText(String.format(Locale.getDefault(), "Proceed to Checkout (%.2f)", total));
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<CartItem> cartItems;

        public CartAdapter(List<CartItem> cartItems) {
            this.cartItems = cartItems;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            CartItem item = cartItems.get(position);
            holder.itemName.setText(item.getMenuItem().getName());
            holder.itemPrice.setText(item.getMenuItem().getPrice());
            holder.quantity.setText(String.valueOf(item.getQuantity()));

            holder.increaseButton.setOnClickListener(v -> {
                CartManager.getInstance().addItem(item.getMenuItem());
                updateCartView();
            });

            holder.decreaseButton.setOnClickListener(v -> {
                CartManager.getInstance().removeItem(item.getMenuItem());
                updateCartView();
            });
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public void setItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            notifyDataSetChanged();
        }

        class CartViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, itemPrice, quantity;
            Button increaseButton, decreaseButton;

            public CartViewHolder(@NonNull View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.tvCartItemName);
                itemPrice = itemView.findViewById(R.id.tvCartItemPrice);
                quantity = itemView.findViewById(R.id.tvQuantity);
                increaseButton = itemView.findViewById(R.id.btnIncrease);
                decreaseButton = itemView.findViewById(R.id.btnDecrease);
            }
        }
    }
}
