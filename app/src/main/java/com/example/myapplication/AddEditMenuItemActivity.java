package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

public class AddEditMenuItemActivity extends AppCompatActivity {

    private TextInputEditText etItemName, etPrice, etDescription;
    private AutoCompleteTextView actvCategory, actvStatus;
    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        actvStatus = findViewById(R.id.actvStatus);
        btnAddItem = findViewById(R.id.btnAddItem);

        // Populate dropdowns
        String[] categories = {"Main Course", "Appetizers", "Desserts", "Beverages"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(categoryAdapter);

        String[] statuses = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        actvStatus.setAdapter(statusAdapter);

        btnAddItem.setOnClickListener(v -> {
            // In the next step, we'll save this data to Firestore
            Toast.makeText(this, "Add Item clicked. (Functionality to be implemented)", Toast.LENGTH_SHORT).show();
        });
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
