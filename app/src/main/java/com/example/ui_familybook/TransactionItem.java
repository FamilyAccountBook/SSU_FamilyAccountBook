package com.example.ui_familybook;

public class TransactionItem {
    public String title;
    public String sub;
    public String amount;
    public boolean isIncome;    // true면 +, false면 -

    public TransactionItem(String title, String sub, String amount, boolean isIncome) {
        this.title = title;
        this.sub = sub;
        this.amount = amount;
        this.isIncome = isIncome;
    }
}
