package com.example.personalfinanceplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.time.OffsetDateTime;

import static androidx.room.ForeignKey.CASCADE;

//represents expenses the user creates
@Entity(tableName = "expense_info")
public class Expense implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long expenseID;

    @ColumnInfo(name = "associated_userID")
    private long associatedUserID;

    @TypeConverters(Converters.class)
    @NonNull
    @ColumnInfo(name = "expense_timestamp") //includes timezone
    private OffsetDateTime timestamp;

    @ColumnInfo(name = "amount")
    private double amount;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "receiptImageFilepath")
    private String receiptImageFilepath;

    @ColumnInfo(name = "expenseNotes")
    private String expenseNotes;

    //constructor (use null as the argument for receiptImageFilepath or expenseNotes if not provided)
    public Expense(long associatedUserID, OffsetDateTime timestamp, double amount, String category, String receiptImageFilepath, String expenseNotes) {
        this.associatedUserID = associatedUserID;
        this.timestamp = timestamp;
        this.amount = amount;
        this.category = category;
        this.receiptImageFilepath = receiptImageFilepath;
        this.expenseNotes = expenseNotes;
    }

    //accessors and mutators

    public void setExpenseID(long newExpenseID) { expenseID = newExpenseID; }

    public long getExpenseID() { return expenseID; }

    public void setAssociatedUserID(long newAssociatedUserID) { associatedUserID = newAssociatedUserID; }

    public long getAssociatedUserID() { return associatedUserID; }

    public void setTimestamp(OffsetDateTime newTimestamp) { timestamp = newTimestamp; }

    public OffsetDateTime getTimestamp() { return timestamp; }

    public void setAmount(double newAmount) { amount = newAmount; }

    public double getAmount() { return amount; }

    public void setCategory(String newCategory) { category = newCategory; }

    public String getCategory() { return category; }

    public void setReceiptImageFilepath(String newFilepath) { receiptImageFilepath = newFilepath; }

    public String getReceiptImageFilepath() { return receiptImageFilepath; }

    public void setExpenseNotes(String newExpenseNotes) { expenseNotes = newExpenseNotes; }

    public String getExpenseNotes() { return expenseNotes; }
}
