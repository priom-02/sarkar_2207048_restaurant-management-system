package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etAddress;
    private Button btnSaveProfile;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadUserProfile();
        }

        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                etFullName.setText(documentSnapshot.getString("fullName"));
                etEmail.setText(documentSnapshot.getString("email"));
                etPhone.setText(documentSnapshot.getString("phone"));
                etAddress.setText(documentSnapshot.getString("address"));
            }
        });
    }

    private void saveUserProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = db.collection("users").document(currentUser.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("phone", phone);
        updates.put("address", address);

        docRef.update(updates)
            .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
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
