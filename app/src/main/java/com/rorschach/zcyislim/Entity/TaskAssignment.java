package com.rorschach.zcyislim.Entity;

public class TaskAssignment {
    public int taskAssignmentId;
    public int userId;
    public int taskId;
    public String taskName;
    public String taskExample;
    public int taskTime;
    public String startDate;
    public String endDate;
    public int expectedCompletionCount;
    public int actualCompletionCount;
    public String taskDuration;
    public int taskGoalType;
    public String assignmentUpdateTime;

    @Override
    public String toString() {
        return "TaskAssignment{" +
                "taskAssignmentId=" + taskAssignmentId +
                ", userId=" + userId +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskExample='" + taskExample + '\'' +
                ", taskTime=" + taskTime +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", expectedCompletionCount=" + expectedCompletionCount +
                ", actualCompletionCount=" + actualCompletionCount +
                ", taskDuration='" + taskDuration + '\'' +
                ", taskGoalType=" + taskGoalType +
                ", assignmentUpdateTime='" + assignmentUpdateTime + '\'' +
                '}';
    }

    public int getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(int taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskExample() {
        return taskExample;
    }

    public void setTaskExample(String taskExample) {
        this.taskExample = taskExample;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskAssignmentId() {
        return taskAssignmentId;
    }

    public void setTaskAssignmentId(int taskAssignmentId) {
        this.taskAssignmentId = taskAssignmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getExpectedCompletionCount() {
        return expectedCompletionCount;
    }

    public void setExpectedCompletionCount(int expectedCompletionCount) {
        this.expectedCompletionCount = expectedCompletionCount;
    }

    public int getActualCompletionCount() {
        return actualCompletionCount;
    }

    public void setActualCompletionCount(int actualCompletionCount) {
        this.actualCompletionCount = actualCompletionCount;
    }

    public String getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(String taskDuration) {
        this.taskDuration = taskDuration;
    }

    public int getTaskGoalType() {
        return taskGoalType;
    }

    public void setTaskGoalType(int taskGoalType) {
        this.taskGoalType = taskGoalType;
    }

    public String getAssignmentUpdateTime() {
        return assignmentUpdateTime;
    }

    public void setAssignmentUpdateTime(String assignmentUpdateTime) {
        this.assignmentUpdateTime = assignmentUpdateTime;
    }
}

