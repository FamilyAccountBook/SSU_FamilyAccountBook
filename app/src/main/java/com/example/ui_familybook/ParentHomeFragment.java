package com.example.ui_familybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.databinding.FragmentParentHomeBinding;

public class ParentHomeFragment extends Fragment {

    private FragmentParentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentParentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 스티커 주기 버튼 클릭
        binding.btnGiveSticker.setOnClickListener(v -> {

            // 다이얼로그 생성
            StickerGiveFragment dialog = new StickerGiveFragment();

            // 다이얼로그 띄우기
            dialog.show(getParentFragmentManager(), "GiveStickerDialog");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}