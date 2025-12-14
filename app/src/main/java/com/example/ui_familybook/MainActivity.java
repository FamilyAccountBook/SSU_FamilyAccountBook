package com.example.ui_familybook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ui_familybook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 바인딩 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 앱이 처음 실행될 때(savedInstanceState가 null일 때) 프래그먼트 로드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ParentHomeFragment())
                    .commit();
        }
    }
}