package com.example.personalfinanceplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    //Creating Button
    private Button signInButton;
    private Button signUpButton;

    private dbViewModel accessDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set button parameters
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signUpButton = (Button) findViewById(R.id.create_account_button);

        //set on click listeners for the sign in and sign up buttons
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        accessDatabase = new dbViewModel(getApplication());
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.create_account_button:
            {
                launchAccountSetupPageOne();
                break;
            }
            case R.id.sign_in_button:
            {
                launchLogInActivity();
                break;
            }
            //INPUT OTHER CASES HERE TO CONTROL WHERE USER GOES BASED ON EXISTENCE OF ACCOUNTS, BUTTON PRESSED, ETC
        }
    }

    private void launchAccountSetupPageOne()
    {
        Intent setupPageOneActivity = new Intent(MainActivity.this, AccountSetupPageOne.class);

        //Launch first page of account setup
        startActivity(setupPageOneActivity);
    }

    private void launchLogInActivity()
    {

        Intent setupLogInPage = new Intent(MainActivity.this, LogInActivity.class);

        //launch login page
        startActivity(setupLogInPage);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}