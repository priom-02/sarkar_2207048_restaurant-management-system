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

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuIteam extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuItemAdapter adapter;
    private List<MenuItem> allMenuItems;
    private ChipGroup chipGroup;
    private TextView viewOrderButton;

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

        setupRecyclerView();
        setupChipGroupListener();

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
        allMenuItems.add(new MenuItem("Cheese Pizza", "$12.99", R.drawable.pizza, "Main Course", "A classic cheese pizza with a rich tomato sauce and a blend of mozzarella and cheddar cheese."));
        allMenuItems.add(new MenuItem("Classic Burger", "$8.99", R.drawable.burger, "Main Course", "A juicy beef patty with lettuce, tomato, onion, and our special sauce, served on a toasted bun."));
        allMenuItems.add(new MenuItem("Sandwich", "$10.99", R.drawable.sandwich, "Main Course", "A delicious sandwich with your choice of fillings, served on freshly baked bread."));
        allMenuItems.add(new MenuItem("Salad", "$7.99", R.drawable.salad, "Appetizers", "A fresh and healthy salad with a variety of greens and a light vinaigrette dressing."));
        allMenuItems.add(new MenuItem("Chocolate Cake", "$5.99", R.drawable.cake, "Desserts", "A rich and moist chocolate cake with a decadent fudge frosting."));
        allMenuItems.add(new MenuItem("Coffee", "$3.99", R.drawable.coffe, "Beverages", "A freshly brewed cup of coffee, perfect to start your day or as an after-meal treat."));
        allMenuItems.add(new MenuItem("French Fries", "$4.50", R.drawable.fries, "Appetizers", "Crispy golden french fries, lightly salted and served hot."));
        allMenuItems.add(new MenuItem("Ice Cream", "$1.99", R.drawable.icecream, "Desserts", "A scoop of our delicious ice cream, available in a variety of flavors."));

        adapter = new MenuItemAdapter(new ArrayList<>(allMenuItems));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupChipGroupListener() {
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

    public static class MenuItem {
        private String name;
        private String price;
        private int imageResource;
        private String category;
        private String description;

        public MenuItem(String name, String price, int imageResource, String category, String description) {
            this.name = name;
            this.price = price;
            this.imageResource = imageResource;
            this.category = category;
            this.description = description;
        }

        public String getName() { return name; }
        public String getPrice() { return price; }
        public int getImageResource() { return imageResource; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
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
            holder.itemImage.setImageResource(item.getImageResource());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MenuItemDetailActivity.class);
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_NAME, item.getName());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_PRICE, item.getPrice());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_IMAGE, item.getImageResource());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_DESC, item.getDescription());
                intent.putExtra(MenuItemDetailActivity.EXTRA_ITEM_CATEGORY, item.getCategory());
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
            menuItems = filteredList;
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
