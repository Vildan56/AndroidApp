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
public class ProductListActivityTest {

    @Rule
    public ActivityScenarioRule<ProductListActivity> activityRule =
            new ActivityScenarioRule<>(ProductListActivity.class);

    @Test
    public void testProductListDisplayed() {
        // Проверяем, что RecyclerView отображается
        Espresso.onView(ViewMatchers.withId(R.id.rv_products))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSearchFunctionality() {
        // Проверяем работу поиска
        Espresso.onView(ViewMatchers.withId(R.id.search_view))
                .perform(ViewActions.click())
                .perform(ViewActions.typeText("Test Product"))
                .perform(ViewActions.pressImeActionButton());

        // Проверяем, что поисковая строка содержит введенный текст
        Espresso.onView(ViewMatchers.withId(R.id.search_view))
                .check(ViewAssertions.matches(ViewMatchers.withText("Test Product")));
    }

    @Test
    public void testCategorySpinner() {
        // Проверяем работу спиннера категорий
        Espresso.onView(ViewMatchers.withId(R.id.spinner_category))
                .perform(ViewActions.click());

        // Проверяем, что спиннер отображается
        Espresso.onView(ViewMatchers.withId(R.id.spinner_category))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAddToCartButton() {
        // Проверяем кнопку добавления в корзину
        Espresso.onView(ViewMatchers.withId(R.id.btn_add_to_cart))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
    }

    @Test
    public void testCartFabButton() {
        // Проверяем FAB кнопку корзины
        Espresso.onView(ViewMatchers.withId(R.id.fab_cart))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
    }
} 