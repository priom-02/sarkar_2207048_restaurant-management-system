package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SigninActivityForUser extends AppCompatActivity {
    EditText etEmail, etPassword, etPhoneNumber, etFullName;
    Button btnSignup;
    FirebaseAuth auth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_for_user);
        etFullName = findViewById(R.id.Name);
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        etPhoneNumber = findViewById(R.id.Phone);
        btnSignup = findViewById(R.id.btnSignin);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
            finish();
        }
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                if(TextUtils.isEmpty(fullName)){
                    etFullName.setError("Full Name is required");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }
                if(password.length()<6){
                    etPassword.setError("Password must be >= 6 characters");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)) {
                    etPhoneNumber.setError("Phone Number is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SigninActivityForUser.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),  UserDashboard.class));
                            finish();
                        } else {
                            Toast.makeText(SigninActivityForUser.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }
}