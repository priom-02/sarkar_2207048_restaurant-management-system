package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private TextView tvNoOrders;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.rvOrderHistory);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setupRecyclerView();
        loadOrderHistory();
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrderHistory() {
        if (currentUser == null) return;

        Query userOrdersQuery = databaseReference.orderByChild("userId").equalTo(currentUser.getUid());

        userOrdersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();

                if (orderList.isEmpty()) {
                    tvNoOrders.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoOrders.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load order history.", Toast.LENGTH_SHORT).show();
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

    private class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

        private List<Order> orders;

        public OrderHistoryAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_item, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateString = sdf.format(new Date(order.getTimestamp()));
            holder.orderDate.setText("Order Date: " + dateString);

            StringBuilder itemsBuilder = new StringBuilder();
            for (CartItem item : order.getItems()) {
                itemsBuilder.append(String.format(Locale.getDefault(), "- %s x %d\n", item.getMenuItem().getName(), item.getQuantity()));
            }
            holder.orderItems.setText(itemsBuilder.toString());
            holder.orderTotal.setText("Total: " + order.getTotalPrice());
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView orderDate, orderItems, orderTotal;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderDate = itemView.findViewById(R.id.tvOrderDate);
                orderItems = itemView.findViewById(R.id.tvOrderItems);
                orderTotal = itemView.findViewById(R.id.tvOrderHistoryTotal);
            }
        }
    }
}
