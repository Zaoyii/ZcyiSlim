package com.rorschach.zcyislim.Pager.Start;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.MainActivity;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityInitUserDataBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitUserDataActivity extends BaseActivity {
    ActivityInitUserDataBinding binding;
    Retrofit retrofit;
    UserRequest userRequest;
    User currentUser;
    View dialogNpic;
    NumberPicker numberPicker;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    int height = -1;
    int weight = -1;
    int age = -1;
    int gender = -1;
    int goal = -1;
    String nickname = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UtilMethod.changeStatusBarFrontColor(true, this, R.color.white);
        initMethod();
        initListener();

    }

    private void initMethod() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
        sharedPreferences = UtilMethod.getPreferences(getApplication());
        editor = sharedPreferences.edit();
        currentUser = UtilMethod.getObject(this, "currentUser");
        dialogNpic = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
        numberPicker = dialogNpic.findViewById(R.id.num_pic);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        materialAlertDialogBuilder.setView(dialogNpic);
    }

    private void initListener() {
        binding.heightChoose.setOnClickListener(view -> {
            if (dialogNpic.getParent() != null) {
                ((ViewGroup) dialogNpic.getParent()).removeView(dialogNpic);
            }
            materialAlertDialogBuilder.setTitle("选择身高");

            numberPicker.setMaxValue(230);
            numberPicker.setMinValue(140);
            if (height == -1) {
                numberPicker.setValue(170);
            } else {
                numberPicker.setValue(height);
            }

            materialAlertDialogBuilder.setNegativeButton("取消", null);
            materialAlertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    height = numberPicker.getValue();
                    binding.heightChoose.setText(height + "");
                }
            });
            materialAlertDialogBuilder.show();
        });
        binding.weightChoose.setOnClickListener(view -> {
            if (dialogNpic.getParent() != null) {
                ((ViewGroup) dialogNpic.getParent()).removeView(dialogNpic);
            }
            materialAlertDialogBuilder.setTitle("选择体重");
            numberPicker.setMaxValue(200);
            numberPicker.setMinValue(40);
            if (weight == -1) {
                numberPicker.setValue(50);
            } else {
                numberPicker.setValue(weight);
            }
            materialAlertDialogBuilder.setNegativeButton("取消", null);
            materialAlertDialogBuilder.setPositiveButton("确定", (dialogInterface, i) -> {
                weight = numberPicker.getValue();
                binding.weightChoose.setText(weight + "");
            });
            materialAlertDialogBuilder.show();
        });
        binding.ageChoose.setOnClickListener(view -> {
            if (dialogNpic.getParent() != null) {
                ((ViewGroup) dialogNpic.getParent()).removeView(dialogNpic);
            }
            materialAlertDialogBuilder.setTitle("选择年龄");
            numberPicker.setMaxValue(120);
            numberPicker.setMinValue(16);
            if (age == -1) {
                numberPicker.setValue(20);
            } else {
                numberPicker.setValue(age);
            }

            materialAlertDialogBuilder.setNegativeButton("取消", null);
            materialAlertDialogBuilder.setPositiveButton("确定", (dialogInterface, i) -> {
                age = numberPicker.getValue();
                binding.ageChoose.setText(age + "");
            });
            materialAlertDialogBuilder.show();
        });
        binding.genderMale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                gender = 0;
            }
        });
        binding.genderFemale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                gender = 1;
            }
        });
        binding.reduceFat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                goal = 0;
            }
        });
        binding.maintain.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                goal = 1;
            }
        });
        binding.gainMuscle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                goal = 2;
            }
        });
        binding.save.setOnClickListener(view -> {
            nickname = binding.nicknameInput.getText().toString();
            binding.registerProgress.setVisibility(View.VISIBLE);
            if (checkData()) {
                Call<ApiResult<String>> initUserData = userRequest.initUserData(currentUser.getUserToken(), nickname, gender, age, weight, height, goal);
                initUserData.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                        if (response.body() != null && response.body().success) {
                            currentUser.setUserNickName(nickname);
                            currentUser.setUserGender(gender);
                            currentUser.setUserAge(age);
                            currentUser.setUserWeight((double) weight);
                            currentUser.setUserHeight((double) height);
                            currentUser.setUserGoal(goal);
                            UtilMethod.setObject(getApplication(), "currentUser", currentUser);
                            editor.putBoolean("hasGoal", true);
                            editor.apply();
                            UtilMethod.showToast(InitUserDataActivity.this, "个人数据初始化成功~");
                            startActivity(new Intent(InitUserDataActivity.this, MainActivity.class));
                            finish();
                        }
                        binding.registerProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                        binding.registerProgress.setVisibility(View.GONE);
                    }
                });
            } else {
                binding.registerProgress.setVisibility(View.GONE);
            }

        });
    }

    private boolean checkData() {
        System.out.println(height + ":" + weight + ":" + gender + ":" + goal + ":" + age + ":" + nickname + "----=-=-");
        if (height == -1) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_height));
            return false;
        }
        if (weight == -1) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_weight));
            return false;
        }
        if (gender == -1) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_gender));
            return false;
        }
        if (goal == -1) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_goal));
            return false;
        }
        if (age == -1) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_age));
            return false;
        }
        if (nickname.length() < 2) {
            binding.alertText.setVisibility(View.VISIBLE);
            binding.alertText.setText(getString(R.string.check_nickname));
            return false;
        }
        return true;
    }
}