package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private RecyclerView rvProducts;
    private List<Product> productList;
    private List<Product> cartItems;
    private ProductAdapter adapter; // Объявляем adapter как поле класса
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProducts = findViewById(R.id.rv_products);
        FloatingActionButton fabCart = findViewById(R.id.fab_cart);
        spinnerCategory = findViewById(R.id.spinner_category);

        cartItems = new ArrayList<>();
        productList = new ArrayList<>();
        productList.add(new Product("Camera Lens", 50.00, "https://via.placeholder.com/150", "Accessories"));
        productList.add(new Product("Photo Frame", 10.00, "https://via.placeholder.com/150", "Frames"));

        adapter = new ProductAdapter(this, productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);

        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
            startActivity(intent);
        });

        setupCategorySpinner(); // Настраиваем Spinner в отдельном методе
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

    private void filterProductsByCategory(String category) {
        List<Product> filteredList = new ArrayList<>();
        if (category.equals("All")) {
            filteredList.addAll(productList); // Используем productList вместо productListFull
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
}