package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.personalfinanceplanner.LogInActivity.TAG_USER_LOGIN;

public class ProfileSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //debug tag
    private static final String TAG_DEBUG = ProfileSettingsActivity.class.getName();

    //declare logged in user
    private User loggedInUser;
    private ArrayList<String> storedCurrencies = new ArrayList<>();
    private Float userBudget;

    //declare expense variable
    private Expense chosenExpense;

    //access database
    private dbViewModel databaseAccessor;

    //declare view items
    private Button updateBudget;
    private EditText inputBudget;
    private Spinner removeCurrency;
    private ListView expenseList;

    //locals
    private String budget;
    private ArrayAdapter<String> ab;
    private ArrayList<String> allExpenses;
    private ArrayAdapter expenseAdapter;

    //private ExpenseCustomAdapter expenseAdapter;
    public static final String TAG_USER_PROFILE_PAGE = "edited user from profile settings";
    public static final String TAG_EXPENSE_PROFILE_PAGE = "chosen expense from profile settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        //create ViewModel for accessing database
        databaseAccessor = new dbViewModel(getApplication());

        //get user
        //Wrap in control statements
        if (getIntent().getSerializableExtra(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY) != null) {
            loggedInUser= (User) getIntent().getExtras().getSerializable(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY);
        }
        else if(getIntent().getSerializableExtra(ExpenseOverview.TAG_USER_EXPENSE_OVERVIEW_PAGE) != null){
            loggedInUser= (User) getIntent().getExtras().getSerializable(ExpenseOverview.TAG_USER_EXPENSE_OVERVIEW_PAGE);
        }
        else{
            Toast.makeText(ProfileSettingsActivity.this,"User passed was null!", Toast.LENGTH_LONG).show();
        }


        //update budget btn
        updateBudget = (Button) findViewById(R.id.updateButton);
        updateBudget.setOnClickListener(this);
        inputBudget = (EditText) findViewById(R.id.newBudget);



        //Remove currencies spinner
        removeCurrency = (Spinner) findViewById(R.id.currencyDelete);
        storedCurrencies = loggedInUser.getSavedCurrencies();
        ab = new ArrayAdapter<String>(ProfileSettingsActivity.this, android.R.layout.simple_spinner_item, storedCurrencies);
        ab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        removeCurrency.setAdapter(ab);

        //add onselect, selected currency rate gets stored in double currencyRate
        removeCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = removeCurrency.getSelectedItem().toString();
                //remove currency from user in database
                if(position == 0){
                   //do nothing can't remove usd
                } else {
                    for (int i = 0; i < storedCurrencies.size(); i++) {
                        if (storedCurrencies.get(i).equals(selectedCurrency)) {
                            loggedInUser.removeSavedCurrency(selectedCurrency);
                            databaseAccessor.update(loggedInUser);
                            Toast.makeText(ProfileSettingsActivity.this, getResources().getString(R.string.currency_removed_notification), Toast.LENGTH_LONG).show();
                            removeCurrency.setSelection(0);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //View/edit/delete expenses
        expenseList = (ListView) findViewById(R.id.expenseListView);
        List<Expense> userExpenses = databaseAccessor.getUserExpenses(loggedInUser.getUserID());
        Log.d(TAG_DEBUG, "no of expenses found: " + userExpenses.size());
        allExpenses = new ArrayList<String>();

        //pull out data into array, note: date index should be 0, amount should be 1 and category should be 2
        for(int i = 0; i < userExpenses.size(); i++){
            String currentExpense;
            String rawDate = userExpenses.get(i).getTimestamp().toString();
            String date = rawDate.substring(0,10);
            currentExpense = date + ":   ";
            String amountLayout = "-";
            String amount = String.valueOf(userExpenses.get(i).getAmount());
            amountLayout = amountLayout + amount + " $";
            currentExpense = currentExpense + amountLayout + " (";
            String category = userExpenses.get(i).getCategory();
            currentExpense = currentExpense + category + ")";
            allExpenses.add(i,currentExpense);
        }

        //use adapter to set list view and move to expense overview on click
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,allExpenses);
        expenseList.setAdapter(adapter);

        expenseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //find correct expense
                chosenExpense = userExpenses.get(position);
                Intent expenseOverview= new Intent(ProfileSettingsActivity.this, ExpenseOverview.class);
                expenseOverview.putExtra(TAG_EXPENSE_PROFILE_PAGE, chosenExpense);
                expenseOverview.putExtra(TAG_USER_PROFILE_PAGE, loggedInUser);
                //Launch
                startActivity(expenseOverview);
            }
        });

    }

    @Override
    public void onClick(View v){

        budget = inputBudget.getText().toString();

        if(emptyBudget(budget)){
            Toast.makeText(ProfileSettingsActivity.this,getResources().getString(R.string.budget_val_invalid), Toast.LENGTH_LONG).show();
            return;
        }
        loggedInUser.setMonthlyBudget(Float.parseFloat(budget));
        databaseAccessor.update(loggedInUser);
        Toast.makeText(ProfileSettingsActivity.this,getResources().getString(R.string.budget_update_notification), Toast.LENGTH_LONG).show();
    }

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
                Intent budgetPage = new Intent(ProfileSettingsActivity.this, BudgetDisplayPage.class);
                budgetPage.putExtra(TAG_USER_PROFILE_PAGE,loggedInUser);
                //Launch second page of account setup
                startActivity(budgetPage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //function to check budget input
    public static Boolean emptyBudget(String input){
        if(input.isEmpty()){
            return true;
        }
        return false;
    }
}
