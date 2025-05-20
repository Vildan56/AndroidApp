package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class ErrorHandlingTest {

    @Test
    public void testInvalidProductPrice() {
        Product product = new Product();
        product.setPrice(-10.0);
        assertEquals(-10.0, product.getPrice(), 0.001);
    }

    @Test
    public void testEmptyProductName() {
        Product product = new Product();
        product.setName("");
        assertTrue("Product name should be empty if set to empty", product.getName().isEmpty());
    }

    @Test
    public void testInvalidCartItemQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(-1);
        assertEquals(-1, cartItem.getQuantity());
    }

    @Test
    public void testNullProductId() {
        Product product = new Product();
        assertNull("Product ID should be null by default", product.getId());
    }

    @Test
    public void testInvalidImageUrl() {
        Product product = new Product();
        product.setImageUrl("invalid_url");
        assertEquals("invalid_url", product.getImageUrl());
    }

    @Test
    public void testOrderTimestamp() {
        Order order = new Order();
        order.setTimestamp(-1L);
        assertEquals(-1L, (long)order.getTimestamp());
    }

    @Test
    public void testEmptyOrderItems() {
        Order order = new Order();
        assertNull("Order items should be null by default", order.getItems());
    }

    @Test
    public void testCartItemTotalPrice() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(1000000.0);
        cartItem.setQuantity(2);
        double expectedTotalPrice = 2000000.0;
        assertEquals(expectedTotalPrice, cartItem.getPrice() * cartItem.getQuantity(), 0.001);
    }

    @Test
    public void testProductCategory() {
        Product product = new Product();
        assertNull("Product category should be null by default", product.getCategory());
    }
} 