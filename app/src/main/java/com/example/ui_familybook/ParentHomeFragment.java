package com.example.ui_familybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Toast를 사용하기 위해 추가
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
            showStickerDialog();
        });
    }

    private void showStickerDialog() {
        // 1. 다이얼로그 객체 생성
        StickerGiveFragment dialog = new StickerGiveFragment();

        // 2. ★ 리스너 연결: 스티커 전송이 성공적으로 Realtime DB에 저장된 후 실행됩니다.
        dialog.setOnStickerSentListener((index, message) -> {
            // 전송 성공 시 사용자에게 피드백을 제공합니다.
            Toast.makeText(requireContext(), "아이에게 스티커를 선물했습니다! 🎁", Toast.LENGTH_SHORT).show();

            // (선택 사항) 만약 이 화면(ParentHomeFragment)에 스티커 개수나 관련 정보가 있다면 여기서 UI를 갱신하면 됩니다.
            // 예: updateStickerCountOnScreen();
        });

        // 3. 다이얼로그 띄우기
        dialog.show(getParentFragmentManager(), "GiveStickerDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}