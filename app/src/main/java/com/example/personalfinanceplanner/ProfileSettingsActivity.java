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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.personalfinanceplanner.LogInActivity.TAG_USER_LOGIN;

public class ProfileSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //declare logged in user
    private User loggedInUser;
    private ArrayList<String> storedCurrencies = new ArrayList<>();
    private Float userBudget;

    //access database
    private dbViewModel databaseAccessor;


    //declare view items
    private Button updateBudget;
    private EditText inputBudget;
    private Spinner removeCurrency;

    //locals
    private String budget;
    private ArrayAdapter<String> ab;
    public static final String TAG_USER_PROFILE_PAGE = "edited user from profile settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        //create ViewModel for accessing database
        databaseAccessor = new dbViewModel(getApplication());

        //get from databse
        //Wrap in control statements
        if (getIntent().getSerializableExtra(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY) != null) {
            loggedInUser= (User) getIntent().getExtras().getSerializable(BudgetDisplayPage.TAG_USER_BUDGET_DISPLAY);
        }
        else{
            Toast.makeText(ProfileSettingsActivity.this,"User passed was null!", Toast.LENGTH_LONG).show();
        }

        //

        //update budget btn
        updateBudget = (Button) findViewById(R.id.updateButton);
        updateBudget.setOnClickListener(this);
        inputBudget = (EditText) findViewById(R.id.newBudget);
        budget = inputBudget.getText().toString();



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
    }

    @Override
    public void onClick(View v){

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
        if(input.equals("")){
            return true;
        }
        return false;
    }
}
