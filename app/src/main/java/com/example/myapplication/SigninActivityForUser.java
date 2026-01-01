package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SigninActivityForUser extends AppCompatActivity {
    EditText etEmail, etPassword, etPhoneNumber, etFullName, etConfirmPassword, etAddress;
    Button btnSignup;
    TextView btnLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_for_user);
        etFullName = findViewById(R.id.Name);
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.ConfirmPassword);
        etPhoneNumber = findViewById(R.id.Phone);
        etAddress = findViewById(R.id.Address);
        btnSignup = findViewById(R.id.btnSignin);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UserLogin.class));
            finish();
        });

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(address)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Password must be >= 6 characters");
                return;
            }
            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = auth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", fullName);
                    user.put("email", email);
                    user.put("phone", phoneNumber);
                    user.put("address", address);

                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SigninActivityForUser.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserLogin.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SigninActivityForUser.this, "Error saving user data!", Toast.LENGTH_SHORT).show();
                            Log.e("FirestoreError", "Error saving user data", e);
                        });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SigninActivityForUser.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
