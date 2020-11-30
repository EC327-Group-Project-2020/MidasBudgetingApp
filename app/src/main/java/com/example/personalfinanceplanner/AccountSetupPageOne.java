package com.example.personalfinanceplanner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AccountSetupPageOne extends AppCompatActivity implements View.OnClickListener {

    //Creating the EditText objects for account creation page
    private EditText username_entry;
    private EditText password_entry;
    private EditText questionOneAnswer;
    private EditText questionTwoAnswer;

    //Creating Button
    private Button next_button;

    //Create dropdown lists for security questions
    private Spinner questionOneList;
    private Spinner questionTwoList;

    //Create ViewModel for read/write capabilities to database
    private dbViewModel accessDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation_page1);

        accessDatabase = new dbViewModel(getApplication());

        next_button = (Button) findViewById(R.id.next_button);

        username_entry = (EditText) findViewById(R.id.username_field);
        password_entry = (EditText) findViewById(R.id.password);
        questionOneAnswer = (EditText) findViewById(R.id.securityAnswerOne);
        questionTwoAnswer = (EditText) findViewById((R.id.securityAnswerTwo));

        //create Security Question One Dropdown List
        questionOneList = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> questionOneListAdapter = new ArrayAdapter<String>(AccountSetupPageOne.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.securityQuestionOneChoices));
        questionOneListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionOneList.setAdapter(questionOneListAdapter);

        //create Security Question Two Dropdown List
        questionTwoList = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> questionTwoListAdapter = new ArrayAdapter<String>(AccountSetupPageOne.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.securityQuestionTwoChoices));
        questionTwoListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionTwoList.setAdapter(questionTwoListAdapter);

        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //grab all of the inputs from the text fields and store in String variables
        String usernameInput = username_entry.getText().toString();
        String passwordInput = password_entry.getText().toString();
        String questionOneChoice = questionOneList.getSelectedItem().toString();
        String answerOneInput = questionOneAnswer.getText().toString();
        String questionTwoChoice = questionTwoList.getSelectedItem().toString();
        String answerTwoInput = questionTwoAnswer.getText().toString();

        //check to make sure all fields were filled, and that security question choices aren't the prompt string; may want to explore making button clickable only if all fields are full
        if (usernameInput.equals("") || passwordInput.equals("") || questionOneChoice.equals("") || questionOneChoice.equals("Please select a security question.")
                || answerOneInput.equals("") || questionTwoChoice.equals("") || questionTwoChoice.equals("Please select a security question.")
                || answerTwoInput.equals("")) {
            //break out of the onClick function if any of the fields aren't properly filled
            Toast.makeText(AccountSetupPageOne.this, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            return;
        }

        //launch into second page of account setup if all fields have been filled as required
        launchAccountSetupPageTwo(usernameInput, passwordInput, questionOneChoice, answerOneInput, questionTwoChoice, answerTwoInput);
    }

    private void launchAccountSetupPageTwo(String usernameInput, String passwordInput, String questionOneChoice, String answerOneInput,
                                              String questionTwoChoice, String answerTwoInput) {

        //create User Entity to be stored in the database
        User createdUser = new User(usernameInput, passwordInput, questionOneChoice, questionTwoChoice, answerOneInput, answerTwoInput);

        //store user in Room Database via dbViewModel methods
        accessDatabase.insert(createdUser);

        Intent setupPageTwoActivity = new Intent(AccountSetupPageOne.this, MainActivity.class); //need to change this from Main to actual second page

        //Launch second page of account setup
        startActivity(setupPageTwoActivity);
    }
}
