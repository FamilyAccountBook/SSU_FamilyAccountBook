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
import java.util.List;

public class ParentHomeFragment extends Fragment {

    private final List<TransactionItem> parentList = new ArrayList<>();
    private final List<TransactionItem> childList = new ArrayList<>();
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_transactions);
        TextView tabMy = view.findViewById(R.id.tab_my);
        TextView tabChild = view.findViewById(R.id.tab_child);
        TextView tvBalance = view.findViewById(R.id.tv_balance);
        TextView tvIncome = view.findViewById(R.id.tv_income);
        TextView tvExpense = view.findViewById(R.id.tv_expense);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        seedData();
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>(parentList));
        recyclerView.setAdapter(adapter);

        tabMy.setOnClickListener(v -> adapter.updateData(parentList));
        tabChild.setOnClickListener(v -> adapter.updateData(childList));

        if (tvBalance != null) {
            tvBalance.setText("+2,025,000원");
        }
        if (tvIncome != null) {
            tvIncome.setText("+3,200,000원");
        }
        if (tvExpense != null) {
            tvExpense.setText("-1,175,000원");
        }

        return view;
    }

    private void seedData() {
        // TODO: Firestore transactions 컬렉션에서 부모/자녀 거래 내역 조회로 교체
        parentList.clear();
        parentList.add(new TransactionItem("Allowance payout", "Cash · 2025-10-19", "-200,000원", false));
        parentList.add(new TransactionItem("Coffee & snacks", "Food · 2025-10-13", "-35,000원", false));
        parentList.add(new TransactionItem("Grocery run", "Shopping · 2025-10-12", "-280,000원", false));
        parentList.add(new TransactionItem("Salary", "Income · 2025-10-10", "+1,500,000원", true));

        childList.clear();
        childList.add(new TransactionItem("Weekly treats", "Allowance · 2025-10-19", "-5,000원", false));
        childList.add(new TransactionItem("Snack bar", "Food · 2025-10-12", "-12,000원", false));
        childList.add(new TransactionItem("Bus fare", "Transport · 2025-10-08", "-8,000원", false));
        childList.add(new TransactionItem("Monthly allowance", "Allowance · 2025-10-01", "+50,000원", true));
    }
}
