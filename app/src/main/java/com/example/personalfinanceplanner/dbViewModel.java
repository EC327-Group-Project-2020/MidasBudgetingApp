package com.example.personalfinanceplanner;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

//used as the object through which all pages of the app interact with the database via repository encapsulation
public class dbViewModel extends AndroidViewModel {

    private dbRepository mRepository;

    private final LiveData<List<User>> mAllUsers;

    public dbViewModel (Application application) {
        super(application);
        mRepository = new dbRepository(application);
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() { return mAllUsers; }

    public void insert(User user) { mRepository.insert(user); } //use to add a user

    public void insertExpense(Expense expense) { mRepository.insertExpense(expense); }

    public void delete(User user) { mRepository.delete(user); } //use to delete a user

    public void update(User user) { mRepository.update(user); } //use to update a user

    public void deleteAll() { mRepository.deleteAll(); } //use to wipe user database

    public List<User> grabUser(String username) { //runs on main thread, so only used on login page where query is minimal. IF TIME PERMITS, MAY SWAP USERNAME AND PASS OUT OF DB AND ERASE THIS METHOD
        return mRepository.queryUser(username);
    }

    public LiveData<List<User>> grabUserLive(String username) { //search the database for the given user
        return mRepository.queryUserLive(username);
    }

    public List<Expense> getUserExpenses(long userID) { return mRepository.getUserExpenses(userID); }

    //grabs user with given userID and all associated expenses
    public List<UserWithExpenses> getUserWithExpenses(long userID) {
        return mRepository.getUserWithExpenses(userID);
    }
}