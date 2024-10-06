package com.rorschach.zcyislim.Pager.Home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rorschach.zcyislim.DatabaseLocal.BaseRoomDatabase;
import com.rorschach.zcyislim.DatabaseLocal.FoodTypeDao;
import com.rorschach.zcyislim.DatabaseLocal.InstanceDatabase;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.Entity.FoodType;
import com.rorschach.zcyislim.Entity.TaskAssignment;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.FoodRequest;
import com.rorschach.zcyislim.Net.TaskRequest;
import com.rorschach.zcyislim.Pager.FoodInfo.FoodInfoActivity;
import com.rorschach.zcyislim.Pager.FoodInfo.FoodRecommendAdapter;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.FragmentHomeBinding;
import com.scwang.smart.refresh.header.MaterialHeader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    Retrofit retrofit;
    TaskRequest taskRequest;
    FoodRequest foodRequest;
    User currentUser;
    ArrayList<FoodType> foodTypes;
    ArrayList<Food> foods;
    ArrayList<Food> searchResultFoods;
    ArrayList<TaskAssignment> taskAssignments;
    HomeFoodAdapter homeFoodAdapter;
    HomeTaskAdapter homeTaskAdapter;
    FoodRecommendAdapter foodRecommendAdapter;
    int currentTypePosition;
    BaseRoomDatabase baseRoomDatabase;
    FoodTypeDao foodTypeDao;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        if (homeViewModel.getRootView() == null) {
            View root = binding.getRoot();
            homeViewModel.setRootView(root);
            initMethod();
            initListener();

            return root;
        } else {
            return null;
        }
    }

    private void initListener() {
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            //initBanner();
            initFoodType();
            getFoodListByType(0);
            initTask();
            binding.refreshLayout.finishRefresh();
        });
        binding.refreshLayout.setEnableLoadMore(false);
        binding.chipGroupName.setSingleSelection(true);
        binding.catSearchView.inflateMenu(R.menu.serach_menu);
        binding.catSearchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    binding.searchAlert.setVisibility(View.GONE);
                    if (foodRecommendAdapter != null) {
                        foodRecommendAdapter.clearList();
                    }
                }
            }
        });
        binding.catSearchView.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                if (binding.catSearchView.getText().toString().length() >= 1) {
                    Call<ApiResult<ArrayList<Food>>> foodByName = foodRequest.getFoodByName(currentUser.getUserToken(), binding.catSearchView.getText().toString());
                    foodByName.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<ApiResult<ArrayList<Food>>> call, Response<ApiResult<ArrayList<Food>>> response) {
                            if (response.body() != null && response.body().success) {
                                if (response.body().data != null && response.body().data.size() > 0) {
                                    searchResultFoods = response.body().data;
                                    binding.searchAlert.setVisibility(View.GONE);
                                    binding.resultBox.setVisibility(View.VISIBLE);
                                    EventBus.getDefault().post(new Events<>(6));
                                } else {
                                    foodRecommendAdapter.clearList();
                                    binding.resultBox.setVisibility(View.GONE);
                                    binding.searchAlert.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.resultBox.setVisibility(View.GONE);
                                binding.searchAlert.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<ArrayList<Food>>> call, Throwable t) {
                            UtilMethod.showToast(getContext(), "网络出现错误，请稍后重试！");
                        }
                    });
                } else {
                    UtilMethod.showToast(getContext(), "输入不能为空");
                }

            }
            return false;
        });
        //initBanner();
        initFoodType();
        getFoodListByType(0);
        initTask();
    }

    private void initTask() {
        switch (currentUser.getUserGoal()) {
            case 0:
                binding.taskName.setText(getString(R.string.task_week, getString(R.string.goal_A)));
                binding.taskDietaryAdvice.setText(getString(R.string.task_dietary_advice_A));
                break;
            case 1:
                binding.taskName.setText(getString(R.string.task_week, getString(R.string.goal_B)));
                binding.taskDietaryAdvice.setText(getString(R.string.task_dietary_advice_B));
                break;
            case 2:
                binding.taskName.setText(getString(R.string.task_week, getString(R.string.goal_C)));
                binding.taskDietaryAdvice.setText(getString(R.string.task_dietary_advice_C));
                break;
            case 3:
                binding.taskName.setText(getString(R.string.task_week, getString(R.string.goal_D)));
                binding.taskDietaryAdvice.setText(getString(R.string.task_dietary_advice_D));
                break;
        }
        Call<ApiResult<ArrayList<TaskAssignment>>> getTask = taskRequest.getTask(currentUser.getUserToken(), currentUser.getUserGoal());
        getTask.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<ArrayList<TaskAssignment>>> call, Response<ApiResult<ArrayList<TaskAssignment>>> response) {
                if (response.body() != null && response.body().success) {
                    binding.taskBox.setVisibility(View.VISIBLE);
                    taskAssignments = response.body().data;
                    TaskAssignment task = taskAssignments.get(0);
                    String startDate = task.getStartDate().substring(0, 10);
                    String endDate = task.getEndDate().substring(0, 10);
                    binding.taskDuration.setText(getString(R.string.goal_duration, startDate, endDate));
                    if (homeTaskAdapter == null) {
                        homeTaskAdapter = new HomeTaskAdapter(taskAssignments, getContext(), position -> materialAlertDialogBuilder.setTitle("确定今日已完成当前任务？").setMessage("确认后不可取消")
                                .setPositiveButton("确定", (dialogInterface, i) -> {
                                    if (taskAssignments != null) {
                                        Call<ApiResult<TaskAssignment>> updateTask = taskRequest.updateTask(currentUser.getUserToken(), taskAssignments.get(position).getTaskAssignmentId());
                                        updateTask.enqueue(new Callback<>() {
                                            @Override
                                            public void onResponse(Call<ApiResult<TaskAssignment>> call1, Response<ApiResult<TaskAssignment>> response1) {

                                                if (response1.body() != null && response1.body().success) {
                                                    taskAssignments.set(position, response1.body().data);
                                                    homeTaskAdapter.updateProgress(taskAssignments, position);
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<ApiResult<TaskAssignment>> call1, Throwable t) {

                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show());
                    } else {
                        homeTaskAdapter.updateList(taskAssignments);
                        homeTaskAdapter.notifyDataSetChanged();
                    }
                    binding.taskRecyclerView.setAdapter(homeTaskAdapter);
                    binding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                }
            }

            @Override
            public void onFailure(Call<ApiResult<ArrayList<TaskAssignment>>> call, Throwable t) {
                UtilMethod.showToast(getContext(), "网络出现错误，请稍后重试！");
            }
        });
    }

    private void initMethod() {
        currentUser = UtilMethod.getObject(getContext(), "currentUser");
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            taskRequest = retrofit.create(TaskRequest.class);
            foodRequest = retrofit.create(FoodRequest.class);
        }
        baseRoomDatabase = InstanceDatabase.getInstance(getActivity());
        foodTypeDao = baseRoomDatabase.getFoodTypeDao();
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog);
        EventBus.getDefault().register(this);
    }

    //从网络获取所有食物分类
    private void initFoodType() {
        List<FoodType> localFoodTypes = foodTypeDao.selectAllFoodType();
        if (localFoodTypes != null && localFoodTypes.size() > 0) {

        } else {
            Call<ApiResult<ArrayList<FoodType>>> allTypeBySize = foodRequest.getAllTypeBySize(currentUser.getUserToken());
            allTypeBySize.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResult<ArrayList<FoodType>>> call, Response<ApiResult<ArrayList<FoodType>>> response) {
                    if (response.body() != null && response.body().success) {
                        if (response.body().data != null && response.body().data.size() > 0) {
                            foodTypes = response.body().data;
                            UtilMethod.setObject(getContext(), "AllFoodType", response.body().data);
                            EventBus.getDefault().post(new Events<>(2));
                        } else {
                            UtilMethod.showToast(getContext(), "未查询到相关内容~");
                        }
                    } else {
                        UtilMethod.showToast(getContext(), response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResult<ArrayList<FoodType>>> call, Throwable t) {
                    UtilMethod.showToast(getContext(), "网络出现错误，请稍后重试！");
                }
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initFoodType(Events<ArrayList<FoodType>> events) {
        if (events.getType() == 2) {
            if (foodTypes != null) {
                if (binding.chipGroupName.getCheckedChipIds().size() > 0) {
                    binding.chipGroupName.removeAllViews();
                }
                for (int index = 0; index < 6; index++) {
                    Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_chip, binding.chipGroupName, false);
                    if (index != 5) {
                        chip.setText(foodTypes.get(index).getFoodTypeName());
                        chip.setCheckable(true);
                    } else {
                        chip.setText("查看全部");
                        chip.setCheckable(false);
                        chip.setOnClickListener(view -> {
                            //跳转到全部页面
                            startActivity(new Intent(getActivity(), AllFoodActivity.class));
                        });
                    }
                    chip.setOnCheckedChangeListener((compoundButton, b) -> {
                        Chip temp = (Chip) compoundButton;
                        if (b) {
                            int indexInFoodTypeList = getIndexInFoodTypeList(temp.getText().toString());
                            getFoodListByType(indexInFoodTypeList);
                            temp.setChipBackgroundColorResource(R.color.hintBack);
                            compoundButton.setTextColor(getResources().getColor(R.color.white));
                            currentTypePosition = indexInFoodTypeList;
                        } else {
                            temp.setChipBackgroundColorResource(R.color.white);
                            compoundButton.setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    binding.chipGroupName.addView(chip);

                    if (index == 0) {
                        chip.setChecked(true);
                        currentTypePosition = 0;
                    }
                }
            }

        } else if (events.getType() == 3) {
            if (homeFoodAdapter == null) {
                homeFoodAdapter = new HomeFoodAdapter(foods, getContext(), position -> {
                    startActivity(new Intent(getActivity(), FoodInfoActivity.class));
                    EventBus.getDefault().postSticky(new Events<>(4, foods.get(position)));
                });
                binding.foodRecyclerView.setAdapter(homeFoodAdapter);
                binding.foodRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }
            homeFoodAdapter.setList(foods);

        } else if (events.getType() == 6) {
            foodRecommendAdapter = new FoodRecommendAdapter(searchResultFoods, getContext(), position -> {
                EventBus.getDefault().postSticky(new Events<>(4, searchResultFoods.get(position)));
                startActivity(new Intent(getActivity(), FoodInfoActivity.class));
            });
            binding.searchResult.setAdapter(foodRecommendAdapter);
            binding.searchResult.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    private void getFoodListByType(int type) {
        Call<ApiResult<ArrayList<Food>>> foodByType = foodRequest.getFoodByType(currentUser.getUserToken(), type, 0);
        foodByType.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<ArrayList<Food>>> call, Response<ApiResult<ArrayList<Food>>> response) {
                if (response.body() != null && response.body().success) {
                    if (response.body().getData() != null && response.body().data.size() > 0) {
                        foods = response.body().data;
                        EventBus.getDefault().post(new Events<>(3));
                    } else {
                        UtilMethod.showToast(getContext(), "服务器有误！请稍后重试！");
                    }
                } else {
                    UtilMethod.showToast(getContext(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResult<ArrayList<Food>>> call, Throwable t) {
                UtilMethod.showToast(getContext(), "网络出现错误，请稍后重试！");
            }
        });
    }

    private int getIndexInFoodTypeList(String key) {
        for (int i = 0; i < foodTypes.size(); i++) {
            if (foodTypes.get(i).getFoodTypeName().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override

    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}