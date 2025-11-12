package com.example.ui_familybook;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChildHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_home);

        // 스티커 버튼
        Button btnSticker = findViewById(R.id.btnSticker);
        btnSticker.setOnClickListener(v ->
                Toast.makeText(this, "스티커 화면으로 이동 예정", Toast.LENGTH_SHORT).show()
        );

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<TransactionItem> data = new ArrayList<>();
        data.add(new TransactionItem("10월 저금", "저축 · 2025년 10월 19일", "-5,000원", false));
        data.add(new TransactionItem("분식집", "식비 · 2025년 10월 12일", "-12,000원", false));
        data.add(new TransactionItem("버스비", "교통비 · 2025년 10월 8일", "-8,000원", false));
        data.add(new TransactionItem("편의점 간식", "식비 · 2025년 10월 5일", "-15,000원", false));
        data.add(new TransactionItem("이번 달 용돈", "용돈 · 2025년 10월 1일", "+50,000원", true));

        TransactionAdapter adapter = new TransactionAdapter(this, data);
        recyclerView.setAdapter(adapter);

        // 하단 네비
        TextView navHome = findViewById(R.id.navHome);
        TextView navAdd = findViewById(R.id.navAdd);
        TextView navStat = findViewById(R.id.navStat);
        TextView navSetting = findViewById(R.id.navSetting);

        navHome.setOnClickListener(v -> Toast.makeText(this, "홈", Toast.LENGTH_SHORT).show());
        navAdd.setOnClickListener(v -> Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show());
        navStat.setOnClickListener(v -> Toast.makeText(this, "통계", Toast.LENGTH_SHORT).show());
        navSetting.setOnClickListener(v -> Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show());
    }
}
