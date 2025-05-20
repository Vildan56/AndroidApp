package com.example.myapplication;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;



@RunWith(MockitoJUnitRunner.class)
public class ProductAdapterTest {

    @Mock
    private Context mockContext;
    
    @Mock
    private ProductAdapter.OnAddToCartClickListener mockListener;

    private ProductAdapter adapter;
    private List<Product> productList;

    @Before
    public void setUp() {
        productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("Test Product 1");
        product1.setPrice(10.0);
        
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Test Product 2");
        product2.setPrice(20.0);
        
        productList.add(product1);
        productList.add(product2);
        
        adapter = new ProductAdapter(mockContext, productList, mockListener);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testFilter() {
        // Test the standalone filtering logic directly
        List<Product> filteredResults = ProductAdapter.performFilteringLogic(productList, "Product 1");
        assertNotNull(filteredResults);
        assertEquals(1, filteredResults.size());
        assertEquals("Test Product 1", filteredResults.get(0).getName());

        List<Product> allResults = ProductAdapter.performFilteringLogic(productList, "");
        assertNotNull(allResults);
        assertEquals(2, allResults.size());

        List<Product> noResults = ProductAdapter.performFilteringLogic(productList, "NonExistent");
        assertNotNull(noResults);
        assertEquals(0, noResults.size());
    }

    @Test
    public void testSetFilteredList() {
        List<Product> newList = new ArrayList<>();
        Product product = new Product();
        product.setId("3");
        product.setName("New Product");
        newList.add(product);
        
        adapter.updateFilteredListData(newList);
        assertEquals(1, adapter.getFilteredList().size());
        assertEquals("New Product", adapter.getFilteredList().get(0).getName());
    }

    @Test
    public void testEmptyList() {
        adapter.updateFilteredListData(new ArrayList<>());
        assertNotNull(adapter.getFilteredList());
        assertEquals(0, adapter.getFilteredList().size());
    }

    @Test
    public void testNullList() {
        adapter.updateFilteredListData(null);
        assertNotNull(adapter.getFilteredList());
        assertEquals(0, adapter.getFilteredList().size());
    }
} 