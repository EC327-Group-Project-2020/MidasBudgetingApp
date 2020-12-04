package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity2 extends AppCompatActivity implements View.OnClickListener {

    private Button getStarted;

    //Tag and var to be passed on to next activity
    public static final String TAG_USER_TUTORIAL2 = "user from tutorial page2";
    private User validUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_intro2);
        getStarted = (Button) findViewById(R.id.get_started_btn);
        getStarted.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //add income to database
        validUser = (User) getIntent().getExtras().getSerializable(TutorialActivity.TAG_USER_TUTORIAL1);

        //Launch new activity
        launchAccountSetup2Page(validUser);

    }

    private void launchAccountSetup2Page(User validUser) {
        Intent setupAccountTwoPage = new Intent(TutorialActivity2.this, AccountSetupPageTwo.class);
        setupAccountTwoPage.putExtra(TAG_USER_TUTORIAL2,validUser);

        //Launch second page of account setup
        startActivity(setupAccountTwoPage);
    }
}
