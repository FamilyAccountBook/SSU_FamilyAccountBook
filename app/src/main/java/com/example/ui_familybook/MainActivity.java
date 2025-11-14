package com.example.ui_familybook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private LinearLayout tabExpense, tabIncome;
    private RecyclerView rvCategories;
    private EditText etAmount, etMemo;
    private TextView tvDate;

    private CategoryAdapter adapter;
    private final List<CategoryItem> expenseList = new ArrayList<>();
    private final List<CategoryItem> incomeList = new ArrayList<>();
    private boolean isExpense = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction_page);

        // 상단 아이콘
        ShapeableImageView ivBack    = findViewById(R.id.ivBack);
        ShapeableImageView ivCamera  = findViewById(R.id.ivCamera);
        ShapeableImageView ivGallery = findViewById(R.id.ivGallery);

        loadImageFromUrl("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/yXiqIVqvYN/qrwenxgq_expires_30_days.png", ivBack);
        loadImageFromUrl("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/yXiqIVqvYN/uvle649m_expires_30_days.png", ivCamera);
        loadImageFromUrl("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/yXiqIVqvYN/e0sxm4g2_expires_30_days.png", ivGallery);

        // 폼
        tabExpense = findViewById(R.id.tabExpense);
        tabIncome  = findViewById(R.id.tabIncome);
        rvCategories = findViewById(R.id.rvCategories);
        etAmount  = findViewById(R.id.etAmount);
        etMemo    = findViewById(R.id.etMemo);
        tvDate    = findViewById(R.id.tvDate);
        tvDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", (month+1)) + "-" + String.format("%02d", dayOfMonth);
                        tvDate.setText(date);
                    }, y, m, d);
            dialog.show();
        });


        // 금액 천단위 콤마 (입력시 포맷)
        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!str.equals(current)) {
                    etAmount.removeTextChangedListener(this);
                    String clean = str.replaceAll(",", "");
                    try {
                        long val = clean.isEmpty() ? 0 : Long.parseLong(clean);
                        String formatted = new DecimalFormat("#,###").format(val);
                        current = formatted;
                        etAmount.setText(formatted);
                        etAmount.setSelection(formatted.length());
                    } catch (Exception e) {
                        current = "";
                    }
                    etAmount.addTextChangedListener(this);
                }
            }
        });

        // 카테고리 데이터
        buildExpense();
        buildIncome();

        // RecyclerView
        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new CategoryAdapter(expenseList);
        rvCategories.setAdapter(adapter);

        // 탭 클릭
        tabExpense.setOnClickListener(v -> {
            selectTab(true);
            adapter.submit(expenseList);
        });
        tabIncome.setOnClickListener(v -> {
            selectTab(false);
            adapter.submit(incomeList);
        });

        // 초기 탭 상태
        selectTab(true);

        // “내역 추가” 버튼(동작은 빈 껍데기)
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            // TODO: 저장 로직 나중에 연결
        });

    }

    private void buildExpense() {
        expenseList.clear();
        expenseList.addAll(Arrays.asList(
                new CategoryItem("🚌", "교통비"),
                new CategoryItem("🍚", "식비"),
                new CategoryItem("👕", "의류"),
                new CategoryItem("💄", "미용"),
                new CategoryItem("🐷", "저축"),
                new CategoryItem("🏥", "의료"),
                new CategoryItem("📄", "세금"),
                new CategoryItem("🎬", "문화생활"),
                new CategoryItem("📚", "교육"),
                new CategoryItem("🧴", "생필품"),
                new CategoryItem("💳", "회비"),
                new CategoryItem("🎁", "경조사"),
                new CategoryItem("🛡️", "보험"),
                new CategoryItem("📱", "통신"),
                new CategoryItem("📦", "기타")
        ));
    }

    private void buildIncome() {
        incomeList.clear();
        incomeList.addAll(Arrays.asList(
                new CategoryItem("💼", "급여"),
                new CategoryItem("💸", "용돈"),
                new CategoryItem("📈", "이자/배당"),
                new CategoryItem("🎁", "기타")
        ));
    }

    private void selectTab(boolean expense) {
        isExpense = expense;
        tabExpense.setBackgroundResource(expense ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        tabIncome.setBackgroundResource(!expense ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
    }

    private void loadImageFromUrl(String url, ShapeableImageView target) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                uiHandler.post(() -> target.setImageBitmap(bmp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== Adapter ====================

    static class CategoryItem {
        final String emoji;
        final String label;
        CategoryItem(String e, String l) { emoji = e; label = l; }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final List<CategoryItem> data = new ArrayList<>();
        private int selected = RecyclerView.NO_POSITION;

        CategoryAdapter(List<CategoryItem> init) { submit(init); }

        void submit(List<CategoryItem> items) {
            data.clear();
            if (items != null) data.addAll(items);
            selected = RecyclerView.NO_POSITION;
            notifyDataSetChanged();
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int position) {
            CategoryItem item = data.get(position);
            h.tvEmoji.setText(item.emoji);
            h.tvLabel.setText(item.label);

            // 선택/비선택 배경
            if (position == selected) {
                h.root.setBackgroundResource(R.drawable.bg_category_selected);
            } else {
                h.root.setBackgroundResource(R.drawable.bg_category_unselected);
            }

            // 클릭 리스너: position을 저장하지 말고, 클릭 시점에 getAdapterPosition() 사용
            h.itemView.setOnClickListener(v -> {
                int cur = h.getAdapterPosition();
                if (cur == RecyclerView.NO_POSITION) return;

                int old = selected;
                if (cur == old) {
                    selected = RecyclerView.NO_POSITION;
                    notifyItemChanged(old);
                } else {
                    selected = cur;
                    notifyItemChanged(cur);
                    if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
                }
            });
        }

        @Override public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            final LinearLayout root;
            final TextView tvEmoji, tvLabel;
            VH(@NonNull View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.root);
                tvEmoji = itemView.findViewById(R.id.tvEmoji);
                tvLabel = itemView.findViewById(R.id.tvLabel);
            }
        }
    }
}
