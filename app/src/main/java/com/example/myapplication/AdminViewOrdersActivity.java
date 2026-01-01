package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AdminViewOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.rvOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        order.setKey(dataSnapshot.getKey());
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminViewOrdersActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
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

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

        private List<Order> orders;

        public OrderAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_item, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.orderId.setText("Order ID: " + (order.getKey() != null ? order.getKey() : "N/A"));
            holder.userName.setText("Name: " + (order.getUserName() != null ? order.getUserName() : "N/A"));
            holder.userPhone.setText("Phone: " + (order.getUserPhone() != null ? order.getUserPhone() : "N/A"));
            holder.userAddress.setText("Address: " + (order.getUserAddress() != null ? order.getUserAddress() : "N/A"));
            holder.orderTotal.setText("Total: " + order.getTotalPrice());
            holder.orderStatus.setText("Status: " + order.getStatus());

            holder.removeButton.setOnClickListener(v -> {
                databaseReference.child(order.getKey()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminViewOrdersActivity.this, "Order removed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminViewOrdersActivity.this, "Failed to remove order", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView orderId, userName, userPhone, userAddress, orderTotal, orderStatus;
            Button removeButton;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderId = itemView.findViewById(R.id.tvOrderId);
                userName = itemView.findViewById(R.id.tvUserName);
                userPhone = itemView.findViewById(R.id.tvUserPhone);
                userAddress = itemView.findViewById(R.id.tvUserAddress);
                orderTotal = itemView.findViewById(R.id.tvOrderTotal);
                orderStatus = itemView.findViewById(R.id.tvOrderStatus);
                removeButton = itemView.findViewById(R.id.btnRemoveOrder);
            }
        }
    }
}
