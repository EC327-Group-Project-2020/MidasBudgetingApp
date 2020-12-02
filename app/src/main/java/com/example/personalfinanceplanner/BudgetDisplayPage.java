package com.example.personalfinanceplanner;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class BudgetDisplayPage extends AppCompatActivity implements View.OnClickListener {


    //VARIABLES
    //debug tag for log
    private static final String TAG_DEBUG = BudgetDisplayPage.class.getName();

    //create User to hold the user object passed from login
    private User loggedInUser;
    private  ArrayList<String> storedCurrencies = new ArrayList<>();
    private String userName;

    //declare dbViewModel for interaction with Database
    private dbViewModel databaseAccessor;

    //dropdown with currencies
    private Spinner addCurrency;        //displays async retrieved names from online currency base
    private Spinner currCurrency;       //displays user currently saved currencies


    //containers to interact with FetchCurrency and hold currency data
    public static ArrayList<String> names = new ArrayList<String>();        //array of currency names to select from
    public static ArrayList<ArrayList<String>> namesRates = new ArrayList<ArrayList<String>>();     //array of name-currency arrays
    public static ArrayAdapter<String> aa;      //array adapter for spinner addCurrency
    private ArrayAdapter<String> ab;

    //INDEX constants that correspond to currency name/rate fields of namesRates array, for easier indexing
    final int CURRENCYNAME = 0;     //fx. firstCurrencyName = namesRates[0][CURRENCYNAME]
    final int CURRENCYRATE = 1;     //fx. firstCurrencyRATE = namesRates[0][CURRENCYRATE]

    //declare "Add Expense" button
    private Button addExpense;

    //declare welcome header textview
    private TextView welcomeHeader;

    //variable to store current currency rate
    private Double currencyRate;


    //ACTIONS
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_display);

        Log.d(TAG_DEBUG, "Loading budget page!");


        //<--------------GETTING DATABASE STUFF-------------->

        //create ViewModel for accessing database
        databaseAccessor = new dbViewModel(getApplication());

        //check if correct user was passed from login and stores verification result
        Bundle passedUser = getIntent().getExtras();
        if(passedUser != null) {
            //assign user passed from login/signup to logged in user
            if(getIntent().getSerializableExtra(LogInActivity.TAG_USER_LOGIN) != null){
            loggedInUser = (User) getIntent().getSerializableExtra(LogInActivity.TAG_USER_LOGIN);
            }
            else{
                loggedInUser = (User) getIntent().getSerializableExtra(AccountSetupPageTwo.TAG_USER_SETUP2);
            }
            //get currency list and name of user
            storedCurrencies = loggedInUser.getSavedCurrencies();
            userName = loggedInUser.getUsername();
        }
        else
            System.out.println("ERROR: User not received. Login forbidden."); //this should not be possible, but just in case

                //------WELCOME HEADER WITH USERNAME------//
        welcomeHeader = (TextView) findViewById(R.id.welcomeBanner);
        String txt = welcomeHeader.getText().toString();
        txt = txt + " " + userName + "!";
        welcomeHeader.setText(txt);

        //<--------------GETTING DATABASE STUFF ENDS HERE-------------->



        //<--------------CURRENCY FEATURE STARTS HERE-------------->

        //Start async activity to fetch currency data
        FetchCurrencyData fetch = new FetchCurrencyData();
        fetch.execute();

                //------SPINNER 1: CURRENCY MENU------//
        //connecting the spinner to layout
        addCurrency = (Spinner) findViewById(R.id.currencyMenu);

        //Setting the ArrayAdapter data on the add-currency spinner
        aa = new ArrayAdapter<String>(BudgetDisplayPage.this, android.R.layout.simple_spinner_item,names);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCurrency.setAdapter(aa);

        //setting on select for currency to be added to database based on select
        addCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){

                    String selectedCurrency = addCurrency.getSelectedItem().toString();

                    for (int i = 0; i < loggedInUser.getSavedCurrencies().size(); i++) //checks to make sure the currency hasn't already been added to profile
                    {
                        if (loggedInUser.getSavedCurrencies().get(i).equals(selectedCurrency))
                        {
                            Toast.makeText(BudgetDisplayPage.this, "Currency has already been added", Toast.LENGTH_SHORT).show();
                            return; //exits if the currency is already in the user's list
                        }
                    }

                    loggedInUser.addSavedCurrency(selectedCurrency);
                    databaseAccessor.update(loggedInUser);
                    Toast.makeText(BudgetDisplayPage.this, "Currency added to your profile!", Toast.LENGTH_SHORT).show();
                }
                addCurrency.setSelection(0);
                Log.d(TAG_DEBUG, "Currency selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

                //------SPINNER 2 - User currencies------//
        //connecting current currency spinner
        currCurrency = (Spinner) findViewById(R.id.currencyChoice);
        ab = new ArrayAdapter<String>(BudgetDisplayPage.this, android.R.layout.simple_spinner_item,storedCurrencies);
        ab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currCurrency.setAdapter(ab);

        //add onselect, selected currency rate gets stored in double currencyRate
        currCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String translationCurrency = currCurrency.getSelectedItem().toString();

                //get correct currency rate
                if(translationCurrency.equals("USD")){
                    currencyRate = 1.0;
                    Log.d(TAG_DEBUG, "currency is usd, rate is: " +currencyRate);
                }
                else{
                    Log.d(TAG_DEBUG, "namesRatesArray size: " + namesRates.size());
                    for(int i=0; i < namesRates.size();i++){
                        if(namesRates.get(i).get(CURRENCYNAME).equals(translationCurrency)){
                            currencyRate = Double.parseDouble(namesRates.get(i).get(CURRENCYRATE));
                            Log.d(TAG_DEBUG, "currency is "+namesRates.get(i).get(CURRENCYNAME)+", rate is" + currencyRate);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(currCurrency.getItemAtPosition(0).toString().equals("USD")){
                    currencyRate = 1.0;
                    Log.d(TAG_DEBUG, "default mode, currency usd, rate: " + currencyRate);
                }
            }
            });

        //<--------------CURRENCY FEATURE ENDS HERE-------------->



        //<--------------EXPENSE ADDITION FEATURE STARTS HERE----->

        addExpense = (Button) findViewById(R.id.addExpenseButton);
        addExpense.setOnClickListener(this);
    }

    //if we want to click something
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.addExpenseButton:
            {
                //TEST VALUES ARE BEING USED AS PLACEHOLDERS - NEED TO ADD INTERFACE FOR FILLING IN EXPENSE INFO
                Expense newExpense = new Expense(loggedInUser.getUserID(), OffsetDateTime.now(ZoneId.systemDefault()),100, "Household",
                        "example/filelocation", null);
                databaseAccessor.insertExpense(newExpense);
                break;
            }
            //ADD MORE CASES HERE FOR DIFFERENT BUTTONS
        }
    }

    //<---------------EXPENSE ADDITION FEATURE ENDS HERE--------------->

    /*TODO ADD REST OF FUNCTIONALITY TO THE PAGE*/
        //MULTIPLY BY double variable currencyRate in all data display to get functional of that
        //need to be able to query a user for all expenses, for graphical display
}
