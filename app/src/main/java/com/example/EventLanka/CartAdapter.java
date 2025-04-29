package com.example.EventLanka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.eventName.setText(item.getEventName());
        holder.eventDate.setText(item.getEventDate());
        holder.eventTime.setText(item.getEventTime());
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventTime;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.tname);
            eventDate = itemView.findViewById(R.id.tdate);
            eventTime = itemView.findViewById(R.id.ttime);
        }
    }
}
