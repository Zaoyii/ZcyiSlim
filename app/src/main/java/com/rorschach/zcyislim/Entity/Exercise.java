package com.rorschach.zcyislim.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_exercise")
public class Exercise implements Serializable, Comparable<Exercise> {
    private static final long serialVersionUID = 9269L;
    @PrimaryKey()
    @ColumnInfo(name = "exercise_id")
    long exerciseId;
    @ColumnInfo(name = "exercise_user_id")
    long userId;
    @ColumnInfo(name = "exercise_type")
    int exerciseType;
    @ColumnInfo(name = "exercise_data")
    String exerciseData;
    @ColumnInfo(name = "exercise_distance")
    Double exerciseDistance;
    @ColumnInfo(name = "exercise_speed")
    Double exerciseSpeed;
    @ColumnInfo(name = "exercise_calories")
    Double exerciseCalories;
    @ColumnInfo(name = "exercise_start_time")
    String startTime;
    @ColumnInfo(name = "exercise_end_time")
    String endTime;
    @ColumnInfo(name = "exercise_create_time")
    String exerciseCreateTime;

    public Exercise() {
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId=" + exerciseId +
                ", userId=" + userId +
                ", exerciseType=" + exerciseType +
                ", exerciseDistance=" + exerciseDistance +
                ", exerciseSpeed=" + exerciseSpeed +
                ", exerciseCalories=" + exerciseCalories +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", exerciseCreateTime='" + exerciseCreateTime + '\'' +
                '}';
    }

    @Ignore
    public Exercise(long userId, int exerciseType, String exerciseData, Double exerciseDistance, Double exerciseSpeed, Double exerciseCalories, String startTime, String endTime) {
        this.userId = userId;
        this.exerciseType = exerciseType;
        this.exerciseData = exerciseData;
        this.exerciseDistance = exerciseDistance;
        this.exerciseSpeed = exerciseSpeed;
        this.exerciseCalories = exerciseCalories;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public Exercise(int exerciseType, Double exerciseDistance, Double exerciseCalories, String startTime) {
        this.exerciseType = exerciseType;
        this.exerciseDistance = exerciseDistance;
        this.exerciseCalories = exerciseCalories;
        this.startTime = startTime;
    }
    @Ignore
    public Exercise(Double exerciseDistance, String startTime) {
        this.exerciseDistance = exerciseDistance;
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Exercise other) {
        return this.getStartTime().compareTo(other.getStartTime());
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getExerciseData() {
        return exerciseData;
    }

    public void setExerciseData(String exerciseData) {
        this.exerciseData = exerciseData;
    }

    public String getExerciseCreateTime() {
        return exerciseCreateTime;
    }

    public void setExerciseCreateTime(String exerciseCreateTime) {
        this.exerciseCreateTime = exerciseCreateTime;
    }

    public Double getExerciseDistance() {
        return exerciseDistance;
    }

    public void setExerciseDistance(Double exerciseDistance) {
        this.exerciseDistance = exerciseDistance;
    }

    public Double getExerciseSpeed() {
        return exerciseSpeed;
    }

    public void setExerciseSpeed(Double exerciseSpeed) {
        this.exerciseSpeed = exerciseSpeed;
    }

    public Double getExerciseCalories() {
        return exerciseCalories;
    }

    public void setExerciseCalories(Double exerciseCalories) {
        this.exerciseCalories = exerciseCalories;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
