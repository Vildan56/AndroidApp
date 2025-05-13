package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private RecyclerView rvProducts;
    private List<Product> productList;
    private List<Product> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        rvProducts = findViewById(R.id.rv_products);
        FloatingActionButton fabCart = findViewById(R.id.fab_cart);
        cartItems = new ArrayList<>();

        productList = new ArrayList<>();
        productList.add(new Product("Photo Print 10x15", 2.00, "https://via.placeholder.com/150"));
        productList.add(new Product("Camera Lens", 50.00, "https://via.placeholder.com/150"));
        productList.add(new Product("Photo Frame", 10.00, "https://via.placeholder.com/150"));

        ProductAdapter adapter = new ProductAdapter(this, productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);

        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
            startActivity(intent);
        });
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartItems.add(product);
    }
}