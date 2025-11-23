package com.example.ui_familybook.fragments.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.R;

public class StatsFragment extends Fragment {

    private static final String ARG_ROLE = "role";

    public static StatsFragment newInstance(String role) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        TextView title = view.findViewById(R.id.tv_stats_title);
        String role = getArguments() != null ? getArguments().getString(ARG_ROLE, "parent") : "parent";
        if (title != null) {
            title.setText(role.equals("child") ? "자녀 통계" : "부모님 통계");
        }
        // TODO: Firestore 집계 데이터로 통계 화면 구성
        return view;
    }
}
