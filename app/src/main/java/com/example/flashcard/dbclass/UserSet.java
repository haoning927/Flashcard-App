package com.example.flashcard.dbclass;

public class UserSet {
    private int userID;
    private String userName;
    private String userEmail;
    private String userPassword;

    public UserSet(int userID,String userName,String userEmail, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
