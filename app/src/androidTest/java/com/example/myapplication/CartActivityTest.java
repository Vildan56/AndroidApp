package com.example.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CartActivityTest {

    @Rule
    public ActivityScenarioRule<CartActivity> activityRule =
            new ActivityScenarioRule<>(CartActivity.class);

    @Test
    public void testCartDisplayed() {
        // Проверяем, что RecyclerView корзины отображается
        Espresso.onView(ViewMatchers.withId(R.id.rv_cart))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testTotalPriceDisplayed() {
        // Проверяем, что отображается общая сумма
        Espresso.onView(ViewMatchers.withId(R.id.tv_total))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCheckoutButton() {
        // Проверяем кнопку оформления заказа
        Espresso.onView(ViewMatchers.withId(R.id.btn_checkout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
    }

    @Test
    public void testCartItemQuantityControls() {
        // Проверяем кнопки управления количеством товара
        Espresso.onView(ViewMatchers.withId(R.id.btn_increase))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.btn_decrease))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCartItemDisplay() {
        // Проверяем отображение элементов корзины
        Espresso.onView(ViewMatchers.withId(R.id.tv_cart_name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.tv_cart_price))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.tv_quantity))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
} 