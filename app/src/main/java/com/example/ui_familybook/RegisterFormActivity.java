package com.example.ui_familybook;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        TextView tabLogin = findViewById(R.id.tab_login);
        TextView tabRegister = findViewById(R.id.tab_register);
        EditText etName = findViewById(R.id.et_name);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPw = findViewById(R.id.et_pw);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // 로그인 탭 → 로그인 화면으로
        tabLogin.setOnClickListener(v -> {
            finish(); // 바로 전 단계로 가도 되고
            // 또는 바로 로그인 화면으로 가고 싶으면
            // startActivity(new Intent(this, LoginActivity.class));
        });

        // 회원가입 탭 → 사실 지금 화면이 회원가입이니까 무시해도 됨

        // 가입하기
        btnRegister.setOnClickListener(v -> {
            // TODO: 입력값 검증하고 서버/Firebase로 전송
            // 일단은 액티비티 종료
            finish();
        });

        // 이전
        btnCancel.setOnClickListener(v -> finish());
    }
}
