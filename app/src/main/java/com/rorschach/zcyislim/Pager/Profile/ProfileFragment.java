package com.rorschach.zcyislim.Pager.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Entity.Weight;
import com.rorschach.zcyislim.Net.ExerciseRequest;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.Pager.Start.LoginActivity;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.FragmentProfileBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    Retrofit retrofit;
    UserRequest userRequest;
    ExerciseRequest exerciseRequest;
    User currentUser;
    ArrayList<Weight> weights;
    List<Entry> listBar;
    List<String> listIndex;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        initMethod();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            loadMyself();
        });
        binding.userInfoBox.setOnClickListener(view -> startActivity(new Intent(getActivity(), UserInfoActivity.class)));
        binding.userAvatar.setOnClickListener(view -> bigImageLoader());
        binding.updateWeightButton.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(14, 2));
            startActivity(new Intent(getActivity(), UpdateUserInfoActivity.class));
        });

        binding.logoutButton.setOnClickListener(view -> new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("确定要退出登录吗？")
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    editor.putBoolean("isLogin", false);
                    editor.putLong("currentUserId", -1);
                    editor.apply();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                })
                .setNegativeButton("取消", null)
                .show());
        loadMyself();
        setUserInfo();
        setExerciseData();
        setWeightData();
    }

    private void initMethod() {
        currentUser = UtilMethod.getObject(getContext(), "currentUser");
        binding.userAvatar.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(ShapeAppearanceModel.PILL).build());
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
            exerciseRequest = retrofit.create(ExerciseRequest.class);
        }
        sharedPreferences = UtilMethod.getPreferences(getActivity().getApplication());
        editor = sharedPreferences.edit();
    }

    private void setUserInfo() {
        String userAvatar;
        if (currentUser.getUserAvatar().equals("defaultAvatar.jpg")) {
            userAvatar = Constant.userAvatar + currentUser.getUserAvatar();
        } else {
            userAvatar = Constant.userAvatar + currentUser.getUserId() + "/" + currentUser.getUserAvatar();
        }
        Glide.with(this).load(userAvatar).into(binding.userAvatar);
        binding.userNickName.setText(currentUser.getUserNickName());
        binding.userWeight.setText(getResources().getString(R.string.user_weight_str, currentUser.getUserWeight().toString()));
        binding.userHeight.setText(getResources().getString(R.string.user_height_str, currentUser.getUserHeight().toString()));
    }

    private void setExerciseData() {
        Call<ApiResult<HashMap<String, Double>>> endExerciseDataByUserId = exerciseRequest.getEndExerciseDataByUserId(currentUser.getUserToken(), (int) currentUser.getUserId());
        endExerciseDataByUserId.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<HashMap<String, Double>>> call, Response<ApiResult<HashMap<String, Double>>> response) {
                if (response.body() != null && response.body().success) {
                    Double yearDistance = response.body().data.get("yearDistance");
                    Double monthDistance = response.body().data.get("monthDistance");
                    Double yearCalories = response.body().data.get("yearCalories");
                    Double monthCalories = response.body().data.get("monthCalories");
                    if (yearDistance != null && monthDistance != null && yearCalories != null && monthCalories != null) {
                        String strYearDistance = String.format(Locale.CHINA, "%.2f", yearDistance * 0.001);
                        String strMonthDistance = String.format(Locale.CHINA, "%.2f", monthDistance * 0.001);
                        String strYearCalories = String.format(Locale.CHINA, "%.0f", yearCalories);
                        String strMonthCalories = String.format(Locale.CHINA, "%.0f", monthCalories);
                        binding.yearDistance.setText(strYearDistance);
                        binding.monthDistance.setText(strMonthDistance);
                        binding.yearCalories.setText(strYearCalories);
                        binding.monthCalories.setText(strMonthCalories);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult<HashMap<String, Double>>> call, Throwable t) {

            }
        });
    }

    private void loadMyself() {
        Call<ApiResult<User>> loadMyself = userRequest.loadMyself(currentUser.getUserToken());
        loadMyself.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<User>> call, Response<ApiResult<User>> response) {
                if (response.body() != null && response.body().success) {
                    String userToken = currentUser.getUserToken();
                    User data = response.body().data;
                    data.setUserToken(userToken);
                    UtilMethod.setObject(getContext(), "currentUser", data);
                    setUserInfo();
                    setExerciseData();
                    setWeightData();
                    binding.refreshLayout.finishRefresh();
                } else {
                    UtilMethod.showToast(getContext(), "服务器有误，请稍后重试~");
                }
                binding.refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ApiResult<User>> call, Throwable t) {

            }
        });
    }

    private void setWeightData() {
        Call<ApiResult<ArrayList<Weight>>> weightByUserId = userRequest.getWeightByUserId(currentUser.getUserToken(), currentUser.getUserId());
        weightByUserId.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<ArrayList<Weight>>> call, Response<ApiResult<ArrayList<Weight>>> response) {
                if (response.body() != null && response.body().success) {
                    if (response.body().data.size() > 0) {
                        weights = response.body().data;
                        initChart();
                        return;
                    }
                }
                binding.weightCard.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ApiResult<ArrayList<Weight>>> call, Throwable t) {

            }
        });
    }

    private void initChart() {
        if (binding.weightCard.getVisibility() == View.GONE) {
            binding.weightCard.setVisibility(View.VISIBLE);
        }
        binding.weightChart.clear();
        if (listBar == null) {
            listBar = new ArrayList<>();
        } else {
            listBar.clear();
        }
        if (listIndex == null) {
            listIndex = new ArrayList<>();
        } else {
            listIndex.clear();
        }
        for (int i = 0; i < weights.size(); i++) {
            listBar.add(new BarEntry(i, (float) weights.get(i).getWeightData()));
            listIndex.add(weights.get(i).getWeightCreateTime().substring(5));
        }

        LineDataSet lineDataSet = new LineDataSet(listBar, "体重(kg)");
        lineDataSet.setValueTextSize(10f);
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(R.color.black);
        lineDataSet.setCircleColor(R.color.black);

        binding.weightChart.setHighlightPerDragEnabled(false);
        binding.weightChart.setHighlightPerTapEnabled(false);
        //y轴
        binding.weightChart.getAxisRight().setEnabled(false);
        binding.weightChart.getAxisLeft().setEnabled(false);
        //x轴
        binding.weightChart.clear();
        binding.weightChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(listIndex));
        binding.weightChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.weightChart.getXAxis().setGranularity(1);
        binding.weightChart.setTouchEnabled(false);
        binding.weightChart.setData(data);
        Description description = new Description();
        description.setText("");
        binding.weightChart.setDescription(description);
        binding.weightChart.notifyDataSetChanged();
        binding.weightChart.invalidate();
    }

    private void bigImageLoader() {
        String userAvatar;
        if (currentUser.getUserAvatar().equals("defaultAvatar.jpg")) {
            userAvatar = Constant.userAvatar + currentUser.getUserAvatar();
        } else {
            userAvatar = Constant.userAvatar + currentUser.getUserId() + "/" + currentUser.getUserAvatar();
        }
        final Dialog dialog = new Dialog(getActivity());

        ImageView image = new ImageView(getContext());
        Glide.with(this).load(userAvatar).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                image.setImageDrawable(resource);
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // Not needed
            }
        });
        dialog.setContentView(image);
        //将dialog周围的白块设置为透明
        dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
        //显示
        dialog.show();
        //点击图片取消
        image.setOnClickListener(v -> dialog.cancel());
    }

    @Override
    public void onResume() {
        currentUser = UtilMethod.getObject(getContext(), "currentUser");
        setUserInfo();
        setWeightData();
        super.onResume();
    }
}