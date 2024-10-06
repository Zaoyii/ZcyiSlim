package com.rorschach.zcyislim.Pager.FoodInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.Entity.FoodType;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.FoodRequest;
import com.rorschach.zcyislim.Pager.Home.AllFoodActivity;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityFoodInfoBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodInfoActivity extends BaseActivity {
    ActivityFoodInfoBinding binding;
    Retrofit retrofit;
    FoodRequest foodRequest;
    User currentUser;

    ArrayList<Food> recommendFoods;
    FoodRecommendAdapter foodRecommendAdapter;
    long currentFoodId = -1;
    long currentFoodTypeId = -1;
    Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        initListener();

    }

    private void initListener() {
        binding.backButton.setOnClickListener(view -> finish());
        binding.foodType.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(13, (int) currentFoodTypeId));
            startActivity(new Intent(FoodInfoActivity.this, AllFoodActivity.class));
        });
    }

    private void initMethod() {
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
        foodRequest = retrofit.create(FoodRequest.class);
        currentUser = UtilMethod.getObject(getApplicationContext(), "currentUser");
        EventBus.getDefault().register(this);
    }

    public void setRecommendFoodInfo() {
        Call<ApiResult<ArrayList<Food>>> foodByRandom = foodRequest.getFoodByRandom(currentUser.getUserToken(), food.getFoodTypeId());
        foodByRandom.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<ArrayList<Food>>> call, Response<ApiResult<ArrayList<Food>>> response) {
                if (response.body() != null && response.body().success) {
                    if (response.body().data != null && response.body().data.size() > 0) {
                        recommendFoods = response.body().data;
                        System.out.println(recommendFoods + "---=-=-=-=-=-=-=");
                        EventBus.getDefault().post(new Events<>(5));
                    } else {
                        UtilMethod.showToast(getApplicationContext(), "服务器有误！请稍后重试！");
                    }
                } else {
                    UtilMethod.showToast(getApplicationContext(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResult<ArrayList<Food>>> call, Throwable t) {

            }
        });
    }

    public void setFoodInfo(Food food, String foodType) {
        binding.foodName.setText(food.getFoodName());
        binding.foodType.setText(foodType);
        binding.foodCalories.setText(food.getFoodCalories());
        binding.foodFat.setText(food.getFoodFat());
        binding.foodProtein.setText(food.getFoodProtein());
        binding.foodCarbohydrate.setText(food.getFoodCarbohydrate());
        Glide.with(getApplicationContext()).load(Constant.foodUrl + food.getFoodTypeId() + "/" + food.getFoodImage()).centerCrop().into(binding.foodImage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setFoodInfo(Events<Food> events) {
        if (events.getType() == 4 && currentFoodId == -1) {
            if (events.getData() != null) {
                setFoodInfo(events.getData(), getFoodTypeNameById(events.getData().getFoodTypeId()));
                food = events.getData();
                setRecommendFoodInfo();
                currentFoodId = events.getData().getFoodId();
                currentFoodTypeId = events.getData().getFoodTypeId();
            }
        }
    }

    public String getFoodTypeNameById(long foodTypeId) {
        ArrayList<FoodType> allFoodType = UtilMethod.getObject(getApplicationContext(), "AllFoodType");
        for (int i = 0; i < Objects.requireNonNull(allFoodType).size(); i++) {
            if (foodTypeId == allFoodType.get(i).getFoodTypeId()) {
                return allFoodType.get(i).getFoodTypeName();
            }
        }
        return "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setRecommend(Events<Map<String, Object>> events) {
        if (events.getType() == 5) {
            foodRecommendAdapter = new FoodRecommendAdapter(recommendFoods, this, position -> {
                EventBus.getDefault().postSticky(new Events<>(4, recommendFoods.get(position)));
                startActivity(new Intent(this, FoodInfoActivity.class));
            });
            binding.rcRecommend.setAdapter(foodRecommendAdapter);
            binding.rcRecommend.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}