package com.rorschach.zcyislim.Net;

import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Entity.Weight;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserRequest {
    @Multipart
    @POST(value = "user/updateAvatar")
    Call<ApiResult<String>> updateAvatar(@Header("Authorization") String Authorization, @Part MultipartBody.Part avatar);

    @POST(value = "user/updateEmail")
    Call<ApiResult<String>> updateEmail(@Header("Authorization") String Authorization, @Query("email") String email, @Query("code") String code);


    @POST(value = "user/sendEmail")
    Call<ApiResult<String>> sendEmail(@Query("email") String email, @Query("type") int type);

    @POST(value = "user/verifyEmail")
    Call<ApiResult<String>> verifyEmail(@Query("email") String email, @Query("code") String code, @Query("type") int type);

    @GET(value = "user/loadMyself")
    Call<ApiResult<User>> loadMyself(@Header("Authorization") String Authorization);

    @GET(value = "user/loadBanner")
    Call<ApiResult<Map<String, String>>> loadBanner(@Header("Authorization") String Authorization);

    @FormUrlEncoded
    @POST(value = "user/login")
    Call<ApiResult<User>> login(@Field("userName") String userName, @Field("userPassword") String userPassword);

    @FormUrlEncoded
    @POST(value = "user/register")
    Call<ApiResult<String>> register(@Field("username") String username,
                                     @Field("password") String password,
                                     @Field("email") String email,
                                     @Field("code") String code);

    @FormUrlEncoded
    @POST(value = "user/initUserData")
    Call<ApiResult<String>> initUserData(@Header("Authorization") String Authorization,
                                         @Field("nickname") String nickname,
                                         @Field("gender") int gender,
                                         @Field("age") int age,
                                         @Field("weight") int weight,
                                         @Field("height") int height,
                                         @Field("goal") int goal);

    @GET(value = "user/getWeightByUserId")
    Call<ApiResult<ArrayList<Weight>>> getWeightByUserId(@Header("Authorization") String Authorization, @Query("userId") long userId);

    @POST(value = "user/updateWeight")
    Call<ApiResult<String>> updateWeight(@Header("Authorization") String Authorization, @Body Weight weight);

    @POST(value = "user/updateHeight")
    Call<ApiResult<String>> updateHeight(@Header("Authorization") String Authorization, @Query("height") Double height, @Query("userId") long userId);

    @POST(value = "user/updateNickname")
    Call<ApiResult<String>> updateNickname(@Header("Authorization") String Authorization, @Query("nickname") String nickname, @Query("userId") long userId);

}
