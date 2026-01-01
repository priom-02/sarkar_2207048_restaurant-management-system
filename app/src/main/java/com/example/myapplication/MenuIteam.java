package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuIteam extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuItemAdapter adapter;
    private List<MenuItem> allMenuItems;
    private ChipGroup chipGroup;
    private TextView viewOrderButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_iteam);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Our Menu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.rv_menu_items);
        chipGroup = findViewById(R.id.chip_group);
        viewOrderButton = findViewById(R.id.tvViewOrder);

        databaseReference = FirebaseDatabase.getInstance().getReference("menuItems");

        setupRecyclerView();
        setupChipGoupListener();

        viewOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuIteam.this, CartActivity.class);
            startActivity(intent);
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

    private void setupRecyclerView() {
        allMenuItems = new ArrayList<>();
        adapter = new MenuItemAdapter(allMenuItems);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMenuItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuItem menuItem = dataSnapshot.getValue(MenuItem.class);
                    if (menuItem != null && "Active".equals(menuItem.getStatus())) {
                        allMenuItems.add(menuItem);
                    }
                }
                adapter.filterList(new ArrayList<>(allMenuItems));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuIteam.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChipGoupListener() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all) {
                adapter.filterList(new ArrayList<>(allMenuItems));
            } else if (checkedId == R.id.chip_appetizers) {
                filterList("Appetizers");
            } else if (checkedId == R.id.chip_main_course) {
                filterList("Main Course");
            } else if (checkedId == R.id.chip_desserts) {
                filterList("Desserts");
            } else if (checkedId == R.id.chip_beverages) {
                filterList("Beverages");
            }
        });
    }

    private void filterList(String category) {
        List<MenuItem> filteredList = allMenuItems.stream()
                .filter(item -> item.getCategory().equals(category))
                .collect(Collectors.toList());
        adapter.filterList(filteredList);
    }

    private class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

        private List<MenuItem> menuItems;

        public MenuItemAdapter(List<MenuItem> menuItems) {
            this.menuItems = menuItems;
        }

        @NonNull
        @Override
        public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
            return new MenuItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
            MenuItem item = menuItems.get(position);
            holder.itemName.setText(item.getName());
            holder.itemPrice.setText(item.getPrice());

            Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.baseline_restaurant_24) // Optional placeholder
                .into(holder.itemImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MenuItemDetailActivity.class);
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_NAME, item.getName());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_PRICE, item.getPrice());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_IMAGE_URL, item.getImageUrl());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_DESC, item.getDescription());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_CATEGORY, item.getCategory());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_STATUS, item.getStatus());
                v.getContext().startActivity(intent);
            });

            holder.addToOrderButton.setOnClickListener(v -> {
                CartManager.getInstance().addItem(item);
                Toast.makeText(v.getContext(), "Added " + item.getName() + " to order", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }

        public void filterList(List<MenuItem> filteredList) {
            this.menuItems = filteredList;
            notifyDataSetChanged();
        }

        class MenuItemViewHolder extends RecyclerView.ViewHolder {
            ImageView itemImage;
            TextView itemName;
            TextView itemPrice;
            Button addToOrderButton;

            public MenuItemViewHolder(@NonNull View itemView) {
                super(itemView);
                itemImage = itemView.findViewById(R.id.iv_item_image);
                itemName = itemView.findViewById(R.id.tv_item_name);
                itemPrice = itemView.findViewById(R.id.tv_item_price);
                addToOrderButton = itemView.findViewById(R.id.btn_add_to_order);
            }
        }
    }
}
