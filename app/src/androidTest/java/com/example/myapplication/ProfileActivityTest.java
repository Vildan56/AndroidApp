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
public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<ProfileActivity> activityRule =
            new ActivityScenarioRule<>(ProfileActivity.class);

    @Test
    public void testProfileDisplayed() {
        // Проверяем основные элементы профиля
        Espresso.onView(ViewMatchers.withId(R.id.tv_email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.rv_orders))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testOrdersListDisplayed() {
        // Проверяем отображение списка заказов
        Espresso.onView(ViewMatchers.withId(R.id.rv_orders))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testLogoutButton() {
        // Проверяем кнопку выхода
        Espresso.onView(ViewMatchers.withId(R.id.btn_logout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
    }

    @Test
    public void testOrderDetails() {
        // Проверяем отображение деталей заказа
        Espresso.onView(ViewMatchers.withId(R.id.tv_order_date))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
} 