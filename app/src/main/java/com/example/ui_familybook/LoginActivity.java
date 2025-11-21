package com.example.ui_familybook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignup = findViewById(R.id.layoutSignup);

        Button btnTabLogin = findViewById(R.id.btnTabLogin);
        Button btnTabSignup = findViewById(R.id.btnTabSignup);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);

        btnTabLogin.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutSignup.setVisibility(View.GONE);
        });

        btnTabSignup.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutSignup.setVisibility(View.VISIBLE);
        });
//
//        btnLogin.setOnClickListener(v -> {
//            // 실제로는 로그인 검증 후
//            Intent intent = new Intent(LoginActivity.this, ParentHomeActivity.class);
//            startActivity(intent);
//        });
//
//        btnSignup.setOnClickListener(v -> {
//            // 가입 처리 후 홈으로
//            Intent intent = new Intent(LoginActivity.this, ParentHomeActivity.class);
//            startActivity(intent);
//        });
    }
}
