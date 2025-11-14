package com.example.ui_familybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View type 정의
    private static final int TYPE_HEADER              = 0;
    private static final int TYPE_PERIOD              = 1;
    private static final int TYPE_TABS                = 2;
    private static final int TYPE_MY_INCOME_EXPENSE   = 3;
    private static final int TYPE_INCOME_CATEGORY     = 4;
    private static final int TYPE_EXPENSE_CATEGORY    = 5;
    private static final int TYPE_DAILY_TREND         = 6;

    @Override
    public int getItemCount() {
        return 7; // 고정된 섹션
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return TYPE_HEADER;
            case 1: return TYPE_PERIOD;
            case 2: return TYPE_TABS;
            case 3: return TYPE_MY_INCOME_EXPENSE;
            case 4: return TYPE_INCOME_CATEGORY;
            case 5: return TYPE_EXPENSE_CATEGORY;
            default: return TYPE_DAILY_TREND;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View view;

        switch (type) {
            case TYPE_HEADER:
                view = inf.inflate(R.layout.item_stats_header, parent, false);
                return new HeaderVH(view);

            case TYPE_PERIOD:
                view = inf.inflate(R.layout.item_stats_period, parent, false);
                return new PeriodVH(view);

            case TYPE_TABS:
                view = inf.inflate(R.layout.item_stats_tabs, parent, false);
                return new TabsVH(view);

            case TYPE_MY_INCOME_EXPENSE:
                view = inf.inflate(R.layout.item_my_income_expense, parent, false);
                return new MyIncomeExpenseVH(view);

            case TYPE_INCOME_CATEGORY:
                view = inf.inflate(R.layout.item_income_category, parent, false);
                return new IncomeCategoryVH(view);

            case TYPE_EXPENSE_CATEGORY:
                view = inf.inflate(R.layout.item_expense_category, parent, false);
                return new ExpenseCategoryVH(view);

            case TYPE_DAILY_TREND:
            default:
                view = inf.inflate(R.layout.item_daily_trend, parent, false);
                return new DailyTrendVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        // 현재 UI-only라 바인딩 없음
        // 추후 기능 넣기 쉽게 구조 유지함
    }

    // 아래는 뷰홀더들 - 필요한 경우 바로 연결하기 쉽게 모두 분리

    static class HeaderVH extends RecyclerView.ViewHolder {
        public HeaderVH(@NonNull View v) { super(v); }
    }

    static class PeriodVH extends RecyclerView.ViewHolder {
        public PeriodVH(@NonNull View v) { super(v); }
    }

    static class TabsVH extends RecyclerView.ViewHolder {
        public TabsVH(@NonNull View v) { super(v); }
    }

    static class MyIncomeExpenseVH extends RecyclerView.ViewHolder {
        public MyIncomeExpenseVH(@NonNull View v) { super(v); }
    }

    static class IncomeCategoryVH extends RecyclerView.ViewHolder {
        public IncomeCategoryVH(@NonNull View v) { super(v); }
    }

    static class ExpenseCategoryVH extends RecyclerView.ViewHolder {
        public ExpenseCategoryVH(@NonNull View v) { super(v); }
    }

    static class DailyTrendVH extends RecyclerView.ViewHolder {
        public DailyTrendVH(@NonNull View v) { super(v); }
    }
}
