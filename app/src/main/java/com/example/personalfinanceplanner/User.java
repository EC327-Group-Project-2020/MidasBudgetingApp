package com.example.personalfinanceplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class User {

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

    @ColumnInfo(name = "alternate_currency")
    private String alternateCurrency;

    //constructor
    public User(String username, String password, String securityQuestionOne, String securityQuestionTwo, String securityAnswerOne, String securityAnswerTwo, String alternateCurrency)
    {
        this.username = username;
        this.password = password;
        this.securityQuestionOne = securityQuestionOne;
        this.securityQuestionTwo = securityQuestionTwo;
        this.securityAnswerOne = securityAnswerOne;
        this.securityAnswerTwo = securityAnswerTwo;
        this.alternateCurrency = alternateCurrency;
    }

    /*NOTE: getter and setter names below should NOT be altered. In order to keep
    the data fields private in the user class and still allow Room to access them, the getters and
    setters must obey JavaBeans conventions for naming getters and setters for each data field, which
    is reflected in the names chosen for those member functions
     */

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

    public void setAlternateCurrency(String newAlternateCurrency) { alternateCurrency = newAlternateCurrency; }

    public String getAlternateCurrency() { return alternateCurrency; }
}
