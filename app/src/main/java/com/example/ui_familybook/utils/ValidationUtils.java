package com.example.ui_familybook.utils;

public class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email != null && email.matches(emailPattern);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidAmount(String amount) {
        try {
            long value = Long.parseLong(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
