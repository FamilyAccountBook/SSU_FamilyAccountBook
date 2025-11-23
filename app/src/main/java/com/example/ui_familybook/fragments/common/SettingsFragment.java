package com.example.ui_familybook.fragments.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.R;

public class SettingsFragment extends Fragment {

    private static final String ARG_ROLE = "role";

    public static SettingsFragment newInstance(String role) {
        SettingsFragment fragment = new SettingsFragment();
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
        String role = getArguments() != null ? getArguments().getString(ARG_ROLE, "parent") : "parent";
        int layoutId = role.equals("child") ? R.layout.fragment_setting_child : R.layout.fragment_setting_parent;
        // TODO: Firestore users/{uid} 로드/업데이트로 프로필/목표 설정 연동
        return inflater.inflate(layoutId, container, false);
    }
}
