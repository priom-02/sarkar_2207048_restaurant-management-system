package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private TextInputEditText etItemName, etPrice, etDescription;
    private AutoCompleteTextView actvCategory, actvStatus;
    private Button btnAddItem;
    private DatabaseReference databaseReference;

    private String menuItemKey;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        actvStatus = findViewById(R.id.actvStatus);
        btnAddItem = findViewById(R.id.btnAddItem);

        databaseReference = FirebaseDatabase.getInstance().getReference("menuItems");

        // Dropdowns
        String[] categories = {"Main Course", "Appetizers", "Desserts", "Beverages"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(categoryAdapter);

        String[] statuses = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        actvStatus.setAdapter(statusAdapter);

        // Check if we are in Edit Mode
        if (getIntent().hasExtra("MENU_ITEM_KEY")) {
            isEditMode = true;
            menuItemKey = getIntent().getStringExtra("MENU_ITEM_KEY");
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Menu Item");
            }
            btnAddItem.setText("Update Item");

            // Pre-fill the fields
            etItemName.setText(getIntent().getStringExtra("MENU_ITEM_NAME"));
            etPrice.setText(getIntent().getStringExtra("MENU_ITEM_PRICE").replace("$", ""));
            actvCategory.setText(getIntent().getStringExtra("MENU_ITEM_CATEGORY"), false);
            etDescription.setText(getIntent().getStringExtra("MENU_ITEM_DESCRIPTION"));
            actvStatus.setText(getIntent().getStringExtra("MENU_ITEM_STATUS"), false);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Menu Item");
            }
            // **THE FIX IS HERE: Set default status for new items**
            actvStatus.setText(statuses[0], false);
        }

        btnAddItem.setOnClickListener(v -> {
            saveMenuItem();
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void saveMenuItem() {
        String name = etItemName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String status = actvStatus.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || category.isEmpty() || description.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        MenuItem updatedItem = new MenuItem(name, "$" + price, category, description, status, "");

        if (isEditMode) {
            databaseReference.child(menuItemKey).setValue(updatedItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddEditMenuItemActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleFailure(task.getException());
                }
            });
        } else {
            String itemId = databaseReference.push().getKey();
            if (itemId != null) {
                databaseReference.child(itemId).setValue(updatedItem).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEditMenuItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        handleFailure(task.getException());
                    }
                });
            }
        }
    }

    private void handleFailure(Exception e) {
        String errorMessage = "Operation failed.";
        if (e != null) {
            errorMessage += " Error: " + e.getMessage();
            Log.e("FirebaseError", "Operation failed", e);
        }
        Toast.makeText(AddEditMenuItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
