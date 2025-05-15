package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private List<CartItem> cartItems;
    private CartAdapter adapter;
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rv_cart);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        cartItems = new ArrayList<>();
        adapter = new CartAdapter(this, cartItems);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);

        loadCartItems();

        ArrayList<Product> newCartItems = getIntent().getParcelableArrayListExtra("cartItems");
        if (newCartItems != null && !newCartItems.isEmpty()) {
            for (Product product : newCartItems) {
                addToCart(product);
            }
        }

        btnCheckout.setOnClickListener(v -> {
            // Логика оформления заказа
        });
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                adapter.notifyDataSetChanged();
                tvTotal.setText(String.format("Total: $%.2f", total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }

    private void addToCart(Product product) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productId", product.getId());
        cartItem.put("quantity", 1);
        cartItem.put("name", product.getName());
        cartItem.put("price", product.getPrice());
        cartItem.put("imageUrl", product.getImageUrl());

        String cartItemId = cartRef.push().getKey();
        if (cartItemId != null) {
            cartRef.child(cartItemId).setValue(cartItem);
        }
    }
}