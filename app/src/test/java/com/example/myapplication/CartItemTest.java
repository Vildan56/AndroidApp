package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class CartItemTest {
    @Test
    public void testCartItemCreation() {
        CartItem cartItem = new CartItem();
        assertNotNull(cartItem);
    }

    @Test
    public void testCartItemSettersAndGetters() {
        CartItem cartItem = new CartItem();
        
        cartItem.setCartItemId("123");
        cartItem.setProductId("456");
        cartItem.setQuantity(2);
        cartItem.setName("Test Item");
        cartItem.setPrice(49.99);
        cartItem.setImageUrl("http://test.com/image.jpg");

        assertEquals("123", cartItem.getCartItemId());
        assertEquals("456", cartItem.getProductId());
        assertEquals(2, cartItem.getQuantity());
        assertEquals("Test Item", cartItem.getName());
        assertEquals(49.99, cartItem.getPrice(), 0.001);
        assertEquals("http://test.com/image.jpg", cartItem.getImageUrl());
    }

    @Test
    public void testCartItemTotalPrice() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(10.0);
        cartItem.setQuantity(3);
        
        assertEquals(30.0, cartItem.getPrice() * cartItem.getQuantity(), 0.001);
    }
} 