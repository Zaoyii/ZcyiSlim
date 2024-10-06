package com.rorschach.zcyislim.Pager.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.Entity.FoodType;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.FoodRequest;
import com.rorschach.zcyislim.Pager.FoodInfo.FoodInfoActivity;
import com.rorschach.zcyislim.Pager.FoodInfo.FoodRecommendAdapter;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityAllFoodBinding;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllFoodActivity extends BaseActivity {

    ActivityAllFoodBinding binding;
    Retrofit retrofit;
    User currentUser;
    FoodRequest foodRequest;
    ArrayList<FoodType> foodTypes;
    ArrayList<Food> foods;
    FoodTypeAdapter typeAdapter;
    FoodRecommendAdapter recommendAdapter;
    public static int page;
    public static int foodType = -1;
    public static int foodTypeStick = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
    }

    private void initMethod() {
        UtilMethod.changeStatusBarFrontColor(true, this, android.R.color.white);
        currentUser = UtilMethod.getObject(this, "currentUser");
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            foodRequest = retrofit.create(FoodRequest.class);
        }
        page = 0;
        EventBus.getDefault().register(this);
        initListener();
        initFoodType();
    }

    private void initListener() {
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate));
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(this));
        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            loadMoreData();
        });
        binding.backButton.setOnClickListener(view -> finish());
    }

    private void loadMoreData() {
        getFoodListByType(foodType);
    }

    private void getFoodListByType(int type) {
        Call<ApiResult<ArrayList<Food>>> foodByType = foodRequest.getFoodByType(currentUser.getUserToken(), type, page);
        foodByType.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<ArrayList<Food>>> call, Response<ApiResult<ArrayList<Food>>> response) {
                if (response.body() != null && response.body().success) {
                    if (response.body().getData() != null && response.body().data.size() > 0) {
                        recommendAdapter.loadMoreData(response.body().data);
                        page++;
                    } else {
                        UtilMethod.showToast(AllFoodActivity.this, response.body().message);
                    }
                } else {
                    UtilMethod.showToast(AllFoodActivity.this, getApplication().getResources().getString(R.string.network_wrong));
                }
                binding.refreshLayout.finishLoadMore();
            }

            @Override
            public void onFailure(Call<ApiResult<ArrayList<Food>>> call, Throwable t) {
                UtilMethod.showToast(AllFoodActivity.this, t.toString());
            }
        });
    }

    private void initFoodType() {
        foodTypes = UtilMethod.getObject(this, "AllFoodType");
        if (foodTypes != null && foodTypes.size() > 0) {
            if (foodTypeStick != -1) {
                foodType = foodTypeStick;
            } else {
                foodType = (int) foodTypes.get(0).getFoodTypeId();
            }
            setFoodType();
            setFood();
            loadMoreData();
        } else {
            Call<ApiResult<ArrayList<FoodType>>> allTypeBySize = foodRequest.getAllTypeBySize(currentUser.getUserToken());
            allTypeBySize.enqueue(new Callback<>() {
                @Override
                public void onResponse
                        (Call<ApiResult<ArrayList<FoodType>>> call, Response<ApiResult<ArrayList<FoodType>>> response) {
                    if (response.body() != null && response.body().success) {
                        if (response.body().data != null && response.body().data.size() > 0) {
                            foodTypes = response.body().data;
                            setFoodType();
                            loadMoreData();
                        } else {
                            UtilMethod.showToast(AllFoodActivity.this, response.body().message);
                        }
                    } else {
                        UtilMethod.showToast(AllFoodActivity.this, getApplication().getResources().getString(R.string.network_wrong));
                    }
                }

                @Override
                public void onFailure(Call<ApiResult<ArrayList<FoodType>>> call, Throwable t) {
                    System.out.println(t);
                }
            });
        }

    }

    private void setFoodType() {
        typeAdapter = new FoodTypeAdapter(foodTypes, this, position -> {
            page = 0;
            foodType = (int) foodTypes.get(position).getFoodTypeId();
            recommendAdapter.clearList();
            typeAdapter.setSelectedIndex(position);
            getFoodListByType(foodType);
        });
        typeAdapter.setSelectedIndex(getIndexInFoodTypeList(foodType));
        binding.foodTypeRe.setAdapter(typeAdapter);
        binding.foodTypeRe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.foodTypeRe.scrollToPosition(getIndexInFoodTypeList(foodType));
    }

    private void setFood() {
        if (foods == null) {
            foods = new ArrayList<>();
        }
        recommendAdapter = new FoodRecommendAdapter(foods, this, position -> {
            startActivity(new Intent(AllFoodActivity.this, FoodInfoActivity.class));
            EventBus.getDefault().postSticky(new Events<>(4, foods.get(position)));
        });
        binding.foodRe.setAdapter(recommendAdapter);
        binding.foodRe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setTypeChecked(Events<Integer> events) {
        if (events.getType() == 13 && foodTypeStick != events.getData()) {
            foodTypeStick = events.getData();
        }
    }

    private int getIndexInFoodTypeList(int foodTypeId) {
        for (int i = 0; i < foodTypes.size(); i++) {
            if (foodTypes.get(i).getFoodTypeId() == foodTypeId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}