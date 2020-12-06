package com.example.personalfinanceplanner;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.List;

public class AccountSetupPageOne extends AppCompatActivity implements View.OnClickListener {

    //Debug tag
    private static final String TAG_DEBUG = AccountSetupPageOne.class.getName();

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

    //Tag for username when passed on to next activity
    public static final String TAG_USER_SETUP1 = "user from setup page 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation_page1);
        //AccountSetupPageOne.this.getActionBar().hide();

        accessDatabase = new dbViewModel(getApplication());

        next_button = (Button) findViewById(R.id.updateButton);

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

        //use function to validate registration (easier for testing)
        if(!validateRegistrationInput(usernameInput,passwordInput,questionOneChoice,answerOneInput,questionTwoChoice,answerTwoInput)){
            Toast.makeText(AccountSetupPageOne.this,getResources().getString(R.string.user_fields_empty_error), Toast.LENGTH_LONG).show();
            //break out of the onClick function if any of the fields aren't properly filled
            return;
        }

        //use function to check if user is already in database
        if(userAlreadyExists(accessDatabase.grabUser(usernameInput))){
            Toast.makeText(AccountSetupPageOne.this,getResources().getString(R.string.username_already_taken_error), Toast.LENGTH_LONG).show();
            //break out of the onClick function if any of the fields aren't properly filled
            return;
        }

        User createdUser = new User(usernameInput, passwordInput, questionOneChoice, questionTwoChoice, answerOneInput, answerTwoInput);

        //launch into second page of account setup if all fields have been filled as required

        launchTutorialPage(createdUser);
    }

    private void launchTutorialPage(User user) {

        //store user in Room Database via dbViewModel methods

        Intent launchTutorialPage = new Intent(AccountSetupPageOne.this, TutorialActivity.class);

        //username passed on to continue initialisation of user
        launchTutorialPage.putExtra(TAG_USER_SETUP1,user);

        //Launch second page of account setup
        startActivity(launchTutorialPage);
    }


    //FUNCTIONS TO TEST USER INPUT BEFORE ONCLICK ACTIVATES
    public static Boolean validateRegistrationInput(String username, String password, String question1, String question1Answer,
                                                    String question2, String question2Answer)    /*returns false if there is an empty field*/
        { if (username.equals("") || password.equals("") || question1.equals("") || question1.equals("Please select a security question.")
                || question1Answer.equals("") || question2.equals("") || question2.equals("Please select a security question.")
                || question2Answer.equals("")) {
            return false;
        }
        return true;
    }

    public static Boolean userAlreadyExists(List<User> user){       /*returns true if user is already in database */
        Log.d(TAG_DEBUG, "inside account page function!");
        if(user.isEmpty()){
            return false;
        }
        return true;
    }
}