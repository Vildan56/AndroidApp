package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private static final String TAG = "ProductListActivity";
    private RecyclerView rvProducts;
    private List<Product> productList;
    private List<Product> cartItems;
    private ProductAdapter adapter;
    private Spinner spinnerCategory;
    private DatabaseReference productsRef;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_product_list);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading layout", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rvProducts = findViewById(R.id.rv_products);
        FloatingActionButton fabCart = findViewById(R.id.fab_cart);
        FloatingActionButton fabProfile = findViewById(R.id.fab_profile);
        spinnerCategory = findViewById(R.id.spinner_category);
        searchView = findViewById(R.id.search_view);

        if (rvProducts == null || fabCart == null || fabProfile == null || spinnerCategory == null || searchView == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Layout error: View not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cartItems = new ArrayList<>();
        productList = new ArrayList<>();

        try {
            adapter = new ProductAdapter(this, productList, this);
            rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
            rvProducts.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up adapter: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up product list", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
            startActivity(intent);
        });

        fabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        productsRef = FirebaseDatabase.getInstance().getReference("products");
        loadProducts();

        setupCategorySpinner();
        setupSearchView();
    }

    private void loadProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "No products found in database");
                    Toast.makeText(ProductListActivity.this, "No products available", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            product.setId(snapshot.getKey());
                            productList.add(product);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing product: " + e.getMessage(), e);
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Loaded " + productList.size() + " products");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(ProductListActivity.this, "Error loading products: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupCategorySpinner() {
        String[] categories = {"All", "Printing", "Frames", "Accessories"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                filterProductsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void filterProductsByCategory(String category) {
        List<Product> filteredList = new ArrayList<>();
        if (category.equals("All")) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getCategory().equals(category)) {
                    filteredList.add(product);
                }
            }
        }
        adapter.setFilteredList(filteredList);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartItems.add(product);
    }
}