package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/* Updated: clicking the Admin button opens AdminLogin */
public class MainActivity extends AppCompatActivity {

    private Button btnAdmin;
    private Button btnUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdmin = findViewById(R.id.btnAdmin);
        btnUser = findViewById(R.id.btnUser);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AdminLogin.class);
                startActivity(i);
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UserLogin.class);
                startActivity(i);
            }
        });

    }
}
