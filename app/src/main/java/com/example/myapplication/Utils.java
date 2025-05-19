package com.example.myapplication;

import android.util.Patterns;

public class Utils {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}