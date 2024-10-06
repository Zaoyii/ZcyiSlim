package com.rorschach.zcyislim.Entity;


import java.io.Serializable;

/**
 * @author ZaoYi
 */

public class User implements Serializable {
    private static final long serialVersionUID = 9273L;
    private long userId;
    private String userName;
    private String userNickName;
    private String userPassword;
    private int userAge;
    private int userGender;
    private int userGoal;
    private Double userHeight;
    private Double userWeight;
    private String userAvatar;
    private String userEmail;
    private String userCreateTime;
    private String userUpdateTime;
    private String userToken;

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }

    public int getUserGoal() {
        return userGoal;
    }

    public void setUserGoal(int userGoal) {
        this.userGoal = userGoal;
    }

    public Double getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(Double userHeight) {
        this.userHeight = userHeight;
    }

    public Double getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(Double userWeight) {
        this.userWeight = userWeight;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(String userCreateTime) {
        this.userCreateTime = userCreateTime;
    }


    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserUpdateTime() {
        return userUpdateTime;
    }

    public void setUserUpdateTime(String userUpdateTime) {
        this.userUpdateTime = userUpdateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userNickName='" + userNickName + '\'' +
                ", userAge=" + userAge +
                ", userGender=" + userGender +
                ", userGoal=" + userGoal +
                ", userHeight=" + userHeight +
                ", userWeight=" + userWeight +
                ", userAvatar='" + userAvatar + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
