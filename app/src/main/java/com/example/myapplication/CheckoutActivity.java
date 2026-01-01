package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private RadioGroup rgPaymentMethod;
    private Button btnPlaceOrder;
    private DatabaseReference databaseReference;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnPlaceOrder.setOnClickListener(v -> {
            int selectedId = rgPaymentMethod.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Confirm Order")
                    .setMessage("Are you sure you want to place this order?")
                    .setPositiveButton("Yes", (dialog, which) -> fetchUserAndPlaceOrder())
                    .setNegativeButton("No", null)
                    .show();
            }
        });
    }

    private void fetchUserAndPlaceOrder() {
        if (currentUser == null) return;

        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String phone = documentSnapshot.getString("phone");
                String address = documentSnapshot.getString("address");
                placeOrder(name, phone, address);
            }
        });
    }

    private void placeOrder(String name, String phone, String address) {
        String userId = currentUser.getUid();
        String orderId = databaseReference.push().getKey();
        double total = 0;
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            try {
                total += Double.parseDouble(item.getMenuItem().getPrice().replace("$", "")) * item.getQuantity();
            } catch (NumberFormatException e) { /* Do nothing */ }
        }

        Order order = new Order(
            userId, name, phone, address,
            CartManager.getInstance().getCartItems(),
            String.format(Locale.getDefault(), "$%.2f", total),
            System.currentTimeMillis(),
            "Pending"
        );

        if (orderId != null) {
            databaseReference.child(orderId).setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    CartManager.getInstance().clearCart();
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
