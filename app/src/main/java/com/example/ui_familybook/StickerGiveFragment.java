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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class StickerGiveFragment extends DialogFragment {

    private DialogGiveStickerBinding binding;
    private int selectedStickerIndex = -1; // 선택된 스티커 번호

    // 1. Firebase 인스턴스 추가
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogGiveStickerBinding.inflate(inflater, container, false);

        // 다이얼로그 배경 투명하게
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 닫기 버튼
        binding.ivClose.setOnClickListener(v -> dismiss());

        // 스티커 선택 로직 (기존 유지)
        for (int i = 0; i < binding.gridStickers.getChildCount(); i++) {
            final int index = i;
            View child = binding.gridStickers.getChildAt(i);

            child.setOnClickListener(v -> {
                selectedStickerIndex = index;
                updateStickerSelection();
            });
        }

        // ★ 2. 선물하기 버튼 클릭 (Firebase 전송)
        binding.btnSendSticker.setOnClickListener(v -> {
            if (selectedStickerIndex == -1) {
                Toast.makeText(getContext(), "스티커를 선택해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = binding.etMsg.getText().toString();
            if (message.isEmpty()) {
                message = "오늘 정말 잘했어요!";
            }

            // Firebase로 전송하는 함수 호출
            sendStickerToFirebase(selectedStickerIndex, message);
        });
    }

    // ★ 3. Firebase Firestore에 데이터 저장하는 함수
    private void sendStickerToFirebase(int index, String message) {
        // TODO: 실제 앱에서는 로그인한 자녀의 UID를 동적으로 받아와야 합니다.
        // 지금은 테스트를 위해 고정된 ID("child_user_1")를 사용합니다.
        String targetChildUid = "child_user_1";

        // 인덱스에 맞는 이모지 변환
        String emoji = getEmojiForIndex(index);

        // Sticker 객체 생성 (Timestamp.now() 사용)
        Sticker newSticker = new Sticker(index, emoji, message, Timestamp.now());

        // DB에 저장: users -> 자녀ID -> stickers 컬렉션에 추가
        db.collection("users")
                .document(targetChildUid)
                .collection("stickers")
                .add(newSticker)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "스티커를 선물했습니다! 🎁", Toast.LENGTH_SHORT).show();
                    dismiss(); // 전송 성공 시 창 닫기
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 인덱스를 이모지 문자열로 바꿔주는 헬퍼 함수
    private String getEmojiForIndex(int index) {
        switch (index) {
            case 0: return "⭐";
            case 1: return "🐷";
            case 2: return "💡"; // 전구
            case 3: return "💖";
            case 4: return "🏆";
            case 5: return "👏";
            default: return "⭐";
        }
    }

    // 선택된 스티커 UI 갱신 (기존 유지)
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