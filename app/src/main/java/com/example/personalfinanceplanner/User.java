package com.example.personalfinanceplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "user_info")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long userID;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    @NonNull
    @ColumnInfo(name = "security_question_one")
    private String securityQuestionOne;

    @NonNull
    @ColumnInfo(name = "security_question_two")
    private String securityQuestionTwo;

    @NonNull
    @ColumnInfo(name = "security_answer_one")
    private String securityAnswerOne;

    @NonNull
    @ColumnInfo(name = "security_answer_two")
    private String securityAnswerTwo;

    @NonNull
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "saved_currency_list")
    private ArrayList<String> savedCurrencies = new ArrayList<String>(); //creates empty resizeable array

    //constructor
    public User(String username, String password, String securityQuestionOne, String securityQuestionTwo, String securityAnswerOne, String securityAnswerTwo)
    {
        this.username = username;
        this.password = password;
        this.securityQuestionOne = securityQuestionOne;
        this.securityQuestionTwo = securityQuestionTwo;
        this.securityAnswerOne = securityAnswerOne;
        this.securityAnswerTwo = securityAnswerTwo;
        this.savedCurrencies.add("USD");
    }

    //copy constructor
    public User(User user)
    {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.securityQuestionOne = user.getSecurityQuestionOne();
        this.securityQuestionTwo = user.getSecurityQuestionTwo();
        this.securityAnswerOne = user.getSecurityAnswerOne();
        this.securityAnswerTwo = user.getSecurityAnswerTwo();
        this.savedCurrencies = user.getSavedCurrencies();
    }

    /*NOTE: getter and setter names below should NOT be altered. In order to keep
    the data fields private in the user class and still allow Room to access them, the getters and
    setters must obey JavaBeans conventions for naming getters and setters for each data field, which
    is reflected in the names chosen for those member functions
     */

    //accessors and mutators
    public void setUsername(String new_name) {
        username = new_name;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String new_password) {
        password = new_password;
    }

    public String getPassword(){
        return password;
    }

    public void setSecurityQuestionOne(String newQuestionOne) {
        securityQuestionOne = newQuestionOne;
    }

    public String getSecurityQuestionOne() {
        return securityQuestionOne;
    }

    public void setSecurityQuestionTwo(String newQuestionTwo){
        securityQuestionTwo = newQuestionTwo;
    }

    public String getSecurityQuestionTwo() {
        return securityQuestionTwo;
    }

    public void setSecurityAnswerOne(String newAnswerOne) {
        securityAnswerOne = newAnswerOne;
    }

    public String getSecurityAnswerOne() {
        return securityAnswerOne;
    }

    public void setSecurityAnswerTwo(String newAnswerTwo) {
        securityAnswerTwo = newAnswerTwo;
    }

    public String getSecurityAnswerTwo() {
        return securityAnswerTwo;
    }

    public void setSavedCurrencies(ArrayList<String> newSavedCurrency) { savedCurrencies = newSavedCurrency; }

    public ArrayList<String> getSavedCurrencies() { return savedCurrencies; }

    //other member functions

    public void addSavedCurrency(String newCurrency) { //adds a new currency to the favorite list, and checks for duplicates

        //check to make sure that currency hasn't already been added to list
        for (int i = 0; i < savedCurrencies.size(); i++)
        {
            if (savedCurrencies.get(i).equals(newCurrency))
            {
                System.out.println("Error: currency type has already been added to saved list.");
                return;
            }
        }

        savedCurrencies.add(newCurrency);
    }

    public void removeSavedCurrency(String currencyToRemove) {  //removes specified saved currency, but does not allow "USD" to be removed
        if (currencyToRemove.equals("USD"))
        {
            System.out.println("Error: cannot delete default currency.");
        }
        else
            savedCurrencies.remove(currencyToRemove);
    }

}
