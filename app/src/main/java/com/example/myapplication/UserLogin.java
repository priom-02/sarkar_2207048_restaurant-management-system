package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {
    private TextView btnSignIn;
    EditText etEmail, etPassword;
    Button btnlogin;
    FirebaseAuth auth;
    ProgressBar progressBar;
    RadioGroup userTypeRadioGroup;
    RadioButton radioUser, radioAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        btnSignIn = findViewById(R.id.btnSignin);
        btnlogin = findViewById(R.id.btnAction);
        progressBar = findViewById(R.id.progressBar3);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("Password must be >= 6 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            int selectedId = userTypeRadioGroup.getCheckedRadioButtonId();
                            if (selectedId == R.id.radioUser) {
                                Toast.makeText(UserLogin.this, "Logged in Successfully as User", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                                finish();
                            } else if (selectedId == R.id.radioAdmin) {
                                Toast.makeText(UserLogin.this, "Logged in Successfully as Admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(UserLogin.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserLogin.this, SigninActivityForUser.class);
                startActivity(i);
            }
        });
    }
}
