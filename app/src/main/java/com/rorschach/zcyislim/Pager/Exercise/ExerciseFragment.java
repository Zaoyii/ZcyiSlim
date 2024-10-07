package com.rorschach.zcyislim.Pager.Exercise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.ExerciseRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.FragmentExerciseBinding;
import com.scwang.smart.refresh.header.MaterialHeader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExerciseFragment extends Fragment {
    FragmentExerciseBinding binding;
    User currentUser;
    ArrayList<Exercise> localList;
    Retrofit retrofit;
    ExerciseRequest exerciseRequest;
    ArrayList<Exercise> exercises;
    List<BarEntry> listBar;
    String currentMMdd;
    SharedPreferences sp;
    ExerciseAdapter exerciseAdapter;
    boolean isExpansion = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        initMethod();
        return binding.getRoot();
    }

    private void initMethod() {
        EventBus.getDefault().register(this);
        sp = getActivity().getSharedPreferences(UtilMethod.TAG, Context.MODE_PRIVATE);
        currentMMdd = new SimpleDateFormat("MM-dd").format(new Date()).substring(1);
        binding.lastSevenDayData.setScaleEnabled(false);
        Description description = binding.lastSevenDayData.getDescription();
        description.setEnabled(false);
        currentUser = UtilMethod.getObject(getActivity(), "currentUser");
        localList = UtilMethod.loadArrExerciseData(getActivity(), "ExerciseData");
        binding.boxRunning.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(11, 0));
            startActivity(new Intent(getActivity(), ExerciseActivity.class));
        });
        binding.boxWalking.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(11, 1));
            startActivity(new Intent(getActivity(), ExerciseActivity.class));
        });
        binding.boxRiding.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(11, 2));
            startActivity(new Intent(getActivity(), ExerciseActivity.class));
        });

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            exerciseRequest = retrofit.create(ExerciseRequest.class);
        }
        getExerciseHistory();
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            getExerciseHistory();
            binding.refreshLayout.finishRefresh();
        });
        binding.refreshLayout.setEnableLoadMore(false);
        binding.expansion.setOnClickListener(view -> {
            isExpansion = !isExpansion;
            if (exerciseAdapter != null) {
                if (isExpansion) {
                    binding.expansionImg.setImageResource(R.drawable.fold);
                    exerciseAdapter.setList(exercises);
                } else {
                    binding.expansionImg.setImageResource(R.drawable.unfold);
                    exerciseAdapter.setList(new ArrayList<>(exercises.subList(0, 5)));
                }
            }
        });
    }

    private void getExerciseHistory() {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        if (exercises.size() > 0) {
            exercises.clear();
        }
        if (!sp.getBoolean("hasUnLoadExerciseData", false)) {
            Call<ApiResult<ArrayList<Exercise>>> getExerciseByUserId = exerciseRequest.getExerciseByUserId(currentUser.getUserToken(), (int) currentUser.getUserId());
            getExerciseByUserId.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResult<ArrayList<Exercise>>> call, Response<ApiResult<ArrayList<Exercise>>> response) {
                    if (response.body() != null && response.body().success) {
                        if (response.body().data.size() > 0) {
                            exercises = response.body().data;
                            localList = exercises;
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putInt("exerciseNetCount", exercises.size());
                            edit.apply();
                            UtilMethod.saveArrExerciseData(getActivity(), "ExerciseData", exercises);
                            EventBus.getDefault().post(new Events<>(10));
                        }
                    } else {
                        if (binding.expansion.getVisibility() == View.VISIBLE) {
                            binding.expansion.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResult<ArrayList<Exercise>>> call, Throwable t) {
                    exercises = localList;
                    EventBus.getDefault().post(new Events<>(10));
                }
            });
        } else {
            localList = UtilMethod.loadArrExerciseData(getActivity(), "ExerciseData");
            int exerciseNetCount = sp.getInt("exerciseNetCount", localList.size());

            List<Exercise> addEs = localList.subList(exerciseNetCount, localList.size());

            Call<ApiResult<String>> addExercises = exerciseRequest.addExercises(currentUser.getUserToken(), addEs);
            addExercises.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("exerciseNetCount", exercises.size() + addEs.size());
                    edit.putBoolean("hasUnLoadExerciseData", false);
                    edit.apply();
                }

                @Override
                public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                    UtilMethod.showToast(getContext(), "无法连接到服务器");
                }
            });
            exercises = localList;
            EventBus.getDefault().post(new Events<>(10));
        }

    }

    public void setLastFewDaysData() {
        if (exercises.size() >= 7) {
            binding.lastSevenDayData.setVisibility(View.VISIBLE);
            if (listBar == null) {
                listBar = new ArrayList<>();
            }
            if (listBar.size() > 0) {
                listBar.clear();
            }
            List<Exercise> charData = UtilMethod.combineDistances(exercises);
            List<Exercise> fewDays = charData.subList(charData.size() - 7, charData.size());

            for (int i = 0; i < 7; i++) {
                listBar.add(new BarEntry(i, fewDays.get(i).getExerciseDistance().floatValue()));
            }
            ArrayList<String> days = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                String startTime = fewDays.get(i).getStartTime();
                String[] s = startTime.split(" ");
                if (currentMMdd.equals(s[0].substring(6))) {
                    days.add("今日");
                } else {
                    days.add(s[0].substring(6));
                }
            }
            BarDataSet barDataSet = new BarDataSet(listBar, "运动距离/米");
            BarData lineData = new BarData(barDataSet);
            barDataSet.setColors(Color.BLACK);
            binding.lastSevenDayData.setHighlightPerDragEnabled(false);
            binding.lastSevenDayData.setHighlightPerTapEnabled(false);
            //y轴
            binding.lastSevenDayData.getAxisRight().setEnabled(false);
            binding.lastSevenDayData.getAxisLeft().setDrawGridLines(false);
            binding.lastSevenDayData.getAxisLeft().setEnabled(false);
            //x轴
            binding.lastSevenDayData.getXAxis().setDrawGridLines(false);
            binding.lastSevenDayData.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
            binding.lastSevenDayData.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            binding.lastSevenDayData.setData(lineData);
            binding.lastSevenDayData.notifyDataSetChanged();
            binding.lastSevenDayData.invalidate();
        } else {
            binding.lastSevenDayData.setVisibility(View.GONE);
        }
    }

    public void initExerciseHistory() {
        if (exercises != null && exercises.size() >= 5) {
            Collections.reverse(exercises);
            exerciseAdapter = new ExerciseAdapter(new ArrayList<>(exercises.subList(0, 5)), getActivity(), position -> {
                EventBus.getDefault().postSticky(new Events<>(12, exercises.get(position)));
                startActivity(new Intent(getActivity(), ExerciseResultActivity.class));
            });
            binding.exerciseRec.setAdapter(exerciseAdapter);
            binding.exerciseRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(Events<Map<String, String>> events) {
        if (events.getType() == 10) {
            setLastFewDaysData();
            initExerciseHistory();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}