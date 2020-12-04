package com.example.personalfinanceplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener{

    private Button conTinue;

    //Tag and var to be passed on to next activity
    public static final String TAG_USER_TUTORIAL1 = "user from tutorial page1";
    private User validUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_intro);
        //TutorialActivity.this.getActionBar().hide();

        conTinue = (Button) findViewById(R.id.continue_btn);
        conTinue.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        //add income to database
        validUser = (User) getIntent().getExtras().getSerializable(AccountSetupPageOne.TAG_USER_SETUP1);

        //Launch new activity
        launchSecondTutorialPage(validUser);
    }

    private void launchSecondTutorialPage(User validUser) {
        Intent setupSecondTutorialPage = new Intent(TutorialActivity.this, TutorialActivity2.class);
        //setupBudgetDisplayPage.putExtra("valid_user", validUser);
        setupSecondTutorialPage.putExtra(TAG_USER_TUTORIAL1,validUser);
        //Launch second page of account setup
        startActivity(setupSecondTutorialPage);
    }

}
