package com.example.personalfinanceplanner;

import java.io.Serializable;

public class tempExpense extends Expense implements Serializable {

    //new data fields
    public String amountFieldValue;
    public int categoryIndex;

    //constructor
    public tempExpense(long associatedUserID, String amountFieldValue, int categoryIndex, String expenseNotes) {
        super(associatedUserID, null, 0, null, null, expenseNotes);
        this.amountFieldValue = amountFieldValue;
        this.categoryIndex = categoryIndex;
    }
}
