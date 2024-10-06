package com.rorschach.zcyislim.Net;

import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.TaskAssignment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TaskRequest {
    @POST(value = "task/getTask")
    Call<ApiResult<ArrayList<TaskAssignment>>> getTask(@Header("Authorization") String Authorization, @Query("goalType") int goalType);

    @POST(value = "task/updateTask")
    Call<ApiResult<TaskAssignment>> updateTask(@Header("Authorization") String Authorization, @Query("taskAssignmentId") int taskAssignmentId);

}
