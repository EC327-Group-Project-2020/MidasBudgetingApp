package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
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

        //check to make sure all fields were filled, and that security question choices aren't the prompt string; may want to explore making button clickable only if all fields are full
        if (usernameInput.equals("") || passwordInput.equals("")) {
            //break out of the onClick function if any of the fields aren't properly filled
            Toast.makeText(LogInActivity.this, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            return;
        }

        //check if the username exists in the database
        if (queriedUserList.isEmpty()) {

            Toast.makeText(LogInActivity.this, getResources().getString(R.string.incorrect_login_creds), Toast.LENGTH_LONG).show();
            return;
        }
        //if the username exists, check that the provided password matches that of the user object
        else
        {
            User queriedUser = queriedUserList.get(0);
            String passwordOnRecord = queriedUser.getPassword();

            if (!passwordOnRecord.equals(passwordInput))
            {
                Toast.makeText(LogInActivity.this, getResources().getString(R.string.incorrect_login_creds), Toast.LENGTH_LONG).show();
                return;
            }
        }

        //NEED TO ADD/EDIT BELOW LOGIC TO LAUNCH THE DASHBOARD PAGE BASED ON THE INFORMATION ASSOCIATED WITH PROVIDED VALID USER

        launchUserDashboardPage();
    }

    private void launchUserDashboardPage() {

        Intent setupUserDashboardPage = new Intent(LogInActivity.this, MainActivity.class); //need to change this from Main to actual dashboard page

        //Launch second page of account setup
        startActivity(setupUserDashboardPage);
    }
}
