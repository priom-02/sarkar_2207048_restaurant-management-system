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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminMenuManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminMenuItemAdapter adapter;
    private List<MenuItem> menuItems;
    private EditText etSearch;
    private Button btnAddNewItem;
    private DatabaseReference databaseReference;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("menuItems");

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
        List<MenuItem> filteredList = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void setupRecyclerView() {
        menuItems = new ArrayList<>();
        adapter = new AdminMenuItemAdapter(menuItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuItem menuItem = dataSnapshot.getValue(MenuItem.class);
                    if (menuItem != null) {
                        menuItem.setKey(dataSnapshot.getKey());
                        menuItems.add(menuItem);
                    }
                }
                adapter.filterList(new ArrayList<>(menuItems));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMenuManagementActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
            }
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

    private class AdminMenuItemAdapter extends RecyclerView.Adapter<AdminMenuItemAdapter.ViewHolder> {

        private List<MenuItem> items;

        public AdminMenuItemAdapter(List<MenuItem> items) {
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
            MenuItem item = items.get(position);
            holder.itemName.setText(item.getName());
            holder.category.setText(item.getCategory());
            holder.price.setText(item.getPrice());
            holder.status.setText(item.getStatus());

            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuManagementActivity.this, AddEditMenuItemActivity.class);
                intent.putExtra("MENU_ITEM_KEY", item.getKey());
                intent.putExtra("MENU_ITEM_NAME", item.getName());
                intent.putExtra("MENU_ITEM_PRICE", item.getPrice());
                intent.putExtra("MENU_ITEM_CATEGORY", item.getCategory());
                intent.putExtra("MENU_ITEM_DESCRIPTION", item.getDescription());
                intent.putExtra("MENU_ITEM_STATUS", item.getStatus());
                startActivity(intent);
            });

            holder.deleteButton.setOnClickListener(v -> {
                databaseReference.child(item.getKey()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMenuManagementActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminMenuManagementActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void filterList(List<MenuItem> filteredList) {
            this.items = filteredList;
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
