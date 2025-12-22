package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class UserDashboard extends AppCompatActivity {

    private Button btnViewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboard.this, MenuIteam.class);
                startActivity(i);
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent (getApplicationContext(), UserLogin.class));
        finish();
    }
}
