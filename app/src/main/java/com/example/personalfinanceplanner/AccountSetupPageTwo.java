package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.personalfinanceplanner.LogInActivity.TAG_USER_LOGIN;

public class AccountSetupPageTwo extends AppCompatActivity implements View.OnClickListener {

    //Debug tag
    private static final String TAG_DEBUG = AccountSetupPageTwo.class.getName();

    //Create ViewModel for read/write capabilities to database
    private dbViewModel accessDatabase;

    //Button
    private Button finishBtn;

    //Textviews
    private TextView monthlyBudget;
    private TextView budgetGoal;

    //Tag for username when passed on to next activity
    public static final String TAG_USER_SETUP2 = "user from setup page 2";
    //var from last activity
    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation_page2);

        //Database instance
        accessDatabase = new dbViewModel(getApplication());

        //Btn
        finishBtn = (Button) findViewById(R.id.finish_button);

        //View
        monthlyBudget = (TextView) findViewById(R.id.monthly_income);
        budgetGoal = (TextView) findViewById(R.id.budget_goal);

        //grab user created in phase 1 of account setup
        Bundle userInfo = getIntent().getExtras();

        if(userInfo != null)
            newUser = (User) getIntent().getSerializableExtra(AccountSetupPageOne.TAG_USER_SETUP1);

        //set on click listener
        finishBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        //Get inputs
        String budgetInput = monthlyBudget.getText().toString();
        String goalInput = budgetGoal.getText().toString();

        //validate
        if(incomeEmpty(budgetInput)){
            Toast.makeText(AccountSetupPageTwo.this,getResources().getString(R.string.empty_income_field), Toast.LENGTH_LONG).show();
            return;

        }
        else if (incomeNotNumber(budgetInput) || incomeNegativeOrZero(budgetInput)){
            Toast.makeText(AccountSetupPageTwo.this,getResources().getString(R.string.income_val_invalid), Toast.LENGTH_LONG).show();
            return;
        }

        newUser.setMonthlyBudget(Float.parseFloat(budgetInput));
        accessDatabase.insert(newUser);

        //Launch new activity
        launchBudgetDisplayPage(newUser);

    }

    //Function to launch budget display page
    private void launchBudgetDisplayPage(User newUser) {

        Intent setupBudgetDisplayPage = new Intent(AccountSetupPageTwo.this, BudgetDisplayPage.class);
        //setupBudgetDisplayPage.putExtra("valid_user", validUser);
        setupBudgetDisplayPage.putExtra(TAG_USER_SETUP2, newUser);
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

    public static Boolean incomeNegativeOrZero(String incomeInput) {
        if(incomeInput.contains("-") || Double.parseDouble(incomeInput) == 0) {
            return true;
        }
        return false;
    }
}
