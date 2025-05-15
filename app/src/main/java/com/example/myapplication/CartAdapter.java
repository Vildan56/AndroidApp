package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private DatabaseReference cartRef;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvCartName.setText(item.getName());
        holder.tvCartPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImageUrl()).into(holder.ivCartItem);

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            updateCartItemInDatabase(item);
            notifyDataSetChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                updateCartItemInDatabase(item);
                notifyDataSetChanged();
            }
        });
    }

    private void updateCartItemInDatabase(CartItem item) {
        cartRef.child(item.getCartItemId()).setValue(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartItem;
        TextView tvCartName, tvCartPrice, tvQuantity;
        Button btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartItem = itemView.findViewById(R.id.iv_cart_item);
            tvCartName = itemView.findViewById(R.id.tv_cart_name);
            tvCartPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}