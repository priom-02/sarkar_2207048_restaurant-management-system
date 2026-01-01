package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    private Button btnViewMenu, btnAddNewItem, btnViewOrders, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnAddNewItem = findViewById(R.id.btnAddNewItem);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewMenu.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboard.this, AdminMenuManagementActivity.class));
        });

        btnAddNewItem.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboard.this, AddEditMenuItemActivity.class));
        });

        btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboard.this, AdminViewOrdersActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboard.this, UserLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
