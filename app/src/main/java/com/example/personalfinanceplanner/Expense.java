package com.example.personalfinanceplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//represents expenses the user creates
@Entity(tableName = "expense_info")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    public long expenseID;

    @ColumnInfo(name = "associated_userID")
    private long associatedUserID;

    @ColumnInfo(name = "amount")
    private double amount;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "receiptImageFilepath")
    private String receiptImageFilepath;

    @ColumnInfo(name = "expenseNotes")
    private String expenseNotes;
    //timestamp; need to figure out how to store in format acceptable for plotting

    //constructor (use null as the argument for receiptImageFilepath or expenseNotes if not provided)
    public Expense(double amount, String category, String receiptImageFilepath, String expenseNotes) {
        this.amount = amount;
        this.category = category;
        this.receiptImageFilepath = receiptImageFilepath;
        this.expenseNotes = expenseNotes;
    }

    //accessors and mutators

    public void setAssociatedUserID(long newAssociatedUserID) { associatedUserID = newAssociatedUserID; }

    public long getAssociatedUserID() { return associatedUserID; }

    public void setAmount(double newAmount) { amount = newAmount; }

    public double getAmount() { return amount; }

    public void setCategory(String newCategory) { category = newCategory; }

    public String getCategory() { return category; }

    public void setReceiptImageFilepath(String newFilepath) { receiptImageFilepath = newFilepath; }

    public String getReceiptImageFilepath() { return receiptImageFilepath; }

    public void setExpenseNotes(String newExpenseNotes) { expenseNotes = newExpenseNotes; }

    public String getExpenseNotes() { return expenseNotes; }
}
