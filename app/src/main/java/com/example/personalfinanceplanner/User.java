package com.example.personalfinanceplanner;

public class User {

    //data fields
    private String username;
    private String password;
    private String securityQuestionOne;
    private String securityQuestionTwo;
    private String securityAnswerOne;
    private String securityAnswerTwo;

    //constructor
    public User(String username, String password, String securityQuestionOne, String securityQuestionTwo, String securityAnswerOne, String securityAnswerTwo) {
        this.username = username;
        this.password = password;
        this.securityQuestionOne = securityQuestionOne;
        this.securityQuestionTwo = securityQuestionTwo;
        this.securityAnswerOne = securityAnswerOne;
        this.securityAnswerTwo = securityAnswerTwo;
    }

    //member functions

    public void setUsername(String new_name) {
        username = new_name;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String new_password) {
        password = new_password;
    }

    //no getPassword function because no retrievals should be allowed; passwords can only be reset

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
}
