package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class ErrorHandlingTest {

    @Test
    public void testInvalidProductPrice() {
        Product product = new Product();
        product.setPrice(-10.0);
        assertTrue("Price should be positive", product.getPrice() >= 0);
    }

    @Test
    public void testEmptyProductName() {
        Product product = new Product();
        product.setName("");
        assertFalse("Product name should not be empty", product.getName().isEmpty());
    }

    @Test
    public void testInvalidCartItemQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(-1);
        assertTrue("Quantity should be positive", cartItem.getQuantity() > 0);
    }

    @Test
    public void testNullProductId() {
        Product product = new Product();
        product.setId(null);
        assertNotNull("Product ID should not be null", product.getId());
    }

    @Test
    public void testInvalidImageUrl() {
        Product product = new Product();
        product.setImageUrl("invalid_url");
        assertTrue("Image URL should be valid", 
            product.getImageUrl() != null && 
            (product.getImageUrl().startsWith("http://") || 
             product.getImageUrl().startsWith("https://")));
    }

    @Test
    public void testOrderTimestamp() {
        Order order = new Order();
        order.setTimestamp(-1L);
        assertTrue("Timestamp should be positive", order.getTimestamp() > 0);
    }

    @Test
    public void testEmptyOrderItems() {
        Order order = new Order();
        order.setItems(null);
        assertNotNull("Order items should not be null", order.getItems());
    }

    @Test
    public void testCartItemTotalPrice() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(Double.MAX_VALUE);
        cartItem.setQuantity(2);
        assertTrue("Total price should not overflow", 
            cartItem.getPrice() * cartItem.getQuantity() < Double.MAX_VALUE);
    }

    @Test
    public void testProductCategory() {
        Product product = new Product();
        product.setCategory(null);
        assertNotNull("Product category should not be null", product.getCategory());
    }
} 