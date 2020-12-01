package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    //Debug tag
    private static final String TAG_DEBUG = LogInActivity.class.getName();

    //declare login button
    private Button login_button;

    //declare username and password fields
    private EditText username_field;
    private EditText password_field;

    //declare ViewModel for read/write capabilities to database
    private dbViewModel accessDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = (Button) findViewById(R.id.log_in_button);

        username_field = (EditText) findViewById(R.id.loginUsername);
        password_field = (EditText) findViewById(R.id.loginPassword);

        accessDatabase = new dbViewModel(getApplication());

        login_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        String usernameInput = username_field.getText().toString();
        String passwordInput = password_field.getText().toString();

        List<User> queriedUserList = accessDatabase.grabUser(usernameInput);

        //check to make sure all fields were filled
        if (emptyFields(usernameInput, passwordInput)) {
            Toast.makeText(LogInActivity.this, R.string.user_fields_empty_error, Toast.LENGTH_LONG).show();
            return;     //break out of the onClick function if any of the fields aren't properly filled
        }

        //check if the username exists in the database
        if(badUserInfo(queriedUserList,passwordInput)){
            Toast.makeText(LogInActivity.this, getResources().getString(R.string.incorrect_login_creds), Toast.LENGTH_LONG).show();
            return;
        }

        //if the username exists, check that the provided password matches that of the user object
        else
        {
            User queriedUser = queriedUserList.get(0);
            System.out.println(queriedUserList.get(0).getUsername());
            String passwordOnRecord = queriedUser.getPassword();

            if (!passwordOnRecord.equals(passwordInput))
            {
                Toast.makeText(LogInActivity.this, getResources().getString(R.string.incorrect_login_creds), Toast.LENGTH_LONG).show();
                return;
            }
        }

        //NEED TO ADD/EDIT BELOW LOGIC TO LAUNCH THE DASHBOARD PAGE BASED ON THE INFORMATION ASSOCIATED WITH PROVIDED VALID USER

        launchBudgetDisplayPage(queriedUserList.get(0));
    }

    private void launchBudgetDisplayPage(User validUser) {


        Intent setupBudgetDisplayPage = new Intent(LogInActivity.this, BudgetDisplayPage.class);
        setupBudgetDisplayPage.putExtra("valid_user", validUser);

        //Launch second page of account setup
        startActivity(setupBudgetDisplayPage);
    }

    //FUNCTIONS TO TEST USER INPUT BEFORE ONCLICK ACTIVATES

    public static Boolean emptyFields(String username, String password){       /*returns true if not all fields are filled*/
        if(username.equals("") || password.equals("")){
            return true;
        }
        return false;
    }

    public static Boolean badUserInfo(List<User> queriedUserList, String passwordInput){       /*returns true if username doesn't exist or if username and password don't match*/
        if(queriedUserList.isEmpty()){
            Log.d(TAG_DEBUG, "User doesn't exist.");
            return true;
        }
        else if(!queriedUserList.get(0).getPassword().equals(passwordInput)){
            Log.d(TAG_DEBUG, "Password doesn't match user");
            return true;
        }
        Log.d(TAG_DEBUG, "User info all good");
        return false;
    }
}
