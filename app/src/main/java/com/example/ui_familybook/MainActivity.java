package com.example.ui_familybook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_page);

        RecyclerView rv = findViewById(R.id.recyclerStatistics);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new StatisticsAdapter());
    }
}
