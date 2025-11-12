package com.example.ui_familybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private final List<CategoryItem> list;
    private int selected = -1;
    private final OnCategoryClick listener;

    public interface OnCategoryClick {
        void onClicked(CategoryItem item);
    }

    public CategoryAdapter(List<CategoryItem> list, OnCategoryClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        CategoryItem item = list.get(position);
        h.tvEmoji.setText(item.emoji);
        h.tvLabel.setText(item.name);

        if (position == selected) {
            h.itemView.setBackgroundResource(R.drawable.bg_category_selected);
        } else {
            h.itemView.setBackgroundResource(R.drawable.bg_category_unselected);
        }

        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            selected = pos;
            notifyDataSetChanged();
            if (listener != null) listener.onClicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvLabel;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvLabel = itemView.findViewById(R.id.tvLabel);
        }
    }
}
