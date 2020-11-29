package com.example.personalfinanceplanner;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

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

    public void delete(User user) { mRepository.delete(user); } //use to delete a user

    public void update(User user) { mRepository.update(user); } //use to update a user

    public void deleteAll() { mRepository.deleteAll(); } //use to wipe user database
}

/*
-Created a class called dbViewModel that gets the Application as a parameter and extends AndroidViewModel.

-Added a private member variable to hold a reference to the repository.

- Added a getAllUsers() method to return a cached list of users.

- Implemented a constructor that creates the dbRepository.

- In the constructor, initialized the allUsers LiveData using the repository.

- Created a wrapper insert() method that calls the Repository's insert() method. In this way, the
implementation of insert() is encapsulated from the UI.*/