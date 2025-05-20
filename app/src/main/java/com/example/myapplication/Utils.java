package com.example.myapplication;

import android.util.Patterns;
import java.util.regex.Pattern;

public class Utils {

    // Simple regex for email validation as a fallback for unit tests
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        if (Patterns.EMAIL_ADDRESS != null) { // Android environment
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else { // Unit test environment
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
        }
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}