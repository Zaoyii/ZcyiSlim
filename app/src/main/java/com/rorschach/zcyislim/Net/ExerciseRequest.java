package com.rorschach.zcyislim.Net;

import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExerciseRequest {

    @POST(value = "exercise/addExercise")
    Call<ApiResult<String>> addExercise(@Header("Authorization") String Authorization, @Body Exercise exercise);

    @POST(value = "exercise/addExercises")
    Call<ApiResult<String>> addExercises(@Header("Authorization") String Authorization, @Body List<Exercise> exercises);

    @GET(value = "exercise/getExerciseByUserId")
    Call<ApiResult<ArrayList<Exercise>>> getExerciseByUserId(@Header("Authorization") String Authorization, @Query("userId") int userId);

    @GET(value = "exercise/getEndExerciseDataByUserId")
    Call<ApiResult<HashMap<String, Double>>> getEndExerciseDataByUserId(@Header("Authorization") String Authorization, @Query("userId") int userId);

}
