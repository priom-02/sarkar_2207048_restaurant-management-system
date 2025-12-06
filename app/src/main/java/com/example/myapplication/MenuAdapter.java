package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {

    public interface OnClick {
        void onClick(MenuItem item);
    }

    private final Context ctx;
    private final List<MenuItem> items;
    private final OnClick listener;

    public MenuAdapter(Context ctx, List<MenuItem> items, OnClick listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MenuItem it = items.get(position);
        holder.title.setText(it.name + " - $" + String.format("%.2f", it.price));
        holder.subtitle.setText(it.description);
        holder.itemView.setOnClickListener(v -> listener.onClick(it));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
