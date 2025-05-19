package com.example.myapplication;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        Product product = new Product();
        product.setId("p1");
        product.setName("Test Product");
        product.setPrice(10.0);
        Order order = new Order("o1", 123456789L, Arrays.asList(product));
        assertEquals("o1", order.getId());
        assertEquals(1, order.getItems().size());
        assertEquals("Test Product", order.getItems().get(0).getName());
    }
}