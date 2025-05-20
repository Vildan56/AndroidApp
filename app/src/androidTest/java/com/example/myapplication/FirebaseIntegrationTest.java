package com.example.myapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirebaseIntegrationTest {

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private String testUserId;

    @Before
    public void setUp() {
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // Здесь должна быть логика для создания тестового пользователя
    }

    @Test
    public void testAddToCart() {
        // Тест добавления товара в корзину
        Product product = new Product();
        product.setId("test_product_id");
        product.setName("Test Product");
        product.setPrice(99.99);

        DatabaseReference cartRef = databaseRef.child("carts").child(testUserId);
        String cartItemId = cartRef.push().getKey();
        assertNotNull(cartItemId);

        // Добавляем товар в корзину
        cartRef.child(cartItemId).setValue(product);
        
        // Проверяем, что товар добавлен
        cartRef.child(cartItemId).get().addOnSuccessListener(snapshot -> {
            assertTrue(snapshot.exists());
            Product savedProduct = snapshot.getValue(Product.class);
            assertNotNull(savedProduct);
            assertEquals(product.getId(), savedProduct.getId());
        });
    }

    @Test
    public void testCreateOrder() {
        // Тест создания заказа
        Order order = new Order();
        order.setId("test_order_id");
        order.setTimestamp(System.currentTimeMillis());

        DatabaseReference ordersRef = databaseRef.child("users").child(testUserId).child("orders");
        String orderId = ordersRef.push().getKey();
        assertNotNull(orderId);

        // Создаем заказ
        ordersRef.child(orderId).setValue(order);

        // Проверяем, что заказ создан
        ordersRef.child(orderId).get().addOnSuccessListener(snapshot -> {
            assertTrue(snapshot.exists());
            Order savedOrder = snapshot.getValue(Order.class);
            assertNotNull(savedOrder);
            assertEquals(order.getId(), savedOrder.getId());
        });
    }

    @Test
    public void testUpdateCartItemQuantity() {
        // Тест обновления количества товара в корзине
        String cartItemId = "test_cart_item";
        int newQuantity = 2;

        DatabaseReference cartRef = databaseRef.child("carts").child(testUserId);
        cartRef.child(cartItemId).child("quantity").setValue(newQuantity);

        // Проверяем обновление
        cartRef.child(cartItemId).get().addOnSuccessListener(snapshot -> {
            assertTrue(snapshot.exists());
            CartItem cartItem = snapshot.getValue(CartItem.class);
            assertNotNull(cartItem);
            assertEquals(newQuantity, cartItem.getQuantity());
        });
    }

    @After
    public void tearDown() {
        // Очистка тестовых данных
        if (testUserId != null) {
            databaseRef.child("carts").child(testUserId).removeValue();
            databaseRef.child("users").child(testUserId).child("orders").removeValue();
        }
    }
} 