package com.example.personalfinanceplanner;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountSetupPageTwoTest {

    @Test
    public void incomeFilled(){
        Boolean result = AccountSetupPageTwo.incomeEmpty("3000");
        assertFalse(result);
    }

    @Test
    public void emptyIncome(){
        Boolean result = AccountSetupPageTwo.incomeEmpty("");
        assertTrue(result);
    }

    @Test
    public void numberIncome(){
        Boolean result = AccountSetupPageTwo.incomeNotNumber("3562");
        assertFalse(result);
    }

    @Test
    public void stringIncome(){
        Boolean result = AccountSetupPageTwo.incomeNotNumber("abbbalabba");
        assertTrue(result);
    }

    @Test
    public void negativeIncome(){
        Boolean result = AccountSetupPageTwo.incomeNegativeOrZero("-1000");
        assertTrue(result);
    }

    @Test
    public void zeroIncome(){
        Boolean result = AccountSetupPageTwo.incomeNegativeOrZero("0");
        assertTrue(result);
    }

    @Test
    public void positiveIncome(){
        Boolean result = AccountSetupPageTwo.incomeNegativeOrZero("45");
        assertFalse(result);
    }
}