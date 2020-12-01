package com.example.personalfinanceplanner;

import android.app.Application;

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

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccountSetupPageOneAndroidTest {

    private AppDatabase dummyDatabase;
    private UserDAO dummyDao;
    List<User> dummyUser;


    @Before
    public void setUp(){
        dummyDatabase =  Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase.class).build();
        dummyDao = dummyDatabase.userDao();
    }

    @Test
    public void userNameNotAvailable() {
        dummyUser = dummyDao.loadGivenUser("Nanna");
        Boolean result = AccountSetupPageOne.userAlreadyExists(dummyUser);
        assertTrue(result);
    }

    @After
    public void tearDown() throws IOException {
        dummyDatabase.close();
    }
}

