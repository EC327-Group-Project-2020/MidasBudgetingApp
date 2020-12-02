package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AccountSetupPageTwo extends AppCompatActivity implements View.OnClickListener {

    //Debug tag
    private static final String TAG_DEBUG = AccountSetupPageTwo.class.getName();

    //Create ViewModel for read/write capabilities to database
    private dbViewModel accessDatabase;

    //Button
    private Button finishBtn;

    //Textviews
    private TextView monthlyIncome;
    private TextView budgetGoal;

    //Tag for username when passed on to next activity
    public static final String TAG_USER_SETUP2 = "user from setup page 2";
    //var from last activity
    private User validUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation_page2);

        //Database instance
        accessDatabase = new dbViewModel(getApplication());

        //Btn
        finishBtn = (Button) findViewById(R.id.finish_button);

        //View
        monthlyIncome = (TextView) findViewById(R.id.monthly_income);
        budgetGoal = (TextView) findViewById(R.id.budget_goal);

        //set on click listener
        finishBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        //Get inputs
        String incomeInput = monthlyIncome.getText().toString();
        String goalInput = budgetGoal.getText().toString();

        //validate
        if(incomeEmpty(incomeInput)){
            Toast.makeText(AccountSetupPageTwo.this,getResources().getString(R.string.empty_income_field), Toast.LENGTH_LONG).show();
            return;

        }
        else if (incomeNotNumber(incomeInput)){
            Toast.makeText(AccountSetupPageTwo.this,getResources().getString(R.string.income_not_number), Toast.LENGTH_LONG).show();
            return;
        }

        //add to database
        //get username from page 1
        validUser = (User) getIntent().getExtras().getSerializable(AccountSetupPageOne.TAG_USER_SETUP1);
        validUser.setMonthlyIncome(Double.parseDouble(incomeInput));

        //Launch new activity
        launchBudgetDisplayPage(validUser);

    }

    //Function to launch budget display page
    private void launchBudgetDisplayPage(User validUser) {
        Intent setupBudgetDisplayPage = new Intent(AccountSetupPageTwo.this, BudgetDisplayPage.class);
        //setupBudgetDisplayPage.putExtra("valid_user", validUser);
        setupBudgetDisplayPage.putExtra(TAG_USER_SETUP2,validUser);
        //Launch second page of account setup
        startActivity(setupBudgetDisplayPage);
    }

    //Function(s) to validate input
    /* Monthly income: can't be empty, must be digits,
     *User goals: a don't care for now
     *
     *
     *               */

    public static Boolean incomeEmpty(String incomeInput) {
        if (incomeInput.equals("")) {
            return true;
        }
        return false;
    }

    public static Boolean incomeNotNumber(String incomeInput) {
        try {
            Double d = Double.parseDouble(incomeInput);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}