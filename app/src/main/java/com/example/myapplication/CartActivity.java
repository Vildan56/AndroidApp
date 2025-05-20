package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private TextView tvTotal;
    private List<CartItem> cartItems;
    private CartAdapter cartAdapter;
    private DatabaseReference cartRef;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView rvCart = findViewById(R.id.rv_cart);
        tvTotal = findViewById(R.id.tv_total);
        Button btnCheckout = findViewById(R.id.btn_checkout);

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
        ordersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("orders");

        loadCartItems();

        ArrayList<Product> newCartItems = getIntent().getParcelableArrayListExtra("cartItems");
        if (newCartItems != null && !newCartItems.isEmpty()) {
            for (Product product : newCartItems) {
                addToCart(product);
            }
        }

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Показываем всплывающее окно для подтверждения заказа
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("Confirm the order");
            builder.setMessage("Your order has been successfully placed!");
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Сохраняем заказ в Firebase
                String orderId = ordersRef.push().getKey();
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("timestamp", System.currentTimeMillis());
                orderData.put("items", cartItems);

                Log.d(TAG, "Saving order with ID: " + orderId);
                Log.d(TAG, "Order data: " + orderData.toString());

                ordersRef.child(orderId).setValue(orderData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Order saved successfully");
                            // Очищаем корзину в базе данных после успешного сохранения заказа
                            cartRef.removeValue()
                                    .addOnSuccessListener(aVoid2 -> {
                                        Log.d(TAG, "Cart cleared successfully");
                                        Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                        cartItems.clear();
                                        cartAdapter.notifyDataSetChanged();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to clear cart: " + e.getMessage(), e);
                                        AlertDialog.Builder errorBuilder = new AlertDialog.Builder(CartActivity.this);
                                        errorBuilder.setTitle("Mistake");
                                        errorBuilder.setMessage("Couldn't empty the trash: " + e.getMessage());
                                        errorBuilder.setPositiveButton("OK", null);
                                        errorBuilder.show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to save order: " + e.getMessage(), e);
                            Toast.makeText(this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItems.clear();
                double total = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem item = snapshot.getValue(CartItem.class);
                    if (item != null) {
                        item.setCartItemId(snapshot.getKey());
                        cartItems.add(item);
                        total += item.getPrice() * item.getQuantity();
                    }
                }
                cartAdapter.notifyDataSetChanged();
                tvTotal.setText(String.format("Total: $%.2f", total));
                Log.d(TAG, "Loaded " + cartItems.size() + " items into cart");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load cart items: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(CartActivity.this, "Error loading cart: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addToCart(Product product) {
        // Проверяем, есть ли уже такой продукт в корзине
        cartRef.orderByChild("productId").equalTo(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Продукт уже есть в корзине, увеличиваем количество
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem existingItem = snapshot.getValue(CartItem.class);
                        if (existingItem != null) {
                            existingItem.setQuantity(existingItem.getQuantity() + 1);
                            cartRef.child(snapshot.getKey()).setValue(existingItem)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Incremented quantity for product: " + product.getName());
                                    Toast.makeText(CartActivity.this, "Product quantity updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error updating quantity: " + e.getMessage());
                                    Toast.makeText(CartActivity.this, "Error updating quantity", Toast.LENGTH_SHORT).show();
                                });
                        }
                    }
                } else {
                    // Продукта нет в корзине, добавляем новый
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("productId", product.getId());
                    cartItem.put("quantity", 1);
                    cartItem.put("name", product.getName());
                    cartItem.put("price", product.getPrice());
                    cartItem.put("imageUrl", product.getImageUrl());

                    String cartItemId = cartRef.push().getKey();
                    if (cartItemId != null) {
                        cartRef.child(cartItemId).setValue(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Added new product to cart: " + product.getName());
                                Toast.makeText(CartActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error adding product: " + e.getMessage());
                                Toast.makeText(CartActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
                            });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking cart: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(CartActivity.this, "Error checking cart: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}