package com.example.personalfinanceplanner.UnitTests.suite;

import com.example.personalfinanceplanner.AccountSetupPageOneTest;
import com.example.personalfinanceplanner.AccountSetupPageTwo;
import com.example.personalfinanceplanner.AccountSetupPageTwoTest;
import com.example.personalfinanceplanner.LogInActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountSetupPageOneTest.class, LogInActivityTest.class, AccountSetupPageTwoTest.class})
public class LocalUnitTestSuite {
}
