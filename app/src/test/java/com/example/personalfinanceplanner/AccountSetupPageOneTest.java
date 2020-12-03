package com.example.personalfinanceplanner;

import android.app.Instrumentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

public class AccountSetupPageOneTest {
    @Test
    public void emptyUserName() {
        Boolean result = AccountSetupPageOne.validateRegistrationInput("","12345","What was your childhood nickname?",
                "blob", "What was the first concert you attended?", "acdc");
        assertFalse(result);
    }

    @Test
    public void emptyPassWord() {
        Boolean result = AccountSetupPageOne.validateRegistrationInput("nanns","","What was your childhood nickname?",
                "blob", "What was the first concert you attended?", "acdc");
        assertFalse(result);
    }

    @Test
    public void question1NotSelected() {
        Boolean result = AccountSetupPageOne.validateRegistrationInput("nanns","","Please select a security question.",
                "blob", "What was the first concert you attended?", "acdc");
        assertFalse(result);
    }

    @Test
    public void question1NoAnswer() {
        Boolean result = AccountSetupPageOne.validateRegistrationInput("nanns","","What was your childhood nickname?",
                "", "What was the first concert you attended?", "acdc");
        assertFalse(result);
    }

    @Test
    public void question2NotSelected() {
        Boolean result = AccountSetupPageOne.validateRegistrationInput("nanns","","What was your childhood nickname?",
                "uuu", "Please select a security question.", "acdc");
        assertFalse(result);
    }


}