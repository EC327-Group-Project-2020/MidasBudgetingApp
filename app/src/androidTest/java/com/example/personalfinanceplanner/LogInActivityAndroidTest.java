package com.example.personalfinanceplanner;

import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
@SmallTest
public class LogInActivityAndroidTest {

    private AppDatabase dummyDatabase;
    private UserDAO dummyDao;
    private final String TAG_DEBUG = LogInActivityAndroidTest.class.getName();
    private List<User> dummyUser;


    @Before
    public void setUp(){
        dummyDatabase =  Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase.class).allowMainThreadQueries().build();
        dummyDao = dummyDatabase.userDao();
        User dummyUser = (User) new User("nanna","123","blabla", "yoyo", "hihi","dodo");
        dummyDao.insertUser(dummyUser);
        Log.d(TAG_DEBUG, "Dummy database set up");
    }

    @After
    public void tearDown() throws IOException {
        dummyDatabase.close();
        Log.d(TAG_DEBUG, "Dummy database closed");
    }

    @Test
    public void userNonExist(){
            dummyUser = dummyDao.loadGivenUser("fibo");
            Boolean result = LogInActivity.badUserInfo(dummyUser,"123");
            assertTrue(result);
    }

    @Test
    public void passwordForgot(){
        dummyUser = dummyDao.loadGivenUser("nanna");
        Boolean result = LogInActivity.badUserInfo(dummyUser,"567");
        assertTrue(result);
    }

    @Test
    public void loginCredsCorrect(){
        dummyUser = dummyDao.loadGivenUser("nanna");
        Boolean result = LogInActivity.badUserInfo(dummyUser,"123");
        assertFalse(result);
    }
}

