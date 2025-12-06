package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private final List<MenuItem> menu = new ArrayList<>();
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        RecyclerView rv = findViewById(R.id.menuRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(this, menu, this::onItemClicked);
        rv.setAdapter(adapter);

        Button add = findViewById(R.id.btnAdd);
        add.setOnClickListener(v -> showAddDialog());

        // seed data
        menu.add(new MenuItem("Burger", 5.99, "Beef burger with cheese"));
        menu.add(new MenuItem("Pizza", 8.99, "Margherita"));
        menu.add(new MenuItem("Pasta", 6.49, "Creamy Alfredo"));
        adapter.notifyDataSetChanged();
    }

    private void onItemClicked(MenuItem item) {
        // show options: change price, remove, view
        final String[] options = {"Change price", "Remove item", "View details", "Cancel"};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(item.name);
        b.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showChangePriceDialog(item);
                    break;
                case 1:
                    showRemoveConfirm(item);
                    break;
                case 2:
                    showViewDetails(item);
                    break;
                default:
                    dialog.dismiss();
            }
        });
        b.show();
    }

    private void showAddDialog() {
        View v = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        // Use simple input dialogs sequentially
        final EditText name = new EditText(this);
        name.setHint("Dish name");

        final EditText price = new EditText(this);
        price.setHint("Price (e.g. 9.99)");
        price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        final EditText desc = new EditText(this);
        desc.setHint("Short description");

        LinearLayoutCompat layout = new LinearLayoutCompat(this);
        layout.setOrientation(LinearLayoutCompat.VERTICAL);
        layout.addView(name);
        layout.addView(price);
        layout.addView(desc);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Add menu item");
        b.setView(layout);
        b.setPositiveButton("Add", (dialog, which) -> {
            String n = name.getText().toString().trim();
            String p = price.getText().toString().trim();
            String d = desc.getText().toString().trim();
            if (n.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }
            double pr;
            try {
                pr = Double.parseDouble(p);
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }
            menu.add(new MenuItem(n, pr, d));
            adapter.notifyItemInserted(menu.size() - 1);
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void showChangePriceDialog(MenuItem item) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("New price");
        input.setText(String.valueOf(item.price));

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Change price for " + item.name);
        b.setView(input);
        b.setPositiveButton("Update", (dialog, which) -> {
            String v = input.getText().toString().trim();
            try {
                double p = Double.parseDouble(v);
                item.price = p;
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Price updated", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void showRemoveConfirm(MenuItem item) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Remove " + item.name + "?");
        b.setPositiveButton("Remove", (dialog, which) -> {
            int idx = menu.indexOf(item);
            if (idx >= 0) {
                menu.remove(idx);
                adapter.notifyItemRemoved(idx);
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void showViewDetails(MenuItem item) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(item.name);
        b.setMessage("Price: $" + String.format("%.2f", item.price) + "\n\n" + item.description);
        b.setPositiveButton("OK", null);
        b.show();
    }
}
