package com.example.personalfinanceplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    //declare database viewmodel
    private dbViewModel databaseAccessor;

    //declare buttons
    private Button captureImageButton;
    private Button submitExpenseButton;

    //declare text views
    private TextView createExpenseHeader;
    private TextView expenseAmountHeader;
    private TextView categoryHeader;
    private TextView uploadReceiptHeader;
    private TextView notesHeader;

    //declare text edit fields
    private EditText expenseAmountField;
    private EditText notesField;

    //declare categories Spinner
    private Spinner categoryField;

    //declare logged in user
    private User loggedInUser;

    //intent tag
    public static final String TAG_EXPENSE_CREATED = "user created expense";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_expense);

        //initialize database viewmodel
        databaseAccessor = new dbViewModel(getApplication());

        //initialize buttons
        captureImageButton = (Button) findViewById(R.id.capture_image_button);
        submitExpenseButton = (Button) findViewById(R.id.submit_button);

        //initialize text views
        createExpenseHeader = (TextView) findViewById(R.id.create_expense_header);
        expenseAmountHeader = (TextView) findViewById(R.id.expense_amount);
        categoryHeader = (TextView) findViewById(R.id.category);
        uploadReceiptHeader = (TextView) findViewById(R.id.receipt_upload);
        notesHeader = (TextView) findViewById(R.id.notes);

        //initialize text edit fields
        expenseAmountField = (EditText) findViewById(R.id.expense_field);
        notesField = (EditText) findViewById(R.id.notes_field);

        //create categories spinner
        categoryField = (Spinner) findViewById(R.id.category_field);
        ArrayAdapter<String> categoryListAdapter = new ArrayAdapter<String>(AddExpenseActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.expense_categories));
        categoryListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryField.setAdapter(categoryListAdapter);


        //grabs logged in user from budget display page
        Bundle passedUser = getIntent().getExtras();

        //assign user passed from budget display page
        if(passedUser != null) {
            if (getIntent().getSerializableExtra(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY) != null) {
                loggedInUser = (User) getIntent().getSerializableExtra(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY);
            }
        }
        captureImageButton.setOnClickListener(this);
        submitExpenseButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.submit_button:
            {
                //grab input values from fields, in type formats specified by classes
                String expenseAmountString = expenseAmountField.getText().toString();
                String notes = notesField.getText().toString();
                String category = categoryField.getSelectedItem().toString();
                OffsetDateTime currentTimestamp = OffsetDateTime.now(ZoneId.systemDefault());
                String receiptImageFilepath = null;
                //MAKE SURE VARIABLE FOR IMAGE FILEPATH HERE IS CORRECT TYPE

                if (expenseAmountString.isEmpty() || expenseAmountString == null || category.equals("Choose an expense category")) {

                    Toast.makeText(AddExpenseActivity.this, getResources().getString(R.string.expense_creation_missing_fields), Toast.LENGTH_LONG).show();
                }
                else {
                    double expenseAmount = Double.parseDouble(expenseAmountString);

                    Toast.makeText(AddExpenseActivity.this, getResources().getString(R.string.expense_creation_success), Toast.LENGTH_LONG).show();

                    if (receiptImageFilepath == null) {
                        Expense newExpense = new Expense(loggedInUser.getUserID(), currentTimestamp, expenseAmount, category, null, notes);
                        databaseAccessor.insertExpense(newExpense);
                    }
                    else {
                        Expense newExpense = new Expense(loggedInUser.getUserID(), currentTimestamp, expenseAmount, category, receiptImageFilepath, notes);
                        databaseAccessor.insertExpense(newExpense);
                    }

                    launchBudgetDisplayPage(loggedInUser);
                }

                break;
            }
            case R.id.capture_image_button:
            {
                break; //INSERT LOGIC FOR DARREN'S CAMERA WORK HERE, AND STORE IMAGE FILEPATH IN receiptImageFilepath
            }
        }
    }

    private void launchBudgetDisplayPage(User loggedInUser) {

        Intent setupBudgetDisplayPage = new Intent(AddExpenseActivity.this, BudgetDisplayPage.class);
        setupBudgetDisplayPage.putExtra(TAG_EXPENSE_CREATED, loggedInUser);

        //Launch second page of account setup
        startActivity(setupBudgetDisplayPage);
    }
}