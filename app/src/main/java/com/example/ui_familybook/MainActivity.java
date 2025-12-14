package com.example.ui_familybook;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTest = findViewById(R.id.btn_test_sticker);

        btnTest.setOnClickListener(v -> {
            StickerGiveFragment dialog = new StickerGiveFragment();

            // ★ 수정됨: .On(...) -> .setOnStickerSentListener(...)
            dialog.setOnStickerSentListener((index, message) -> {

                String stickerName = "";
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

            dialog.show(getSupportFragmentManager(), "TestStickerDialog");
        });
    }
}