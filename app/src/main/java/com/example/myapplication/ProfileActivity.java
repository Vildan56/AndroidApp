package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private DatabaseReference userRef;
    private Button btnLogout;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Current user ID: " + userId);
        ordersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("orders");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Инициализация UI элементов
        rvOrders = findViewById(R.id.rv_orders);
        btnLogout = findViewById(R.id.btn_logout);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);

        if (rvOrders == null) {
            Log.e(TAG, "RecyclerView rv_orders not found in layout");
            Toast.makeText(this, "Error: Orders view not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (tvName == null || tvPhone == null) {
            Log.e(TAG, "TextView tv_name or tv_phone not found in layout");
            Toast.makeText(this, "Error: Profile views not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (tvEmail == null) {
            Log.e(TAG, "TextView tv_email not found in layout");
            Toast.makeText(this, "Error: Email view not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);

        loadOrders();
        loadUserData();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Редактировать профиль");
            View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
            builder.setView(view);

            EditText etName = view.findViewById(R.id.et_name);
            EditText etPhone = view.findViewById(R.id.et_phone);
            etName.setText(tvName.getText());
            etPhone.setText(tvPhone.getText());

            builder.setPositiveButton("Сохранить", (dialog, which) -> {
                String newName = etName.getText().toString().trim();
                String newPhone = etPhone.getText().toString().trim();

                // Валидация имени
                if (newName.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newName.matches("^(?=.*[а-яА-Яa-zA-Z])[а-яА-Яa-zA-Z\\-\\s]{2,50}$")) {
                    Toast.makeText(ProfileActivity.this, "Имя должно содержать хотя бы одну букву и состоять из букв, пробелов или дефисов (2-50 символов)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Валидация телефона
                if (newPhone.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Телефон не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPhone.matches("^\\+?\\d{10,15}$")) {
                    Toast.makeText(ProfileActivity.this, "Телефон должен содержать только цифры (допускается + в начале), длина 10-15 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                updates.put("phone", newPhone);
                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            tvName.setText(newName);
                            tvPhone.setText(newPhone);
                            Toast.makeText(ProfileActivity.this, "Профиль обновлён", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProfileActivity.this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
                        });
            });

            builder.setNegativeButton("Отмена", null);
            builder.show();
        });
    }

    private void loadOrders() {

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue(String.class);
                Log.d(TAG, "DataSnapshot exists: " + dataSnapshot.exists());
                Log.d(TAG, "DataSnapshot children count: " + dataSnapshot.getChildrenCount());

                orderList.clear();
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "No orders found in database");
                    Toast.makeText(ProfileActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                    orderAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Log.d(TAG, "Processing order: " + snapshot.getKey());
                        Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                        if (timestamp == null) {
                            Log.w(TAG, "Timestamp missing for order: " + snapshot.getKey());
                            continue;
                        }

                        List<Product> items = new ArrayList<>();
                        DataSnapshot itemsSnapshot = snapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartItem cartItem = itemSnapshot.getValue(CartItem.class);
                            if (cartItem != null) {
                                Product product = new Product();
                                product.setId(cartItem.getProductId());
                                product.setName(cartItem.getName());
                                product.setPrice(cartItem.getPrice());
                                product.setImageUrl(cartItem.getImageUrl());
                                items.add(product);
                            } else {
                                Log.w(TAG, "Failed to parse cart item: " + itemSnapshot.getKey());
                            }
                        }

                        Order order = new Order(snapshot.getKey(), timestamp, items);
                        orderList.add(order);
                        Log.d(TAG, "Added order: " + order.getId() + " with " + items.size() + " items");
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing order: " + e.getMessage(), e);
                    }
                }

                orderAdapter.notifyDataSetChanged();
                Log.d(TAG, "Total orders loaded: " + orderList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(ProfileActivity.this, "Error loading orders: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    if (name != null) {
                        tvName.setText(name);
                    } else {
                        tvName.setText("Не указано");
                    }

                    if (email != null) {
                        tvEmail.setText(email);
                    } else {
                        tvEmail.setText("Не указано");
                    }

                    if (phone != null) {
                        tvPhone.setText(phone);
                    } else {
                        tvPhone.setText("Не указано");
                    }
                } else {
                    Log.w(TAG, "User data not found in database");
                    tvName.setText("Не указано");
                    tvPhone.setText("Не указано");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load user data: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(ProfileActivity.this, "Error loading user data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}