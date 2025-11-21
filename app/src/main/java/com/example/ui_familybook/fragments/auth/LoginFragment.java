package com.example.ui_familybook.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.MainActivity;
import com.example.ui_familybook.fragments.parent.ParentHomeFragment;
import com.example.ui_familybook.R;

public class LoginFragment extends Fragment {

    private LinearLayout layoutLogin;
    private LinearLayout layoutSignup;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutSignup = view.findViewById(R.id.layoutSignup);

        TextView btnTabLogin = view.findViewById(R.id.tab_login);
        TextView btnTabSignup = view.findViewById(R.id.tab_register);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnSignup = view.findViewById(R.id.btnSignup);

        btnTabLogin.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutSignup.setVisibility(View.GONE);
        });

        btnTabSignup.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutSignup.setVisibility(View.VISIBLE);
        });

        btnLogin.setOnClickListener(v -> navigateToHome());

        btnSignup.setOnClickListener(v -> navigateToHome());

        return view;
    }

    private void navigateToHome() {
        if (requireActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) requireActivity();
            activity.showBottomNav();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new ParentHomeFragment())
                    .commit();
        }
    }
}
