package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    private EditText emailEt;
    private EditText passwordEt;
    private Button actionBtn;
    private TextView subtitleTv;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "admin_prefs";
    private static final String KEY_EMAIL = "admin_email";
    private static final String KEY_PASS = "admin_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailEt = findViewById(R.id.etEmail);
        passwordEt = findViewById(R.id.etPassword);
        actionBtn = findViewById(R.id.btnAction);
        subtitleTv = findViewById(R.id.tvSubtitle);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_EMAIL, null);

        final boolean isSignup = (savedEmail == null);
        if (isSignup) {
            actionBtn.setText("Create Admin");
            subtitleTv.setText("First time setup: create admin credentials");
        } else {
            actionBtn.setText("Sign In");
            subtitleTv.setText("Enter admin credentials to continue");
            emailEt.setText(savedEmail);
        }

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEt.getText().toString().trim();
                String pass = passwordEt.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminLogin.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(AdminLogin.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isSignup) {
                    prefs.edit()
                            .putString(KEY_EMAIL, email)
                            .putString(KEY_PASS, pass)
                            .apply();
                    Toast.makeText(AdminLogin.this, "Admin created. Signing in...", Toast.LENGTH_SHORT).show();
                    openMain();
                } else {
                    String storedEmail = prefs.getString(KEY_EMAIL, "");
                    String storedPass = prefs.getString(KEY_PASS, "");
                    if (email.equals(storedEmail) && pass.equals(storedPass)) {
                        Toast.makeText(AdminLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                        openMain();
                    } else {
                        Toast.makeText(AdminLogin.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void openMain() {
        Intent intent = new Intent(AdminLogin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
