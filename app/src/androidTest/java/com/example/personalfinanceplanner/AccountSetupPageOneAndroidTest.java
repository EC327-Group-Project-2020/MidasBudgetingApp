package com.example.personalfinanceplanner;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccountSetupPageOneAndroidTest {

    private static final String TAG_DEBUG = AccountSetupPageOneAndroidTest.class.getName();
    private AppDatabase dummyDatabase;
    private UserDAO dummyDao;
    private List<User> dummyUser;


    @Before
    public void setUp(){
        Context context = ApplicationProvider.getApplicationContext();
        dummyDatabase =  Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase.class).build();
        dummyDao = dummyDatabase.userDao();
        User dummyUser = (User) new User("hanna","hagaskoli","blabla", "yoyo", "hihi","dodo");
        dummyDao.insertUser(dummyUser);
    }

    @Test
    public void userNameNotAvailable() {
        Context context = ApplicationProvider.getApplicationContext();
        dummyUser = dummyDao.loadGivenUser("hanna");
        Boolean result = AccountSetupPageOne.userAlreadyExists(dummyUser);
        Log.d(TAG_DEBUG, "function finished, result value: " + result);
        assertTrue(result);
    }

    @Test
    public void userNameAvailable() {
        Context context = ApplicationProvider.getApplicationContext();
        dummyUser = dummyDao.loadGivenUser("fabio");
        Boolean result = AccountSetupPageOne.userAlreadyExists(dummyUser);
        assertFalse(result);
    }

    @After
    public void tearDown() throws IOException {
        dummyDatabase.close();
    }
}

