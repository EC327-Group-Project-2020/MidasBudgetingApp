package com.example.personalfinanceplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ExpenseOverview  extends AppCompatActivity implements View.OnClickListener  {

    //declare expense variable
    private Expense chosenExpense;
    private User loggedInUser;

    //access database
    private dbViewModel databaseAccessor;

    //declare view items
    private Button deleteExpenseBtn;
    private Button updateExpenseBtn;
    private TextView expenseAmount;
    private Spinner expenseCategory;
    private TextView expenseNotes;
    private ImageView imageView;
    private TextView noImageText;

    public static final String TAG_USER_EXPENSE_OVERVIEW_PAGE = "user from expense overview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_overview_delete);

        databaseAccessor = new dbViewModel(getApplication());

        //imageView = findViewById(R.id.imageView);

        //everything crashed when i tried to put this in control statements...but would be nice to do so
        chosenExpense = (Expense) getIntent().getExtras().getSerializable(ProfileSettingsActivity.TAG_EXPENSE_PROFILE_PAGE);
        loggedInUser = (User) getIntent().getExtras().getSerializable(ProfileSettingsActivity.TAG_USER_PROFILE_PAGE);

        //populate view items with correct expense data
        expenseAmount = (TextView) findViewById(R.id.expense_field2);
        String amount = String.valueOf(chosenExpense.getAmount());
        expenseAmount.setText(amount);

        expenseCategory = (Spinner) findViewById(R.id.category_field2);
        ArrayAdapter<String> categoryListAdapter = new ArrayAdapter<String>(ExpenseOverview.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.expense_categories));
        categoryListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategory.setAdapter(categoryListAdapter);

        expenseCategory.setSelection(getSpinnerIndex(expenseCategory, chosenExpense.getCategory()));

        expenseNotes = (TextView) findViewById(R.id.notes_field2);
        expenseNotes.setText(chosenExpense.getExpenseNotes());

        //noImageText = (TextView) findViewById(R.id.no_image);

        //set onclick listener to button
        deleteExpenseBtn = (Button) findViewById(R.id.delete_button);
        updateExpenseBtn = (Button) findViewById(R.id.update_button);
        updateExpenseBtn.setOnClickListener(this);
        deleteExpenseBtn.setOnClickListener(this);

        /*
        if (chosenExpense.getReceiptImageFilepath() != null) {

            String filepath = chosenExpense.getReceiptImageFilepath();
            Uri imageUri = Uri.parse(filepath);
            System.out.println("IMAGE URI: " + imageUri);

            Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        }
        else
            noImageText.setText(R.string.empty_image_message);
        */
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.delete_button:

                databaseAccessor.deleteExpense(chosenExpense);
                Toast.makeText(ExpenseOverview.this, getResources().getString(R.string.expense_deleted), Toast.LENGTH_LONG).show();
                launchProfileSettings(loggedInUser);

                break;

            case R.id.update_button:

                String amountAsString = expenseAmount.getText().toString();
                String categorySelection = expenseCategory.getSelectedItem().toString();
                String notes = expenseNotes.getText().toString();

                chosenExpense.setAmount(Double.parseDouble(amountAsString));
                chosenExpense.setCategory(categorySelection);
                chosenExpense.setExpenseNotes(notes);

                databaseAccessor.updateExpense(chosenExpense);
                Toast.makeText(ExpenseOverview.this, getResources().getString(R.string.expense_updated), Toast.LENGTH_LONG).show();
                launchProfileSettings(loggedInUser);

                break;
        }

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

    private int getSpinnerIndex(Spinner spinner, String spinnerText) {
        int i = 0;

        while (!spinner.getItemAtPosition(i).toString().equals(spinnerText)) {
            i++;
        }

        return i;
    }

    private void launchProfileSettings(User loggedInUser) {

        Intent backToProfileSettings = new Intent(ExpenseOverview.this, ProfileSettingsActivity.class);
        backToProfileSettings.putExtra(TAG_USER_EXPENSE_OVERVIEW_PAGE, loggedInUser);

        startActivity(backToProfileSettings);
    }


}
