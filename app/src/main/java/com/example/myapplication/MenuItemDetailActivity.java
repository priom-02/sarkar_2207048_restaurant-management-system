package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MenuItemDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_NAME = "extra_item_name";
    public static final String EXTRA_ITEM_PRICE = "extra_item_price";
    public static final String EXTRA_ITEM_IMAGE_URL = "extra_item_image_url";
    public static final String EXTRA_ITEM_DESC = "extra_item_desc";
    public static final String EXTRA_ITEM_CATEGORY = "extra_item_category";
    public static final String EXTRA_ITEM_STATUS = "extra_item_status";

    private DatabaseReference reviewsRef;
    private String itemName;

    private RatingBar averageRatingBar;
    private TextView averageRatingText;
    private LinearLayout averageRatingLayout;

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

        itemName = getIntent().getStringExtra(EXTRA_ITEM_NAME);
        reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(itemName);

        // Initialize views for average rating
        averageRatingBar = findViewById(R.id.rb_average_rating);
        averageRatingText = findViewById(R.id.tv_average_rating_text);
        averageRatingLayout = findViewById(R.id.layout_average_rating);

        // Get data from intent and set to views
        setupViewsFromIntent();

        // Setup review submission
        setupReviewSubmission();

        // Load and display average rating
        loadAverageRating();
    }

    private void setupViewsFromIntent() {
        String itemPrice = getIntent().getStringExtra(EXTRA_ITEM_PRICE);
        String itemImageUrl = getIntent().getStringExtra(EXTRA_ITEM_IMAGE_URL);
        String itemDesc = getIntent().getStringExtra(EXTRA_ITEM_DESC);
        String itemCategory = getIntent().getStringExtra(EXTRA_ITEM_CATEGORY);
        String itemStatus = getIntent().getStringExtra(EXTRA_ITEM_STATUS);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbar.setTitle(itemName);

        ImageView imageView = findViewById(R.id.ivDetailImage);
        TextView priceView = findViewById(R.id.tvDetailPrice);
        TextView descView = findViewById(R.id.tvDetailDescription);

        if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
            Glide.with(this).load(itemImageUrl).placeholder(R.drawable.baseline_restaurant_24).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.baseline_restaurant_24);
        }

        priceView.setText(itemPrice);
        descView.setText(itemDesc);

        FloatingActionButton fab = findViewById(R.id.fabAddToCart);
        fab.setOnClickListener(view -> {
            MenuItem menuItem = new MenuItem(itemName, itemPrice, itemCategory, itemDesc, itemStatus, itemImageUrl);
            CartManager.getInstance().addItem(menuItem);
            Toast.makeText(this, itemName + " added to cart", Toast.LENGTH_SHORT).show();
        });

        TextView viewOrder = findViewById(R.id.tvViewOrder);
        viewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MenuItemDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void setupReviewSubmission() {
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        btnSubmitReview.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                return;
            }

            String reviewId = reviewsRef.push().getKey();
            Review review = new Review(userId, rating);

            if (reviewId != null) {
                reviewsRef.child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Review submitted! Thank you!", Toast.LENGTH_SHORT).show();
                        ratingBar.setRating(0);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit review.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadAverageRating() {
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float totalRating = 0;
                    long reviewCount = 0;

                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            totalRating += review.getRating();
                            reviewCount++;
                        }
                    }

                    if (reviewCount > 0) {
                        float average = totalRating / reviewCount;
                        averageRatingBar.setRating(average);
                        averageRatingText.setText(String.format(Locale.getDefault(), "%.1f (%d Reviews)", average, reviewCount));
                        averageRatingLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    averageRatingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuItemDetailActivity.this, "Failed to load ratings.", Toast.LENGTH_SHORT).show();
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
}
