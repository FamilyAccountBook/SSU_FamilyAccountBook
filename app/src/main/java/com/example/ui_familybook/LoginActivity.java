package com.example.ui_familybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);   // 앞에서 만든 로그인 레이아웃

        TextView tabLogin = findViewById(R.id.tab_login);
        TextView tabRegister = findViewById(R.id.tab_register);
        Button btnLogin = findViewById(R.id.btn_login);

        // 로그인 탭은 현재 화면이니까 아무 것도 안 해도 됨

        // 회원가입 탭 → 역할 선택 화면으로 이동
        tabRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterSelectActivity.class);
            startActivity(intent);
        });

        // 로그인 버튼 눌렀을 때 (여기선 일단 토스트나 다음 화면으로 가는 자리)
        btnLogin.setOnClickListener(v -> {
            // TODO: 실제 로그인 로직
            // 예시로 일단 완료 메시지 or 메인화면으로 이동 넣어도 됨
        });
    }
}
