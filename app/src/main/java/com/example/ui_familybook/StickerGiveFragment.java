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
// ★ 변경: Firestore 관련 import 삭제하고 Realtime Database import 추가
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StickerGiveFragment extends DialogFragment {

    private DialogGiveStickerBinding binding;
    private int selectedStickerIndex = -1; // 선택된 스티커 번호

    // ★ 1. Realtime Database 인스턴스로 변경
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

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

        // 스티커 선택 로직
        for (int i = 0; i < binding.gridStickers.getChildCount(); i++) {
            final int index = i;
            View child = binding.gridStickers.getChildAt(i);

            child.setOnClickListener(v -> {
                selectedStickerIndex = index;
                updateStickerSelection();
            });
        }

        // 선물하기 버튼 클릭
        binding.btnSendSticker.setOnClickListener(v -> {
            if (selectedStickerIndex == -1) {
                Toast.makeText(getContext(), "스티커를 선택해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = binding.etMsg.getText().toString();
            if (message.isEmpty()) {
                message = "오늘 정말 잘했어요!";
            }

            sendStickerToFirebase(selectedStickerIndex, message);
        });
    }

    // ★ 3. Realtime Database에 데이터 저장하는 함수
    private void sendStickerToFirebase(int index, String message) {
        String targetChildUid = "child_user_1";

        String emoji = getEmojiForIndex(index);

        // ★ 변경: Timestamp 객체 대신 long 타입의 시간값 사용 (정렬 및 호환성 위함)
        long timestamp = System.currentTimeMillis();

        // Sticker 객체 생성 (Sticker 클래스 생성자도 long 타입을 받도록 수정되어야 함)
        Sticker newSticker = new Sticker(index, emoji, message, timestamp);

        // ★ 변경: Realtime Database 저장 로직
        // push()는 랜덤 키(ID)를 생성합니다. (Firestore의 .add()와 유사)
        db.getReference("users")
                .child(targetChildUid)
                .child("stickers")
                .push() // 고유 ID 생성
                .setValue(newSticker) // 값 저장
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "스티커를 선물했습니다! 🎁", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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