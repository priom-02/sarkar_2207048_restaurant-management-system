package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminMenuManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminMenuItemAdapter adapter;
    private List<MenuIteam.MenuItem> menuItems;
    private EditText etSearch;
    private Button btnAddNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.rvAdminMenuItems);
        etSearch = findViewById(R.id.etSearch);
        btnAddNewItem = findViewById(R.id.btnAddNewItem);

        setupRecyclerView();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        btnAddNewItem.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuManagementActivity.this, AddEditMenuItemActivity.class);
            startActivity(intent);
        });
    }

    private void filter(String text) {
        List<MenuIteam.MenuItem> filteredList = new ArrayList<>();
        for (MenuIteam.MenuItem item : menuItems) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void setupRecyclerView() {
        // For now, we'll reuse the same data source as the user menu
        menuItems = new ArrayList<>();
        menuItems.add(new MenuIteam.MenuItem("Cheese Pizza", "$12.99", R.drawable.pizza, "Main Course", "..."));
        menuItems.add(new MenuIteam.MenuItem("Classic Burger", "$8.99", R.drawable.burger, "Main Course", "..."));
        menuItems.add(new MenuIteam.MenuItem("Sandwich", "$10.99", R.drawable.sandwich, "Main Course", "..."));
        menuItems.add(new MenuIteam.MenuItem("Salad", "$7.99", R.drawable.salad, "Appetizers", "..."));
        menuItems.add(new MenuIteam.MenuItem("Chocolate Cake", "$5.99", R.drawable.cake, "Desserts", "..."));
        menuItems.add(new MenuIteam.MenuItem("Coffee", "$3.99", R.drawable.coffe, "Beverages", "..."));

        adapter = new AdminMenuItemAdapter(menuItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AdminMenuItemAdapter extends RecyclerView.Adapter<AdminMenuItemAdapter.ViewHolder> {

        private List<MenuIteam.MenuItem> items;

        public AdminMenuItemAdapter(List<MenuIteam.MenuItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_menu_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MenuIteam.MenuItem item = items.get(position);
            holder.itemName.setText(item.getName());
            holder.category.setText(item.getCategory());
            holder.price.setText(item.getPrice());
            holder.status.setText("Active"); // Placeholder

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Edit: " + item.getName(), Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Delete: " + item.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void filterList(List<MenuIteam.MenuItem> filteredList) {
            items = filteredList;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, category, price, status;
            Button editButton, deleteButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.tvItemName);
                category = itemView.findViewById(R.id.tvCategory);
                price = itemView.findViewById(R.id.tvPrice);
                status = itemView.findViewById(R.id.tvStatus);
                editButton = itemView.findViewById(R.id.btnEdit);
                deleteButton = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
