package com.example.ui_familybook.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.MainActivity;
import com.example.ui_familybook.R;
import com.example.ui_familybook.utils.SessionManager;
import com.example.ui_familybook.utils.ValidationUtils;

public class RegisterFormFragment extends Fragment {

    private static final String ARG_ROLE = "role";
    private String role = "parent";

    public static RegisterFormFragment newInstance(String role) {
        RegisterFormFragment fragment = new RegisterFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString(ARG_ROLE, "parent");
        }
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
        tabRegister.setOnClickListener(v -> {
            // Already on register tab.
        });

        btnRegister.setOnClickListener(v -> handleRegister(etName, etEmail, etPw));
        btnCancel.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        return view;
    }

    private void handleRegister(EditText etName, EditText etEmail, EditText etPw) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPw.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(requireContext(), "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            Toast.makeText(requireContext(), "비밀번호는 6자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = ((MainActivity) requireActivity()).getSessionManager();
        // TODO: Firebase Auth createUserWithEmailAndPassword + Firestore users/{uid} 문서 생성
        sessionManager.saveUser(email, name, password, role);
        Toast.makeText(requireContext(), "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

        ((MainActivity) requireActivity()).showBottomNav();
        ((MainActivity) requireActivity()).openHomeByRole();
    }
}
