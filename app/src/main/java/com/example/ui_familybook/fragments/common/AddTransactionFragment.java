package com.example.ui_familybook.fragments.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.R;

public class AddTransactionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // TODO: 거래 추가 시 Firestore transactions 컬렉션에 저장 로직 연결
        return inflater.inflate(R.layout.activity_add_transaction_page, container, false);
    }
}
