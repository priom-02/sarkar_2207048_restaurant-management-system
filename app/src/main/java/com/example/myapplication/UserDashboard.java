package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserDashboard extends AppCompatActivity {

    private Button btnViewMenu, btnProfile, btnOrdersHistory, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnProfile = findViewById(R.id.btnProfile);
        btnOrdersHistory = findViewById(R.id.btnOrdersHistory);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewMenu.setOnClickListener(v -> {
            startActivity(new Intent(UserDashboard.this, MenuIteam.class));
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(UserDashboard.this, ProfileActivity.class));
        });

        btnOrdersHistory.setOnClickListener(v -> {
            startActivity(new Intent(UserDashboard.this, OrderHistoryActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserDashboard.this, UserLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
