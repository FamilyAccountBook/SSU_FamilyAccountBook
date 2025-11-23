package com.example.ui_familybook;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.databinding.ActivityMainBinding;
import com.example.ui_familybook.fragments.auth.LoginFragment;
import com.example.ui_familybook.fragments.child.ChildHomeFragment;
import com.example.ui_familybook.fragments.common.AddTransactionFragment;
import com.example.ui_familybook.fragments.common.SettingsFragment;
import com.example.ui_familybook.fragments.common.StatsFragment;
import com.example.ui_familybook.fragments.parent.ParentHomeFragment;
import com.example.ui_familybook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private boolean suppressNavCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // TODO: Firebase Auth 세션으로 대체
        sessionManager = new SessionManager(this);

        setContentView(binding.getRoot());

        // Window inset padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = binding.bottomNav;
        bottomNav.setVisibility(View.GONE);
        bottomNav.setOnItemSelectedListener(item -> {
            if (suppressNavCallback) {
                return true;
            }
            int id = item.getItemId();
            if (id == R.id.homeFragment) {
                openHomeByRole();
                return true;
            } else if (id == R.id.addFragment) {
                switchFragment(new AddTransactionFragment());
                return true;
            } else if (id == R.id.statsFragment) {
                switchFragment(StatsFragment.newInstance(getCurrentRole()));
                return true;
            } else if (id == R.id.settingFragment) {
                switchFragment(SettingsFragment.newInstance(getCurrentRole()));
                return true;
            }
            return false;
        });

        if (sessionManager.isLoggedIn()) {
            showBottomNav();
            openHomeByRole();
        } else if (savedInstanceState == null) {
            switchFragment(new LoginFragment());
        }
    }

    public void showBottomNav() {
        if (binding != null) {
            binding.bottomNav.setVisibility(View.VISIBLE);
        }
    }

    public void hideBottomNav() {
        if (binding != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void openHomeByRole() {
        String role = getCurrentRole();
        Fragment fragment = role.equals("child")
                ? new ChildHomeFragment()
                : new ParentHomeFragment();
        switchFragment(fragment);
        setSelectedItemSilently(R.id.homeFragment);
    }

    private void setSelectedItemSilently(int itemId) {
        if (binding == null) {
            return;
        }
        suppressNavCallback = true;
        binding.bottomNav.setSelectedItemId(itemId);
        suppressNavCallback = false;
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, fragment)
                .commit();
    }

    private String getCurrentRole() {
        return sessionManager != null ? sessionManager.getRole() : "parent";
    }
}
