package com.example.ui_familybook;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParentHomeActivity extends AppCompatActivity {

    private ArrayList<TransactionItem> parentList;
    private ArrayList<TransactionItem> childList;
    private TransactionAdapter adapter;
    private TextView tabMy, tabChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        // 1. 뷰 찾기
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);
        tabMy = findViewById(R.id.tabMy);
        tabChild = findViewById(R.id.tabChild);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. 더미 데이터 준비
        parentList = new ArrayList<>();
        parentList.add(new TransactionItem("월간 저축", "저축 · 2025년 10월 19일", "-200,000원", false));
        parentList.add(new TransactionItem("커피 & 간식", "식비 · 2025년 10월 13일", "-35,000원", false));
        parentList.add(new TransactionItem("옷 구매", "shopping · 2025년 10월 12일", "-280,000원", false));
        parentList.add(new TransactionItem("부수입", "기타 · 2025년 10월 10일", "+150,000원", true));

        childList = new ArrayList<>();
        childList.add(new TransactionItem("10월 저금", "저축 · 2025년 10월 19일", "-5,000원", false));
        childList.add(new TransactionItem("분식집", "식비 · 2025년 10월 12일", "-12,000원", false));
        childList.add(new TransactionItem("버스비", "교통비 · 2025년 10월 8일", "-8,000원", false));
        childList.add(new TransactionItem("이번 달 용돈", "용돈 · 2025년 10월 1일", "+50,000원", true));

        // 3. 기본은 '나의 거래 내역'
        adapter = new TransactionAdapter(this, parentList);
        recyclerView.setAdapter(adapter);
        setTabSelected(true);   // 기본 탭 스타일

        // 4. 탭 클릭 시 데이터 교체
        tabMy.setOnClickListener(v -> {
            adapter.updateData(parentList);  // 어댑터에 이 메서드 하나 추가하면 편함
            setTabSelected(true);
        });

        tabChild.setOnClickListener(v -> {
            adapter.updateData(childList);
            setTabSelected(false);
        });

        // 하단 네비는 그대로...
        TextView navHome = findViewById(R.id.navHome);
        TextView navAdd = findViewById(R.id.navAdd);
        TextView navStat = findViewById(R.id.navStat);
        TextView navSetting = findViewById(R.id.navSetting);

        navHome.setOnClickListener(v -> Toast.makeText(this, "홈", Toast.LENGTH_SHORT).show());
        navAdd.setOnClickListener(v -> Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show());
        navStat.setOnClickListener(v -> Toast.makeText(this, "통계", Toast.LENGTH_SHORT).show());
        navSetting.setOnClickListener(v -> Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show());
    }

    private void setTabSelected(boolean mySelected) {
        if (mySelected) {
            tabMy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabMy.setTextColor(0xFFFFFFFF);

            tabChild.setBackgroundColor(0x00000000);
            tabChild.setTextColor(0xFF727272);
        } else {
            tabChild.setBackgroundResource(R.drawable.bg_tab_selected);
            tabChild.setTextColor(0xFFFFFFFF);

            tabMy.setBackgroundColor(0x00000000);
            tabMy.setTextColor(0xFF727272);
        }
    }
}
