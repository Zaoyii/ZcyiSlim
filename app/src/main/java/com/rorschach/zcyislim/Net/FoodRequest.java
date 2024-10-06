package com.rorschach.zcyislim.Net;

import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.Entity.FoodType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface FoodRequest {
    @GET(value = "food/getAllFoodType")
    Call<ApiResult<ArrayList<FoodType>>> getAllTypeBySize(@Header("Authorization") String Authorization);

    @GET(value = "food/getFoodByType")
    Call<ApiResult<ArrayList<Food>>> getFoodByType(@Header("Authorization") String Authorization, @Query("foodType") int type, @Query("page") int page);

    @GET(value = "food/getFoodByRandom")
    Call<ApiResult<ArrayList<Food>>> getFoodByRandom(@Header("Authorization") String Authorization, @Query("foodType") long foodType);

    @GET(value = "food/getFoodByName")
    Call<ApiResult<ArrayList<Food>>> getFoodByName(@Header("Authorization") String Authorization, @Query("foodName") String foodName);

}
