package com.example.ui_familybook.fragments.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_familybook.R;
import com.example.ui_familybook.adapters.TransactionAdapter;
import com.example.ui_familybook.adapters.TransactionItem;

import java.util.ArrayList;

public class ParentHomeFragment extends Fragment {

    private ArrayList<TransactionItem> parentList;
    private ArrayList<TransactionItem> childList;
    private TransactionAdapter adapter;
    private TextView tabMy;
    private TextView tabChild;

    public ParentHomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTransactions);
        tabMy = view.findViewById(R.id.tabMy);
        tabChild = view.findViewById(R.id.tabChild);

        //

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        parentList = new ArrayList<>();
        parentList.add(new TransactionItem("월간 용돈", "용돈 · 2025년 10월 19일", "-200,000원", false));
        parentList.add(new TransactionItem("커피 & 간식", "식비 · 2025년 10월 13일", "-35,000원", false));
        parentList.add(new TransactionItem("옷 구매", "shopping · 2025년 10월 12일", "-280,000원", false));
        parentList.add(new TransactionItem("부모 입금", "기타 · 2025년 10월 10일", "+150,000원", true));

        childList = new ArrayList<>();
        childList.add(new TransactionItem("10월 과자", "용돈 · 2025년 10월 19일", "-5,000원", false));
        childList.add(new TransactionItem("분식집", "식비 · 2025년 10월 12일", "-12,000원", false));
        childList.add(new TransactionItem("버스비", "교통 · 2025년 10월 8일", "-8,000원", false));
        childList.add(new TransactionItem("이번 달 용돈", "용돈 · 2025년 10월 1일", "+50,000원", true));

        adapter = new TransactionAdapter(requireContext(), parentList);
        recyclerView.setAdapter(adapter);

        tabMy.setOnClickListener(v -> adapter.updateData(parentList));
        tabChild.setOnClickListener(v -> adapter.updateData(childList));

        return view;
    }
}

