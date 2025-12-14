package com.example.ui_familybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private final int totalStickers;
    // ★ 변경: 단순 숫자(int)가 아니라 실제 스티커 데이터 리스트를 가집니다.
    private List<Sticker> stickerList = new ArrayList<>();

    public StickerAdapter(int totalStickers) {
        this.totalStickers = totalStickers;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker_slot, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        // 현재 위치(position)에 데이터가 있는지 확인
        if (position < stickerList.size()) {
            // [채워진 상태]
            Sticker sticker = stickerList.get(position);

            // 1. DB에 저장된 진짜 이모지 표시
            holder.stickerIcon.setText(sticker.getEmoji());
            holder.stickerIcon.setAlpha(1f);

            // 2. 배경: 채워진 모양
            holder.itemView.setBackgroundResource(R.drawable.sticker_item_background);

            // (선택 사항) 클릭 시 상세 다이얼로그 띄우기 등의 이벤트 연결 가능

        } else {
            // [빈 칸 상태]
            holder.stickerIcon.setText(""); // 빈 텍스트 (또는 숫자 position + 1)
            holder.stickerIcon.setAlpha(1f);

            // 2. 배경: 점선 모양
            holder.itemView.setBackgroundResource(R.drawable.sticker_slot_background);
        }
    }

    @Override
    public int getItemCount() {
        // 항상 30개(고정된 판 크기)를 반환해야 함
        return totalStickers;
    }

    // ★ 변경: 리스트를 통째로 받아서 갱신하는 메서드
    public void submitList(List<Sticker> newStickers) {
        this.stickerList = newStickers; // 데이터 교체
        notifyDataSetChanged(); // 화면 갱신
    }

    // 현재 채워진 개수 반환 (프래그먼트에서 사용)
    public int getFilledCount() {
        return stickerList.size();
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder {
        TextView stickerIcon;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerIcon = itemView.findViewById(R.id.tv_sticker_icon);
        }
    }
}