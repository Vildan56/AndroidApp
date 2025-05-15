package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        Button btnLogout = findViewById(R.id.btn_logout);
        RecyclerView rvOrders = findViewById(R.id.rv_orders);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            userId = user.getUid();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        loadUserData();

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
                String newName = etName.getText().toString();
                String newPhone = etPhone.getText().toString();

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

    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    if (name != null) tvName.setText(name);
                    if (email != null) tvEmail.setText(email);
                    if (phone != null) tvPhone.setText(phone);
                } else {
                    Toast.makeText(ProfileActivity.this, "Данные пользователя не найдены", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
            }
        });
    }
}