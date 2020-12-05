package com.example.personalfinanceplanner;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;

//User Data Access Object - contains the methods/functions used for accessing the AppDatabase
@Dao
public interface UserDAO {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user); //for adding a User to the Room database

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpense(Expense expense); //for adding expenses

    @Update
    void updateUser(User user); //for updating User in the database

    @Delete
    void deleteUser(User user); //for deleting a User from the database, if needed; uses the primary keys of the entities passed as parameters to search database

    //returns a list of all the users in the database, with ability to update UI via LiveData
    @Query("SELECT * FROM user_info") //parameter here refers to the @Entity table name specified in User_room class
    LiveData<List<User>> loadAllUsers(); //LiveData wrapping of list allows UI elements pulling queried data to update in real time with changes to database

    //loads non-live list of Users with the given username
    @Query("SELECT * FROM user_info WHERE username LIKE :providedUsername")
    List<User> loadGivenUser(String providedUsername);

    //loads the list of User entities that have the provided username, with ability to update UI via LiveData
    @Query("SELECT * FROM user_info WHERE username LIKE :providedUsername")
    LiveData<List<User>> loadGivenUserLive(String providedUsername);

    @Query("DELETE FROM user_info") //empties the user table
    void deleteAll();
    //add more queries here based on the information that needs to be searched and returned; must return LiveData for UI to update

    @Query("SELECT * FROM expense_info WHERE associated_userID LIKE :providedUserID") //returns list of expenses for a given user id
    List<Expense> getUserExpenses(long providedUserID);

    @Query("SELECT * FROM expense_info WHERE associated_userID LIKE :providedUserID AND category LIKE :providedCategory")
    List<Expense> getExpensesInCategory(long providedUserID, String providedCategory);

    @Transaction
    @Query("SELECT * FROM user_info WHERE userID LIKE :providedUserID") //returns the associated user with their expenses, based on the provided userID
    List<UserWithExpenses> getUserWithExpenses(long providedUserID);
    //THIS NEEDS TO BE TESTED
}
