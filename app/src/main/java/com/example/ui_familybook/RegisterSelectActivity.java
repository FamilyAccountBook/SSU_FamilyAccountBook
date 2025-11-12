package com.example.ui_familybook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select);

        TextView tabLogin = findViewById(R.id.tab_login);
        TextView tabRegister = findViewById(R.id.tab_register);
        LinearLayout cardParent = findViewById(R.id.card_parent);
        LinearLayout cardChild = findViewById(R.id.card_child);

        // 로그인 탭 누르면 로그인 화면으로 돌아가기
        tabLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterSelectActivity.this, LoginActivity.class);
            // 이미 로그인 화면이 있으면 새로 여러 개 쌓이지 않게 옵션 줘도 됨
            startActivity(intent);
            finish();
        });

        // 부모님 선택 → 회원가입 폼으로 이동
        cardParent.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterSelectActivity.this, RegisterFormActivity.class);
            intent.putExtra("role", "부모님");
            startActivity(intent);
        });

        // 자녀 선택 → 회원가입 폼으로 이동
        cardChild.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterSelectActivity.this, RegisterFormActivity.class);
            intent.putExtra("role", "자녀");
            startActivity(intent);
        });
    }
}
