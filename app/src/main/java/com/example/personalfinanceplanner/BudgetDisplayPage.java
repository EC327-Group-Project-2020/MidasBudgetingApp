package com.example.personalfinanceplanner;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.util.StringUtil;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.personalfinanceplanner.AddExpenseActivity.TAG_EXPENSE_CREATED;
import static com.example.personalfinanceplanner.LogInActivity.TAG_USER_LOGIN;

public class BudgetDisplayPage extends AppCompatActivity implements View.OnClickListener {


    //VARIABLES
    //debug tag for log
    private static final String TAG_DEBUG = BudgetDisplayPage.class.getName();
    public static final String TAG_USER_BUDGET_DISPLAY = "user from budget display";

    //create User to hold the user object passed from login
    private User loggedInUser;
    private ArrayList<String> storedCurrencies = new ArrayList<>();
    private String userName;
    private String newUser_Username; //for retrieving user info for newly created users

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
    private TextView breakdownHeader;
    private TextView totalExpensesCurrencyLabel;
    private TextView expensePerDayCurrencyLabel;

    //variable to store current currency rate and current currency name
    private Double currencyRate;
    private String currencyName;

    //color scheme for pie chart
    public static final int[] pieColors = {Color.rgb(243, 66, 53), Color.rgb(155, 38, 175), Color.rgb(0, 187, 211),
            Color.rgb(232, 29, 98), Color.rgb(102, 57, 182), Color.rgb(0, 149, 135), Color.rgb(62, 80, 180), Color.rgb(32, 149, 242),
            Color.rgb(75, 174, 79), Color.rgb(254, 234, 58), Color.rgb(254, 192, 6), Color.rgb(138, 194, 73), Color.rgb(120, 84, 71),
            Color.rgb(157, 157, 157), Color.rgb(95, 124, 138)};

    //declare pie chart
    PieChart pieChart;

    //ACTIONS
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_display);
        BudgetDisplayPage.this.setTitle(R.string.app_title);

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
            else if(getIntent().getSerializableExtra(ProfileSettingsActivity.TAG_USER_PROFILE_PAGE) != null){
                loggedInUser = (User) getIntent().getSerializableExtra(ProfileSettingsActivity.TAG_USER_PROFILE_PAGE);
            }
            else {
                newUser_Username = getIntent().getStringExtra(AccountSetupPageTwo.TAG_USER_SETUP2);

                loggedInUser = databaseAccessor.grabUser(newUser_Username).get(0);
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
        txt = txt + ", " + userName + "!";
        welcomeHeader.setText(txt);


        //breakdown header
        breakdownHeader = (TextView) findViewById(R.id.monthHeader);
        String dynamicMonthText = "Your " + OffsetDateTime.now(ZoneId.systemDefault()).getMonth().toString().substring(0,1) +
                OffsetDateTime.now(ZoneId.systemDefault()).getMonth().toString().substring(1).toLowerCase() + " Overview";
        breakdownHeader.setText(dynamicMonthText);

        //graph labels
        totalExpensesCurrencyLabel = (TextView) findViewById(R.id.currencyLabelGraph1);
        totalExpensesCurrencyLabel.setText("USD");
        expensePerDayCurrencyLabel = (TextView) findViewById(R.id.currencyLabel2);
        expensePerDayCurrencyLabel.setText("USD");

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
                expensesPerDay[dayOfExpense - 1] = expensesPerDay[dayOfExpense - 1] + expenseRecord.getAmount();//based on the day value of the expense, adds the expense amount to that position in array
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
            dataPoints.add(new Entry((i + 1), (float) expensesPerDay[i])); //value of the x is the day of the month (i+1), value of y is the total amount spent that day
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
        expensesPerDayLineChart.setAutoScaleMinMaxEnabled(true);
        expensesPerDayLineChart.setVisibleXRange(1, daysInCurrentMonth);
        expensesPerDayLineChart.setDrawBorders(true);
        expensesPerDayLineChart.setBorderColor(Color.parseColor("#434343"));
        expensesPerDayLineChart.setDescription(null);
        expensesPerDayLineChart.setExtraBottomOffset(2f);
        expensesPerDayLineChart.setExtraLeftOffset(30f);
        legend.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawGridLinesBehindData(false);
        yAxisLeft.setDrawGridLinesBehindData(false);
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setSpaceTop(10);
        xAxis.setAxisLineColor(Color.parseColor("#434343"));
        yAxisLeft.setAxisLineColor(Color.parseColor("#434343"));
        xAxis.setAxisLineWidth(2f);
        yAxisLeft.setAxisLineWidth(2f);
        xAxis.setTextSize(14);
        yAxisLeft.setTextSize(14);
        yAxisLeft.setYOffset(10);

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

                if (dayOfExpense == (i + 1) && monthOfExpense == currentMonth && yearOfExpense == currentYear) {
                    runningExpenseSumPerDay[i] += expenseRecord.getAmount();
                }
            }

            if (i > 0) {
                runningExpenseSumPerDay[i] += runningExpenseSumPerDay[i - 1];
            }
        }

        List<Entry> dataPointsSet2 = new ArrayList<Entry>(); //convert expense totals per day of current month into a set of data points
        for (int i = 0; i < runningExpenseSumPerDay.length; i++) {
            // turn your data into Entry objects
            dataPointsSet2.add(new Entry((i + 1), (float) runningExpenseSumPerDay[i]));//value of the x is the day of the month (i+1), value of y is the total amount spent that day
        }

        float monthlyBudget = loggedInUser.getMonthlyBudget();
        float maxYvalue = (monthlyBudget > runningExpenseSumPerDay[currentDayInMonth-1]) ? loggedInUser.getMonthlyBudget() : (float) runningExpenseSumPerDay[currentDayInMonth]; //sets the max y value of the graph as either the budget or your current expense total, depending on which is bigger

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
        } else {
            dataSet2.setColor(Color.parseColor("#ff0033"));
            dataSet2.setFillColor(Color.parseColor("#ff0033"));
            dataSet2.setCircleColor(Color.parseColor("#ff0033"));
        }

        //plot dynamic budget line
        float[] budgetArray = new float[daysInCurrentMonth];
        List<Entry> budgetPoints = new ArrayList<Entry>();

        for (int i = 0; i <= daysInCurrentMonth; i++)
            budgetPoints.add(new Entry((i+1), monthlyBudget));

        //budget line styling
        LineDataSet budgetSet = new LineDataSet(budgetPoints, "Your Monthly Budget");
        budgetSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        budgetSet.setLineWidth(5f); //default is 1f
        budgetSet.setDrawCircles(false);
        budgetSet.setDrawValues(false);
        budgetSet.setColor(Color.parseColor("#FED215"));


        ArrayList<ILineDataSet> lineDataSets2 = new ArrayList<ILineDataSet>();
        lineDataSets2.add(dataSet2);
        lineDataSets2.add(budgetSet);
        LineData data2 = new LineData(lineDataSets2);
        totalExpensesOverTimeChart.setData(data2); //draw chart

        //chart styling
        totalExpensesOverTimeChart.setAutoScaleMinMaxEnabled(true);
        totalExpensesOverTimeChart.setVisibleXRange(1, daysInCurrentMonth);
        totalExpensesOverTimeChart.setDrawBorders(true);
        totalExpensesOverTimeChart.setBorderColor(Color.parseColor("#434343"));
        totalExpensesOverTimeChart.setDescription(null);
        totalExpensesOverTimeChart.setExtraBottomOffset(2f);
        totalExpensesOverTimeChart.setExtraLeftOffset(30f);
        legend2.setEnabled(false);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        yAxisRight2.setEnabled(false);
        yAxisRight2.setDrawGridLinesBehindData(false);
        yAxisLeft2.setDrawGridLinesBehindData(false);
        yAxisLeft2.setAxisMinimum(0);
        yAxisLeft2.setSpaceTop(10);
        xAxis2.setAxisLineColor(Color.parseColor("#434343"));
        yAxisLeft2.setAxisLineColor(Color.parseColor("#434343"));
        xAxis2.setAxisLineWidth(2f);
        yAxisLeft2.setAxisLineWidth(2f);
        xAxis2.setTextSize(14);
        yAxisLeft2.setTextSize(14);
        xAxis2.setXOffset(10);
        yAxisLeft2.setYOffset(10);

        //END OF CHART 2 IMPLEMENTATION

        //BEGINNING OF PIE CHART IMPLEMENTATION

        pieChart = findViewById(R.id.categories_pieChart);

        List<PieEntry> pieEntries = new ArrayList<>();

        //need to calculate the percentages for each pie slice

        //declare variables to hold category percents
        float entertainmentPercentage;
        float educationPercentage;
        float shoppingPercentage;
        float personalCarePercentage;
        float healthAndFitnessPercentage;
        float kidsPercentage;
        float foodAndDiningPercentage;
        float giftsAndDonationsPercentage;
        float investmentsPercentage;
        float billsAndUtilitiesPercentage;
        float autoAndTransportPercentage;
        float travelPercentage;
        float feesAndChargesPercentage;
        float businessServicesPercentage;
        float taxesPercentage;

        //running sums per category
        float entertainment = 0;
        float education = 0;
        float shopping = 0;
        float personalCare = 0;
        float healthAndFitness = 0;
        float kids = 0;
        float foodAndDining = 0;
        float giftsAndDonations = 0;
        float investments = 0;
        float billsAndUtilities = 0;
        float autoAndTransport = 0;
        float travel = 0;
        float feesAndCharges = 0;
        float businessServices = 0;
        float taxes = 0;

        //loop through all expenses for the month, check category, add to temporary sum for that category
        //then divide each by total to get respective percentage
        for (int i = 0; i < userExpenses.size(); i++) { //goes through entire list of user's expenses, and populates expensesPerDay array with expenses on each day

            Expense expenseRecord = userExpenses.get(i);

            int monthOfExpense = expenseRecord.getTimestamp().getMonthValue();
            int yearOfExpense = expenseRecord.getTimestamp().getYear();
            int currentMonth = (OffsetDateTime.now(ZoneId.systemDefault())).getMonthValue();
            int currentYear = (OffsetDateTime.now(ZoneId.systemDefault())).getYear();

            if (monthOfExpense == currentMonth && yearOfExpense == currentYear) { //checks to see if the expense in question occurred in the current calendar month and year

                switch (expenseRecord.getCategory()) {
                    case "Entertainment":
                        entertainment += (float) expenseRecord.getAmount();
                        break;
                    case "Education":
                        education += (float) expenseRecord.getAmount();
                        break;
                    case "Shopping":
                        shopping += (float) expenseRecord.getAmount();
                        break;
                    case "Personal Care":
                        personalCare += (float) expenseRecord.getAmount();
                        break;
                    case "Health & Fitness":
                        healthAndFitness += (float) expenseRecord.getAmount();
                        break;
                    case "Kids":
                        kids += (float) expenseRecord.getAmount();
                        break;
                    case "Food & Dining":
                        foodAndDining += (float) expenseRecord.getAmount();
                        break;
                    case "Gifts & Donations":
                        giftsAndDonations += (float) expenseRecord.getAmount();
                        break;
                    case "Investments":
                        investments += (float) expenseRecord.getAmount();
                        break;
                    case "Bills & Utilities":
                        billsAndUtilities += (float) expenseRecord.getAmount();
                        break;
                    case "Auto & Transport":
                        autoAndTransport += (float) expenseRecord.getAmount();
                        break;
                    case "Travel":
                        travel += (float) expenseRecord.getAmount();
                        break;
                    case "Fees & Charges":
                        feesAndCharges += (float) expenseRecord.getAmount();
                        break;
                    case "Business Services":
                        businessServices += (float) expenseRecord.getAmount();
                        break;
                    case "Taxes":
                        taxes += (float) expenseRecord.getAmount();
                        break;
                }
            }
        }

        //calculate percentages per category

        entertainmentPercentage = (float) (entertainment/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        educationPercentage = (float) (education/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        shoppingPercentage = (float) (shopping/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        personalCarePercentage = (float) (personalCare/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        healthAndFitnessPercentage  = (float) (healthAndFitness/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        kidsPercentage = (float) (kids/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        foodAndDiningPercentage = (float) (foodAndDining/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        giftsAndDonationsPercentage = (float) (giftsAndDonations/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        investmentsPercentage = (float) (investments/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        billsAndUtilitiesPercentage = (float) (billsAndUtilities/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        autoAndTransportPercentage = (float) (autoAndTransport/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        travelPercentage = (float) (travel/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        feesAndChargesPercentage = (float) (feesAndCharges/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        businessServicesPercentage = (float) (businessServices/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;
        taxesPercentage = (float) (taxes/runningExpenseSumPerDay[currentDayInMonth-1]) * 100;

        //fill data

        if (entertainmentPercentage > 0) pieEntries.add(new PieEntry(entertainmentPercentage, "Entertainment"));
        if (educationPercentage > 0) pieEntries.add(new PieEntry(educationPercentage, "Education"));
        if (shoppingPercentage > 0) pieEntries.add(new PieEntry(shoppingPercentage, "Shopping"));
        if (personalCarePercentage > 0) pieEntries.add(new PieEntry(personalCarePercentage, "Personal Care"));
        if (healthAndFitnessPercentage > 0) pieEntries.add(new PieEntry(healthAndFitnessPercentage, "Health & Fitness"));
        if (kidsPercentage > 0) pieEntries.add(new PieEntry(kidsPercentage, "Kids"));
        if (foodAndDiningPercentage > 0) pieEntries.add(new PieEntry(foodAndDiningPercentage, "Food & Dining"));
        if (giftsAndDonationsPercentage > 0) pieEntries.add(new PieEntry(giftsAndDonationsPercentage, "Gifts & Donations"));
        if (investmentsPercentage > 0) pieEntries.add(new PieEntry(investmentsPercentage, "Investments"));
        if (billsAndUtilities > 0) pieEntries.add(new PieEntry(billsAndUtilitiesPercentage, "Bills & Utilities"));
        if (autoAndTransportPercentage > 0) pieEntries.add(new PieEntry(autoAndTransportPercentage, "Auto & Transport"));
        if (travelPercentage > 0) pieEntries.add(new PieEntry(travelPercentage, "Travel"));
        if (feesAndChargesPercentage > 0) pieEntries.add(new PieEntry(feesAndChargesPercentage, "Fees & Charges"));
        if (businessServicesPercentage > 0) pieEntries.add(new PieEntry(businessServicesPercentage, "Business Services"));
        if (taxesPercentage > 0) pieEntries.add(new PieEntry(taxesPercentage, "Taxes"));

        //compile in data set
        PieDataSet pieSet = new PieDataSet(pieEntries, null);
        pieSet.setSliceSpace(3f);
        pieSet.setSelectionShift(5f);
        pieSet.setColors(pieColors);

        PieData pieData = new PieData(pieSet);
        pieData.setValueTextSize(13f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new PercentFormatter(pieChart));

        if (runningExpenseSumPerDay[currentDayInMonth-1] != 0)
            pieChart.setData(pieData);
        pieChart.setNoDataText("No expenses to show - add one to start!");
        pieChart.setNoDataTextColor(Color.LTGRAY);
        pieChart.getPaint(Chart.PAINT_INFO).setTextSize(Utils.convertDpToPixel(17f));
        pieChart.invalidate(); // refresh

        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 5, 5, 5);
        pieChart.setDescription(null);
        Legend pieLegend = pieChart.getLegend();
        pieLegend.setEnabled(true);
        pieLegend.setTextSize(14f);
        pieLegend.setWordWrapEnabled(true);

        //<--------------GETTING DATABASE STUFF ENDS HERE-------------->


        //<--------------CURRENCY FEATURE STARTS HERE-------------->

        //Start async activity to fetch currency data
        FetchCurrencyData fetch = new FetchCurrencyData();
        fetch.execute();

        //------SPINNER 1: CURRENCY MENU------//
        //connecting the spinner to layout
        addCurrency = (Spinner) findViewById(R.id.currencyMenu);

        //Setting the ArrayAdapter data on the add-currency spinner
        aa = new ArrayAdapter<String>(BudgetDisplayPage.this, android.R.layout.simple_spinner_item, names);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCurrency.setAdapter(aa);

        //setting on select for currency to be added to database based on select
        addCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    String selectedCurrency = addCurrency.getSelectedItem().toString();

                    for (int i = 0; i < loggedInUser.getSavedCurrencies().size(); i++) //checks to make sure the currency hasn't already been added to profile
                    {
                        if (loggedInUser.getSavedCurrencies().get(i).equals(selectedCurrency)) {
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
        ab = new ArrayAdapter<String>(BudgetDisplayPage.this, android.R.layout.simple_spinner_item, storedCurrencies);
        ab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currCurrency.setAdapter(ab);

        //add onselect, selected currency rate gets stored in double currencyRate
        currCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencyName = currCurrency.getSelectedItem().toString();
                String translationCurrency = currCurrency.getSelectedItem().toString();
                //get correct currency rate
                if (translationCurrency.equals("USD")) {
                    currencyRate = 1.0;
                    Log.d(TAG_DEBUG, "currency is usd, rate is: " +currencyRate);

                    //update graphical display back to USD from other currencies
                    for (int i = 0; i < dataPoints.size(); i++)
                        dataPoints.set(i, new Entry((i+1), (float) (expensesPerDay[i])));

                    for (int i = 0; i < dataPointsSet2.size(); i++)
                        dataPointsSet2.set(i, new Entry((i+1), (float) (runningExpenseSumPerDay[i])));

                    for (int i = 0; i <= daysInCurrentMonth; i++)
                        budgetPoints.set(i, new Entry((i+1), monthlyBudget));

                    budgetSet.notifyDataSetChanged();
                    dataSet.notifyDataSetChanged();
                    dataSet2.notifyDataSetChanged();
                    expensesPerDayLineChart.notifyDataSetChanged();
                    totalExpensesOverTimeChart.notifyDataSetChanged();
                    expensesPerDayLineChart.invalidate();
                    totalExpensesOverTimeChart.invalidate();

                    //adjust axis labels
                    totalExpensesCurrencyLabel.setText("USD");

                }
                else{
                    Log.d(TAG_DEBUG, "namesRatesArray size: " + namesRates.size());
                    for (int i = 0; i < namesRates.size(); i++) {
                        if (namesRates.get(i).get(CURRENCYNAME).equals(translationCurrency)) {
                            currencyRate = Double.parseDouble(namesRates.get(i).get(CURRENCYRATE));
                            Log.d(TAG_DEBUG, "currency is " + namesRates.get(i).get(CURRENCYNAME) + ", rate is" + currencyRate);
                        }
                    }
                    //change graphical displays based on currency rates
                    for (int i = 0; i < dataPoints.size(); i++)
                        dataPoints.set(i, new Entry((i+1), (float) (expensesPerDay[i]*currencyRate)));

                    for (int i = 0; i < dataPointsSet2.size(); i++)
                        dataPointsSet2.set(i, new Entry((i+1), (float) (runningExpenseSumPerDay[i]*currencyRate)));

                    for (int i = 0; i <= daysInCurrentMonth; i++)
                        budgetPoints.set(i, new Entry((i+1), (float) (monthlyBudget * currencyRate)));

                    budgetSet.notifyDataSetChanged();
                    dataSet.notifyDataSetChanged();
                    dataSet2.notifyDataSetChanged();
                    expensesPerDayLineChart.notifyDataSetChanged();
                    totalExpensesOverTimeChart.notifyDataSetChanged();
                    expensesPerDayLineChart.invalidate();
                    totalExpensesOverTimeChart.invalidate();

                    //adjust axis labels
                    totalExpensesCurrencyLabel.setText(translationCurrency);
                    expensePerDayCurrencyLabel.setText(translationCurrency);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (currCurrency.getItemAtPosition(0).toString().equals("USD")) {
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

        switch (v.getId()) {
            case R.id.addExpenseButton: {
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


        //<---------------TOOLBAR STARTS HERE--------------->


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.display_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miLogout:
                Intent logOut = new Intent(BudgetDisplayPage.this, MainActivity.class);
                //Launch second page of account setup
                startActivity(logOut);
                return true;
            case R.id.miProfile:
                Intent goToSettings = new Intent(BudgetDisplayPage.this, ProfileSettingsActivity.class);
                goToSettings.putExtra(TAG_USER_BUDGET_DISPLAY, loggedInUser);
                //Launch second page of account setup
                startActivity(goToSettings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        //<---------------TOOLBAR ENDS HERE--------------->
    }
