package com.example.ui_familybook.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.R;

public class RegisterFormFragment extends Fragment {

    private static final String ARG_ROLE = "role";

    public static RegisterFormFragment newInstance(String role) {
        RegisterFormFragment fragment = new RegisterFormFragment();
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
        View view = inflater.inflate(R.layout.fragment_register_form, container, false);

        TextView tabLogin = view.findViewById(R.id.tab_login);
        TextView tabRegister = view.findViewById(R.id.tab_register);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPw = view.findViewById(R.id.et_pw);
        Button btnRegister = view.findViewById(R.id.btn_register);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        tabLogin.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        btnRegister.setOnClickListener(v -> {
            // TODO: 입력값 검증 및 서버 전송 처리
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        btnCancel.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        return view;
    }
}
