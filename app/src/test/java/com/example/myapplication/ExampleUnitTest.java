package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void testValidEmail() {
        assertTrue(Utils.isValidEmail("test@example.com"));
        assertFalse(Utils.isValidEmail("test.example.com"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(Utils.isValidPassword("123456"));
        assertFalse(Utils.isValidPassword("123"));
    }

    @Test
    public void testProductTotalPrice() {
        Product product = new Product();
        product.setPrice(100.0);
        int quantity = 3;
        assertEquals(300.0, product.getPrice() * quantity, 0.01);
    }
}