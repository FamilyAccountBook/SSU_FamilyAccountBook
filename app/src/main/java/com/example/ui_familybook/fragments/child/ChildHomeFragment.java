package com.example.ui_familybook.fragments.child;

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

public class ChildHomeFragment extends Fragment {

    private final List<TransactionItem> transactions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_transactions);
        TextView tvBalance = view.findViewById(R.id.tv_balance);
        TextView tvIncome = view.findViewById(R.id.tv_income);
        TextView tvExpense = view.findViewById(R.id.tv_expense);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        seedData();
        recyclerView.setAdapter(new TransactionAdapter(requireContext(), transactions));

        if (tvBalance != null) {
            tvBalance.setText("+120,000원");
        }
        if (tvIncome != null) {
            tvIncome.setText("+180,000원");
        }
        if (tvExpense != null) {
            tvExpense.setText("-60,000원");
        }

        return view;
    }

    private void seedData() {
        // TODO: Firestore transactions 컬렉션에서 자녀 거래 내역 조회로 교체
        transactions.clear();
        transactions.add(new TransactionItem("Allowance", "Weekly · 2025-10-19", "+20,000원", true));
        transactions.add(new TransactionItem("Snacks", "Food · 2025-10-12", "-5,000원", false));
        transactions.add(new TransactionItem("Books", "Study · 2025-10-08", "-12,000원", false));
        transactions.add(new TransactionItem("Gift", "Reward · 2025-10-01", "+50,000원", true));
    }
}
