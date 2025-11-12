package com.example.ui_familybook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 기본 레이아웃

        // 앱 켜지자마자 로그인 화면으로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // MainActivity는 필요 없으니까 종료 (뒤로가기 눌러도 안 보이게)
        finish();
    }
}
