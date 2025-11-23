package com.example.ui_familybook.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "family_book_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role";
    private static final String KEY_LOGGED_IN = "logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String email, String name, String password, String role) {
        // TODO: Firestore users/{uid} 저장 및 Firebase Auth 계정 생성 후 로컬 캐싱만 보조로 사용
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_NAME, name)
                .putString(KEY_PASSWORD, password)
                .putString(KEY_ROLE, role)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public boolean login(String email, String password) {
        // TODO: Firebase Auth signInWithEmailAndPassword 사용 후 성공 시 로컬 캐시 갱신
        String storedEmail = prefs.getString(KEY_EMAIL, null);
        String storedPassword = prefs.getString(KEY_PASSWORD, null);
        boolean matched = email != null && email.equals(storedEmail)
                && password != null && password.equals(storedPassword);
        if (matched) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply();
        }
        return matched;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "parent");
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
}
