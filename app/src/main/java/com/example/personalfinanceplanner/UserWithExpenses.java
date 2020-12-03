package com.example.personalfinanceplanner;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

//defines the relationship between the User Entity and the Expense entity, and associates them via a one-to-many relational database
public class UserWithExpenses {
    @Embedded public User user;
    @Relation(
            parentColumn = "userID",
            entityColumn = "associated_userID",
            entity = Expense.class
    )
    public List<Expense> expenses; //represents the list of expenses stored with each associated user ID
}
