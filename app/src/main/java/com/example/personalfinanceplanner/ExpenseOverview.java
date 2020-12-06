package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseOverview  extends AppCompatActivity implements View.OnClickListener  {

    //declare expense variable
    private Expense chosenExpense;
    private User loggedInUser;

    //access database
    private dbViewModel databaseAccessor;

    //declare view items
    private Button deleteExpenseBtn;
    private TextView expenseAmount;
    private TextView expenseCategory;
    private TextView expenseNotes;

    public static final String TAG_USER_EXPENSE_OVERVIEW_PAGE = "user from expense overview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_overview_delete);

        //everything crashed when i tried to put this in control statements...but would be nice to do so
        chosenExpense = (Expense) getIntent().getExtras().getSerializable(ProfileSettingsActivity.TAG_EXPENSE_PROFILE_PAGE);
        loggedInUser = (User) getIntent().getExtras().getSerializable(ProfileSettingsActivity.TAG_USER_PROFILE_PAGE);


        //populate view items with correct expense data
        expenseAmount = (TextView) findViewById(R.id.expense_field2);
        String amount = String.valueOf(chosenExpense.getAmount());
        expenseAmount.setText(amount);

        expenseCategory =(TextView) findViewById(R.id.category_field2);
        expenseCategory.setText(chosenExpense.getCategory());

        expenseNotes = (TextView) findViewById(R.id.notes_field2);
        expenseNotes.setText(chosenExpense.getExpenseNotes());

        //set onclick listener to button
        deleteExpenseBtn = (Button) findViewById(R.id.delete_button);
        deleteExpenseBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    /*TODO DELETE EXPENSE FROM DATABASE AND DO AN .UPDATE() METHOD */

    }

    //toolbar and
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miBack:
                Intent profilePage = new Intent(ExpenseOverview.this, ProfileSettingsActivity.class);
                profilePage.putExtra(TAG_USER_EXPENSE_OVERVIEW_PAGE, loggedInUser);
                //Launch
                startActivity(profilePage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
