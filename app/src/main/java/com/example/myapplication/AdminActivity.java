package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Old AdminActivity replaced by AdminDashboardActivity
public class AdminActivity extends AppCompatActivity {

    private final List<MenuItem> menu = new ArrayList<>();
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        RecyclerView rv = findViewById(R.id.adminRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(this, menu, item -> {
            // no-op for admin click
        });
        rv.setAdapter(adapter);

        Button add = findViewById(R.id.btnAddDummy);
        add.setOnClickListener(v -> {
            menu.add(new MenuItem("New Dish " + (menu.size() + 1), 9.99 + menu.size(), "Tasty new dish"));
            adapter.notifyItemInserted(menu.size() - 1);
        });

        // seed
        menu.add(new MenuItem("Burger", 5.99, "Beef burger with cheese"));
        menu.add(new MenuItem("Pizza", 8.99, "Margherita"));
        adapter.notifyDataSetChanged();
    }
}
