package com.example.happyrun.model;

public class User {
    private int userId;
    private String userName;
    private String userGender;
    private String userMajor;
    private String userPassword;
    private String userAvatar;
    private float userHeight;
    private float userWeight;
    private String userSchool;
    private String semester;

    public User(int userId, String userName, String userGender, String userMajor, String userPassword, String userAvatar, float userHeight, float userWeight, String userSchool, String semester) {
        this.userId = userId;
        this.userName = userName;
        this.userGender = userGender;
        this.userMajor = userMajor;
        this.userPassword = userPassword;
        this.userAvatar = userAvatar;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.userSchool = userSchool;
        this.semester = semester;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public void setUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setUserHeight(float userHeight) {
        this.userHeight = userHeight;
    }

    public void setUserWeight(float userWeight) {
        this.userWeight = userWeight;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserMajor() {
        return userMajor;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public float getUserHeight() {
        return userHeight;
    }

    public float getUserWeight() {
        return userWeight;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public String getSemester() {
        return semester;
    }
}
