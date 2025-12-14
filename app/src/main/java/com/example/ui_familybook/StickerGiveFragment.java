package com.example.ui_familybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ui_familybook.databinding.DialogGiveStickerBinding;
import com.google.firebase.database.FirebaseDatabase;

public class StickerGiveFragment extends DialogFragment {

    private DialogGiveStickerBinding binding;
    private int selectedStickerIndex = -1;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    // ★ 1. 데이터를 전달할 인터페이스 정의
    public interface OnStickerSentListener {
        void onStickerSent(int index, String message);
    }

    // ★ 2. 리스너 변수 선언
    private OnStickerSentListener listener;

    // ★ 3. 외부(MainActivity)에서 리스너를 연결해주는 메서드 (이름을 setOn...으로 짓는 것이 관례입니다)
    public void setOnStickerSentListener(OnStickerSentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogGiveStickerBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivClose.setOnClickListener(v -> dismiss());

        for (int i = 0; i < binding.gridStickers.getChildCount(); i++) {
            final int index = i;
            View child = binding.gridStickers.getChildAt(i);
            child.setOnClickListener(v -> {
                selectedStickerIndex = index;
                updateStickerSelection();
            });
        }

        binding.btnSendSticker.setOnClickListener(v -> {
            if (selectedStickerIndex == -1) {
                Toast.makeText(getContext(), "스티커를 선택해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }
            String message = binding.etMsg.getText().toString();
            if (message.isEmpty()) message = "오늘 정말 잘했어요!";

            sendStickerToFirebase(selectedStickerIndex, message);
        });
    }

    private void sendStickerToFirebase(int index, String message) {
        String targetChildUid = "child_user_1";
        String emoji = getEmojiForIndex(index);
        long timestamp = System.currentTimeMillis();

        Sticker newSticker = new Sticker(index, emoji, message, timestamp);

        db.getReference("users")
                .child(targetChildUid)
                .child("stickers")
                .push()
                .setValue(newSticker)
                .addOnSuccessListener(aVoid -> {
                    // Toast.makeText(getContext(), "스티커 선물 완료!", Toast.LENGTH_SHORT).show(); // 중복 토스트 방지 위해 주석 처리 가능

                    // ★ 4. 전송 성공 시, 연결된 리스너(MainActivity)에게 알림
                    if (listener != null) {
                        listener.onStickerSent(index, message);
                    }

                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ... 기존 헬퍼 메서드들 (getEmojiForIndex, updateStickerSelection) ...
    private String getEmojiForIndex(int index) {
        switch (index) {
            case 0: return "⭐";
            case 1: return "🐷";
            case 2: return "💡";
            case 3: return "💖";
            case 4: return "🏆";
            case 5: return "👏";
            default: return "⭐";
        }
    }

    private void updateStickerSelection() {
        for (int i = 0; i < binding.gridStickers.getChildCount(); i++) {
            View child = binding.gridStickers.getChildAt(i);
            if (i == selectedStickerIndex) {
                child.setBackgroundResource(R.drawable.sticker_item_background);
                child.setBackgroundTintList(requireContext().getColorStateList(R.color.light_pink_background));
            } else {
                child.setBackgroundResource(R.drawable.sticker_item_background);
                child.setBackgroundTintList(null);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}