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

        void insert(User user) {

            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.insertUser(user);
            });
        }

        void insertExpense(Expense expense) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.insertExpense(expense);
            });
        }

        void update(User user) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.updateUser(user);
            });
        }

        void updateExpense(Expense expense) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.updateExpense(expense);
            });
        }

        void deleteUser(User user) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.deleteUser(user);
            });
        }

        void deleteExpense(Expense expense) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                mUserDao.deleteExpense(expense);
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

        List<User> queryUserUsingID(long userID) { //return non-Live version of given user for non-UI dependent data
            return mUserDao.loadGivenUserUsingID(userID);
        }

        LiveData<List<User>> queryUserLive(String username) { //return LiveData version of given user
            return mUserDao.loadGivenUserLive(username);
        }

        List<Expense> getUserExpenses(long userID) {
            return mUserDao.getUserExpenses(userID);
        }

        List<Expense> getExpensesInCategory(long userID, String category) { return mUserDao.getExpensesInCategory(userID, category); }

        List<UserWithExpenses> getUserWithExpenses(long userID) { //returns the list of expenses for the user with given userID
            return mUserDao.getUserWithExpenses(userID);
        }
}