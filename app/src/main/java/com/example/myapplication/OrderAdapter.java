package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date(order.getTimestamp()));
        holder.tvOrderDate.setText("Order Date: " + date);

        // Преобразуем List<Product> в List<CartItem>
        List<CartItem> cartItems = new ArrayList<>();
        for (Product product : order.getItems()) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setImageUrl(product.getImageUrl());
            cartItem.setQuantity(1); // Предполагаем, что в заказе количество 1
            cartItems.add(cartItem);
        }

        CartAdapter itemsAdapter = new CartAdapter(context, cartItems);
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderItems.setAdapter(itemsAdapter);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate;
        RecyclerView rvOrderItems;

        public OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            rvOrderItems = itemView.findViewById(R.id.rv_order_items);
        }
    }
}