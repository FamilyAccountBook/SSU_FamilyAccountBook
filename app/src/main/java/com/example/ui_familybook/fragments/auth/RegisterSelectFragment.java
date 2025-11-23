package com.example.ui_familybook.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.R;

public class RegisterSelectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_select, container, false);

        TextView tabLogin = view.findViewById(R.id.tab_login);
        TextView tabRegister = view.findViewById(R.id.tab_register);
        LinearLayout cardParent = view.findViewById(R.id.card_parent);
        LinearLayout cardChild = view.findViewById(R.id.card_child);

        tabLogin.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );
        tabRegister.setOnClickListener(v -> {
            // Already on register tab; no-op for now.
        });

        cardParent.setOnClickListener(v -> navigateToForm("parent"));
        cardChild.setOnClickListener(v -> navigateToForm("child"));

        return view;
    }

    private void navigateToForm(String role) {
        Fragment next = RegisterFormFragment.newInstance(role);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, next)
                .addToBackStack(null)
                .commit();
    }
}
