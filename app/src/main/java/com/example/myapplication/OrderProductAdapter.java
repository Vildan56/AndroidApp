package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder> {
    private List<CartItem> cartItems;
    private Context context;

    public OrderProductAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvProductPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvProductQuantity.setText("Quantity: " + item.getQuantity());
        
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.ivProduct);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class OrderProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName, tvProductPrice, tvProductQuantity;

        public OrderProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_order_product);
            tvProductName = itemView.findViewById(R.id.tv_order_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_order_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_order_product_quantity);
        }
    }
} 