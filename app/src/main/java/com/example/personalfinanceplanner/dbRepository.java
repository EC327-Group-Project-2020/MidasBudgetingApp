package com.example.personalfinanceplanner;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class dbRepository {

        private UserDAO mUserDao;
        private LiveData<List<User>> mAllUsers;

        dbRepository(Application application) {
            AppDatabase db = AppDatabase.getDatabase(application);
            mUserDao = db.userDao();
            mAllUsers = mUserDao.loadAllUsers();
        }

        // Room executes all queries on a separate thread.
        // Observed LiveData will notify the observer when the data has changed.
        LiveData<List<User>> getAllUsers() {
            return mAllUsers;
        }

        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        void insert(User user) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.insertUser(user);
            });
        }

        void update(User user) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.updateUser(user);
            });
        }

        void delete(User user) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.deleteUser(user);
            });
        }

        void deleteAll() {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.deleteAll();
            });
        }

        List<User> queryUser(String username) { //return non-Live version of given user for non-UI dependent data
            return mUserDao.loadGivenUser(username);
        }

        LiveData<List<User>> queryUserLive(String username) { //return LiveData version of given user
            return mUserDao.loadGivenUserLive(username);
        }
}