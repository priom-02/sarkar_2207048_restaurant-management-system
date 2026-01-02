package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

    private static final String CHANNEL_ID = "order_status_channel";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

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

        createNotificationChannel();
        requestNotificationPermission();
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Status";
            String description = "Channel for order status notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void sendNotification(String title, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, handle accordingly
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_restaurant_menu) // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Notification permission is required to send order updates.", Toast.LENGTH_LONG).show();
            }
        }
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

            holder.acceptButton.setOnClickListener(v -> {
                databaseReference.child(order.getKey()).child("status").setValue("Accepted").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminViewOrdersActivity.this, "Order accepted", Toast.LENGTH_SHORT).show();
                        sendNotification("Order Accepted", "Your order " + order.getKey() + " has been accepted.");
                    } else {
                        Toast.makeText(AdminViewOrdersActivity.this, "Failed to accept order", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.removeButton.setOnClickListener(v -> {
                databaseReference.child(order.getKey()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminViewOrdersActivity.this, "Order removed successfully", Toast.LENGTH_SHORT).show();
                        sendNotification("Order Removed", "Your order " + order.getKey() + " has been removed.");
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
            Button acceptButton, removeButton;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderId = itemView.findViewById(R.id.tvOrderId);
                userName = itemView.findViewById(R.id.tvUserName);
                userPhone = itemView.findViewById(R.id.tvUserPhone);
                userAddress = itemView.findViewById(R.id.tvUserAddress);
                orderTotal = itemView.findViewById(R.id.tvOrderTotal);
                orderStatus = itemView.findViewById(R.id.tvOrderStatus);
                acceptButton = itemView.findViewById(R.id.btnAcceptOrder);
                removeButton = itemView.findViewById(R.id.btnRemoveOrder);
            }
        }
    }
}
