package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuItemDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_NAME = "extra_item_name";
    public static final String EXTRA_ITEM_PRICE = "extra_item_price";
    public static final String EXTRA_ITEM_IMAGE_URL = "extra_item_image_url";
    public static final String EXTRA_ITEM_DESC = "extra_item_desc";
    public static final String EXTRA_ITEM_CATEGORY = "extra_item_category";
    public static final String EXTRA_ITEM_STATUS = "extra_item_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get data from intent
        String itemName = getIntent().getStringExtra(EXTRA_ITEM_NAME);
        String itemPrice = getIntent().getStringExtra(EXTRA_ITEM_PRICE);
        String itemImageUrl = getIntent().getStringExtra(EXTRA_ITEM_IMAGE_URL);
        String itemDesc = getIntent().getStringExtra(EXTRA_ITEM_DESC);
        String itemCategory = getIntent().getStringExtra(EXTRA_ITEM_CATEGORY);
        String itemStatus = getIntent().getStringExtra(EXTRA_ITEM_STATUS);

        // Set data to views
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbar.setTitle(itemName);

        ImageView imageView = findViewById(R.id.ivDetailImage);
        TextView priceView = findViewById(R.id.tvDetailPrice);
        TextView descView = findViewById(R.id.tvDetailDescription);
        TextView viewOrder = findViewById(R.id.tvViewOrder);

        // Loading images from a URL requires a library like Glide or Picasso.
        // For now, we'll set a placeholder. This will be a future step.
        // imageView.setImageResource(R.drawable.placeholder);
        priceView.setText(itemPrice);
        descView.setText(itemDesc);

        FloatingActionButton fab = findViewById(R.id.fabAddToCart);
        fab.setOnClickListener(view -> {
            MenuItem item = new MenuItem(itemName, itemPrice, itemCategory, itemDesc, itemStatus, itemImageUrl);
            CartManager.getInstance().addItem(item);
            Toast.makeText(this, "Added " + itemName + " to cart", Toast.LENGTH_SHORT).show();
        });

        viewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MenuItemDetailActivity.this, CartActivity.class);
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
}
