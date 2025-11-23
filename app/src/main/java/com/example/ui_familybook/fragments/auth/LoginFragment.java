package com.example.ui_familybook.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.MainActivity;
import com.example.ui_familybook.R;
import com.example.ui_familybook.utils.SessionManager;
import com.example.ui_familybook.utils.ValidationUtils;

public class LoginFragment extends Fragment {

    private EditText etEmail;
    private EditText etPassword;
    private LinearLayout layoutLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        layoutLogin = view.findViewById(R.id.layout_login);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);

        TextView btnTabLogin = view.findViewById(R.id.tab_login);
        TextView btnTabSignup = view.findViewById(R.id.tab_register);
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnSignup = view.findViewById(R.id.btn_signup);

        btnTabLogin.setOnClickListener(v -> layoutLogin.setVisibility(View.VISIBLE));

        View.OnClickListener openRegister = v -> navigateToRegister();
        btnTabSignup.setOnClickListener(openRegister);
        btnSignup.setOnClickListener(openRegister);

        btnLogin.setOnClickListener(v -> handleLogin());

        return view;
    }

    private void handleLogin() {
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString() : "";

        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(requireContext(), "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            Toast.makeText(requireContext(), "비밀번호는 6자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = ((MainActivity) requireActivity()).getSessionManager();
        // TODO: Firebase Auth signInWithEmailAndPassword 연동 후 role 로드
        if (sessionManager.login(email, password)) {
            ((MainActivity) requireActivity()).showBottomNav();
            ((MainActivity) requireActivity()).openHomeByRole();
        } else {
            Toast.makeText(requireContext(), "등록된 계정을 찾을 수 없습니다. 회원가입을 진행해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToRegister() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, new RegisterSelectFragment())
                .addToBackStack(null)
                .commit();
    }
}
