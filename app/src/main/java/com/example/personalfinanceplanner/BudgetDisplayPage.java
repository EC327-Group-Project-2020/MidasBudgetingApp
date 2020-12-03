package com.example.personalfinanceplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.example.personalfinanceplanner.AddExpenseActivity.TAG_EXPENSE_CREATED;
import static com.example.personalfinanceplanner.LogInActivity.TAG_USER_LOGIN;

public class BudgetDisplayPage extends AppCompatActivity implements View.OnClickListener {


    //VARIABLES
    //debug tag for log
    private static final String TAG_DEBUG = BudgetDisplayPage.class.getName();
    public static final String TAG_USER_BUDGET_DISPLAY = "user from budget display";

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
    private FloatingActionButton addExpense;

    //declare welcome header text view
    private TextView welcomeHeader;

    //variable to store current currency rate
    private Double currencyRate;


    //ACTIONS
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_display);

        Log.d(TAG_DEBUG, "Loading budget page!");


        //<--------------GETTING DATABASE STUFF-------------->

        //create ViewModel for accessing database
        databaseAccessor = new dbViewModel(getApplication());

        //grabs user passed from login
        Bundle passedUser = getIntent().getExtras();

        if(passedUser != null) {

            //assign user passed from login/signup to logged in user
            if (getIntent().getSerializableExtra(TAG_USER_LOGIN) != null) {
                loggedInUser = (User) getIntent().getSerializableExtra(TAG_USER_LOGIN);
            }

            else if (getIntent().getSerializableExtra(TAG_EXPENSE_CREATED) != null) {
                loggedInUser = (User) getIntent().getSerializableExtra(TAG_EXPENSE_CREATED);
            }

            else {
                loggedInUser = (User) getIntent().getSerializableExtra(AccountSetupPageTwo.TAG_USER_SETUP2);
                }

            //get currency list and name of user
            storedCurrencies = loggedInUser.getSavedCurrencies();
            userName = loggedInUser.getUsername();
            }

        else
            System.out.println("ERROR: User not received. Login forbidden."); //this should not be possible, but just in case


        //welcoming our customers

                //------WELCOME HEADER WITH USERNAME------//

        welcomeHeader = (TextView) findViewById(R.id.welcomeBanner);
        String txt = welcomeHeader.getText().toString();
        txt = txt + " " + userName + "!";
        welcomeHeader.setText(txt);

        //<--------------EXPENSE GRAPHING----------------------->

        List<Expense> userExpenses = databaseAccessor.getUserExpenses(loggedInUser.getUserID());
        LineChart expensesPerDayLineChart = findViewById(R.id.daily_expenses_trendline);
        expensesPerDayLineChart.invalidate(); //refreshes chart
        expensesPerDayLineChart.notifyDataSetChanged(); //alerts chart when underlying data has changed

        //grab chart components
        XAxis xAxis = expensesPerDayLineChart.getXAxis();
        YAxis yAxisRight = expensesPerDayLineChart.getAxisRight();
        YAxis yAxisLeft = expensesPerDayLineChart.getAxisLeft();
        Legend legend = expensesPerDayLineChart.getLegend();

        //CHART ONE: EXPENSES PER DAY OF CURRENT MONTH

        Calendar c = Calendar.getInstance();
        int daysInCurrentMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH); //gets the number of days in the current month

        double[] expensesPerDay = new double[daysInCurrentMonth]; //creates an array with each element corresponding to a day of the current month

        for (int i = 0; i < userExpenses.size(); i++) { //goes through entire list of user's expenses, and populates expensesPerDay array with expenses on each day

            Expense expenseRecord = userExpenses.get(i);

            int monthOfExpense = expenseRecord.getTimestamp().getMonthValue();
            int yearOfExpense = expenseRecord.getTimestamp().getYear();
            int currentMonth = (OffsetDateTime.now(ZoneId.systemDefault())).getMonthValue();
            int currentYear = (OffsetDateTime.now(ZoneId.systemDefault())).getYear();

            if (monthOfExpense == currentMonth && yearOfExpense == currentYear) { //checks to see if the expense in question occurred in the current calendar month and year

                int dayOfExpense = expenseRecord.getTimestamp().getDayOfMonth();
                expensesPerDay[dayOfExpense-1] = expensesPerDay[dayOfExpense-1] + expenseRecord.getAmount();//based on the day value of the expense, adds the expense amount to that position in array
            }
        }

        float maxExpense = 0;
        //get max expense value in array, for plotting purposes
        for (int i = 0; i < expensesPerDay.length; i++) {

            if (maxExpense < expensesPerDay[i])
                maxExpense = (float) expensesPerDay[i];
        }

        List<Entry> dataPoints = new ArrayList<Entry>(); //convert expense totals per day of current month into a set of data points
        for (int i = 0; i < expensesPerDay.length; i++) {
            // turn your data into Entry objects
            dataPoints.add(new Entry((i+1), (float) expensesPerDay[i])); //value of the x is the day of the month (i+1), value of y is the total amount spent that day
        }

        LineDataSet dataSet = new LineDataSet(dataPoints, null); // group data points as set for line graph
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.parseColor("#FED215"));
        dataSet.setLineWidth(2f); //default is 1f
        dataSet.setFillColor(Color.parseColor("#FED215"));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.parseColor("#FED215"));
        dataSet.setDrawValues(false);


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        lineDataSets.add(dataSet);
        LineData data = new LineData(lineDataSets);
        expensesPerDayLineChart.setData(data); //draw chart

        //chart styling
        expensesPerDayLineChart.setVisibleXRange(1, daysInCurrentMonth);
        expensesPerDayLineChart.setVisibleYRange(0, maxExpense + 100, YAxis.AxisDependency.LEFT);
        expensesPerDayLineChart.setDrawBorders(true);
        expensesPerDayLineChart.setBorderColor(Color.parseColor("#434343"));
        expensesPerDayLineChart.setDescription(null);
        legend.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawGridLinesBehindData(false);
        yAxisLeft.setDrawGridLinesBehindData(false);
        expensesPerDayLineChart.animateXY(500,500);
        xAxis.setAxisLineColor(Color.parseColor("#434343"));
        yAxisLeft.setAxisLineColor(Color.parseColor("#434343"));
        xAxis.setAxisLineWidth(2f);
        yAxisLeft.setAxisLineWidth(2f);
        xAxis.setTextSize(14);
        yAxisLeft.setTextSize(14);
        yAxisLeft.setYOffset(10);

        //NEED TO MAKE SURE AXES SHOW AMOUNT IN CORRECT CURRENCY

        //END OF EXPENSE PER DAY CHART IMPLEMENTATION


        //START OF RUNNING EXPENSE TOTAL PER DAY, COMPARED TO BUDGET

        LineChart totalExpensesOverTimeChart = findViewById(R.id.total_expenses_trendline);
        totalExpensesOverTimeChart.invalidate(); //refreshes chart
        totalExpensesOverTimeChart.notifyDataSetChanged(); //alerts chart when underlying data has changed

        int currentDayInMonth = (OffsetDateTime.now(ZoneId.systemDefault())).getDayOfMonth();

        //grab chart components
        XAxis xAxis2 = totalExpensesOverTimeChart.getXAxis();
        YAxis yAxisRight2 = totalExpensesOverTimeChart.getAxisRight();
        YAxis yAxisLeft2 = totalExpensesOverTimeChart.getAxisLeft();
        Legend legend2 = totalExpensesOverTimeChart.getLegend();

        double[] runningExpenseSumPerDay = new double[daysInCurrentMonth]; //creates an array with each element corresponding to a day of the current month

        for (int i = 0; i < daysInCurrentMonth; i++) { //goes through entire list of user's expenses, and populates expensesPerDay array with expense sums on each day

            for (int j = 0; j < userExpenses.size(); j++) { //sorts

                Expense expenseRecord = userExpenses.get(j);

                int monthOfExpense = expenseRecord.getTimestamp().getMonthValue();
                int yearOfExpense = expenseRecord.getTimestamp().getYear();
                int currentMonth = (OffsetDateTime.now(ZoneId.systemDefault())).getMonthValue();
                int currentYear = (OffsetDateTime.now(ZoneId.systemDefault())).getYear();
                int dayOfExpense = expenseRecord.getTimestamp().getDayOfMonth();

                if (dayOfExpense == (i+1) && monthOfExpense == currentMonth && yearOfExpense == currentYear) {
                    runningExpenseSumPerDay[i] += expenseRecord.getAmount();
                }
            }

            if (i > 0) {
                runningExpenseSumPerDay[i] += runningExpenseSumPerDay[i-1];
            }
        }

        List<Entry> dataPointsSet2 = new ArrayList<Entry>(); //convert expense totals per day of current month into a set of data points
        for (int i = 0; i < runningExpenseSumPerDay.length; i++) {
            // turn your data into Entry objects
            dataPointsSet2.add(new Entry((i+1), (float) runningExpenseSumPerDay[i]));//value of the x is the day of the month (i+1), value of y is the total amount spent that day
        }

        float monthlyBudget = loggedInUser.getMonthlyBudget();
        float maxYvalue = (monthlyBudget > runningExpenseSumPerDay[currentDayInMonth]) ? (float) loggedInUser.getMonthlyBudget() : (float) runningExpenseSumPerDay[currentDayInMonth]; //sets the max y value of the graph as either the budget or your current expense total, depending on which is bigger

        LineDataSet dataSet2 = new LineDataSet(dataPointsSet2, null); // group data points as set for line graph
        dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet2.setLineWidth(2f); //default is 1f
        dataSet2.setCircleRadius(4f);
        dataSet2.setDrawValues(false);

        //if the user is under budget, line is green; otherwise, red
        if (maxYvalue == monthlyBudget) {
            dataSet2.setColor(Color.GREEN);
            dataSet2.setFillColor(Color.GREEN);
            dataSet2.setCircleColor(Color.GREEN);
        }
        else {
            dataSet2.setColor(Color.parseColor("#ff0033"));
            dataSet2.setFillColor(Color.parseColor("#ff0033"));
            dataSet2.setCircleColor(Color.parseColor("#ff0033"));
        }

        ArrayList<ILineDataSet> lineDataSets2 = new ArrayList<ILineDataSet>();
        lineDataSets2.add(dataSet2);
        LineData data2 = new LineData(lineDataSets2);
        totalExpensesOverTimeChart.setData(data2); //draw chart

        //chart styling
        totalExpensesOverTimeChart.setVisibleXRange(1, daysInCurrentMonth);
        totalExpensesOverTimeChart.setDrawBorders(true);
        totalExpensesOverTimeChart.setBorderColor(Color.parseColor("#434343"));
        totalExpensesOverTimeChart.setDescription(null);
        legend2.setEnabled(false);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        yAxisRight2.setEnabled(false);
        yAxisRight2.setDrawGridLinesBehindData(false);
        yAxisLeft2.setDrawGridLinesBehindData(false);
        yAxisLeft2.setAxisMinimum(0);
        yAxisLeft2.setAxisMaximum(maxYvalue + 200); //plus 100 is just to give space above max value on graph
        totalExpensesOverTimeChart.animateXY(500,500);
        xAxis2.setAxisLineColor(Color.parseColor("#434343"));
        yAxisLeft2.setAxisLineColor(Color.parseColor("#434343"));
        xAxis2.setAxisLineWidth(2f);
        yAxisLeft2.setAxisLineWidth(2f);
        xAxis2.setTextSize(14);
        yAxisLeft2.setTextSize(14);
        xAxis2.setXOffset(10);
        yAxisLeft2.setYOffset(10);

        //add budget line to chart
        LimitLine ll = new LimitLine(monthlyBudget, "Your Monthly Budget");
        totalExpensesOverTimeChart.getAxisLeft().addLimitLine(ll);
        ll.setLineColor(Color.parseColor("#FED215"));
        ll.setLineWidth(3f);
          
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

        addExpense = (FloatingActionButton) findViewById(R.id.addExpenseButton);
        addExpense.setOnClickListener(this);
    }

    //if we want to click something
    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.addExpenseButton:
            {
                launchExpenseCreationActivity(loggedInUser);
                break;
            }
            //ADD MORE CASES HERE FOR DIFFERENT BUTTONS
        }
    }


    private void launchExpenseCreationActivity(User user){

            Intent setupExpenseCreation = new Intent(BudgetDisplayPage.this, AddExpenseActivity.class);
            setupExpenseCreation.putExtra(TAG_USER_BUDGET_DISPLAY, user);

            //Launch second page of account setup
            startActivity(setupExpenseCreation);
        }

    //<---------------EXPENSE ADDITION FEATURE ENDS HERE--------------->

    /*TODO ADD REST OF FUNCTIONALITY TO THE PAGE*/
        //MULTIPLY BY double variable currencyRate in all data display to get functional of that
        //need to be able to query a user for all expenses, for graphical display

}
