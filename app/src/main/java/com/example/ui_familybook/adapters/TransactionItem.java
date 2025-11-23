package com.example.ui_familybook.adapters;

public class TransactionItem {
    public String title;
    public String sub;
    public String amount;
    public boolean isIncome;    // true = income, false = expense

    public TransactionItem(String title, String sub, String amount, boolean isIncome) {
        this.title = title;
        this.sub = sub;
        this.amount = amount;
        this.isIncome = isIncome;
    }
}
