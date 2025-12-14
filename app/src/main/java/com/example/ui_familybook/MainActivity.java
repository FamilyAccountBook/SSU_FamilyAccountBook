package com.example.ui_familybook; // 패키지명 확인

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 테스트 버튼 찾기
        Button btnTest = findViewById(R.id.btn_test_sticker);

        // 버튼 클릭 시 다이얼로그 띄우기
        btnTest.setOnClickListener(v -> {

            // 1. 다이얼로그 객체 생성
            StickerGiveFragment dialog = new StickerGiveFragment();

            // 2. 리스너 연결 (결과를 받아서 Toast 띄우기)
            dialog.On((index, message) -> {

                String stickerName = "";
                // 인덱스에 따른 이름 변환 (테스트용)
                switch (index) {
                    case 0: stickerName = "잘했어요(별)"; break;
                    case 1: stickerName = "절약왕(돼지)"; break;
                    case 2: stickerName = "똑똑해요(전구)"; break;
                    case 3: stickerName = "사랑해요(하트)"; break;
                    case 4: stickerName = "최고예요(트로피)"; break;
                    case 5: stickerName = "박수!(박수)"; break;
                }

                // 결과 토스트 메시지 출력
                Toast.makeText(MainActivity.this,
                        stickerName + " 선택됨\n메시지: " + message,
                        Toast.LENGTH_LONG).show();
            });

            // 3. 화면에 보여주기
            dialog.show(getSupportFragmentManager(), "TestStickerDialog");
        });
    }
}