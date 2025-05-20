package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProductTest {
    @Test
    public void testProductCreation() {
        Product product = new Product();
        assertNotNull(product);
    }

    @Test
    public void testProductSettersAndGetters() {
        Product product = new Product();
        
        product.setId("123");
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setCategory("Test Category");
        product.setDescription("Test Description");
        product.setImageUrl("http://test.com/image.jpg");

        assertEquals("123", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(99.99, product.getPrice(), 0.001);
        assertEquals("Test Category", product.getCategory());
        assertEquals("Test Description", product.getDescription());
        assertEquals("http://test.com/image.jpg", product.getImageUrl());
    }

    @Test
    public void testProductEquals() {
        Product product1 = new Product();
        product1.setId("123");
        product1.setName("Test Product");

        Product product2 = new Product();
        product2.setId("123");
        product2.setName("Test Product");

        Product product3 = new Product();
        product3.setId("456");
        product3.setName("Different Product");

        assertTrue(product1.equals(product2));
        assertFalse(product1.equals(product3));
    }
} 