package com.rorschach.zcyislim.Pager.Profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Entity.Weight;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityUpdateUserInfoBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateUserInfoActivity extends BaseActivity {
    ActivityUpdateUserInfoBinding binding;
    int type = -1;
    Retrofit retrofit;
    User currentUser;
    UserRequest userRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();

    }

    private void initMethod() {
        currentUser = UtilMethod.getObject(this, "currentUser");
        EventBus.getDefault().register(this);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
        binding.save.setOnClickListener(view -> updateInfo());
        binding.backButton.setOnClickListener(view -> finish());

        binding.newHeightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.alertText.setVisibility(View.GONE);
            }
        });
        binding.newNicknameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.alertText.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setUpdateType(Events<Integer> events) {
        if (events.getType() == 14) {
            if (events.getData() == 0) {
                binding.updateNicknameBox.setVisibility(View.VISIBLE);
                binding.updateHeightBox.setVisibility(View.GONE);
                binding.updateWeightBox.setVisibility(View.GONE);
                binding.nickname.setText(currentUser.getUserNickName());
                type = 0;
            } else if (events.getData() == 1) {
                binding.updateHeightBox.setVisibility(View.VISIBLE);
                binding.updateNicknameBox.setVisibility(View.GONE);
                binding.updateWeightBox.setVisibility(View.GONE);
                binding.height.setText(currentUser.getUserHeight().toString());
                type = 1;
            } else if (events.getData() == 2) {
                binding.updateWeightBox.setVisibility(View.VISIBLE);
                binding.updateHeightBox.setVisibility(View.GONE);
                binding.updateNicknameBox.setVisibility(View.GONE);
                type = 2;
            }
        }

    }

    public void updateInfo() {
        binding.saveProgress.setVisibility(View.VISIBLE);
        if (type == 0) {
            String nickname = binding.newNicknameInput.getText().toString();
            if (nickname.length() < 2) {
                binding.saveProgress.setVisibility(View.GONE);
                binding.alertText.setVisibility(View.VISIBLE);
                binding.alertText.setText("昵称至少要2位");
            } else {
                binding.alertText.setVisibility(View.GONE);
                Call<ApiResult<String>> updateNickname = userRequest.updateNickname(currentUser.getUserToken(), nickname, currentUser.getUserId());
                updateNickname.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                        if (response.body() != null && response.body().success) {
                            binding.saveProgress.setVisibility(View.GONE);
                            currentUser.setUserNickName(nickname);
                            UtilMethod.setObject(getApplicationContext(), "currentUser", currentUser);
                            UtilMethod.showToast(UpdateUserInfoActivity.this, "修改成功!");
                            finish();
                        } else {
                            binding.saveProgress.setVisibility(View.GONE);
                            UtilMethod.showToast(UpdateUserInfoActivity.this, response.body().message);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                        binding.saveProgress.setVisibility(View.GONE);
                        UtilMethod.showToast(UpdateUserInfoActivity.this, "网络出错请稍后重试");
                    }
                });
            }

        } else if (type == 1) {
            String height = binding.newHeightInput.getText().toString();
            if (height.matches("\\d+(\\.\\d+)?")) {
                double newHeight = Double.parseDouble(height);
                if (newHeight >= 50 && newHeight <= 300) {
                    binding.alertText.setVisibility(View.GONE);
                    Call<ApiResult<String>> updateHeight = userRequest.updateHeight(currentUser.getUserToken(), newHeight, currentUser.getUserId());
                    updateHeight.enqueue(new Callback<ApiResult<String>>() {
                        @Override
                        public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                            if (response.body() != null && response.body().success) {
                                binding.saveProgress.setVisibility(View.GONE);
                                currentUser.setUserHeight(newHeight);
                                UtilMethod.setObject(getApplicationContext(), "currentUser", currentUser);
                                UtilMethod.showToast(UpdateUserInfoActivity.this, "修改成功!");
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                            binding.saveProgress.setVisibility(View.GONE);
                            UtilMethod.showToast(UpdateUserInfoActivity.this, "网络出错请稍后重试");
                        }
                    });
                } else {
                    binding.saveProgress.setVisibility(View.GONE);
                    binding.alertText.setVisibility(View.VISIBLE);
                    binding.alertText.setText("请输入正确的身高!");

                }
            } else {
                binding.saveProgress.setVisibility(View.GONE);
                binding.alertText.setVisibility(View.VISIBLE);
                binding.alertText.setText("请输入正确的身高!");
            }
        } else if (type == 2) {
            String weight = binding.newWeightInput.getText().toString();
            if (weight.matches("\\d+(\\.\\d+)?")) {
                double newWeight = Double.parseDouble(weight);
                if (newWeight >= 30 && newWeight <= 200) {
                    binding.alertText.setVisibility(View.GONE);
                    Call<ApiResult<String>> updateWeight = userRequest.updateWeight(currentUser.getUserToken(), new Weight(newWeight, currentUser.getUserId()));
                    updateWeight.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                            if (response.body() != null && response.body().success) {
                                binding.saveProgress.setVisibility(View.GONE);
                                currentUser.setUserWeight(newWeight);
                                UtilMethod.setObject(getApplicationContext(), "currentUser", currentUser);
                                UtilMethod.showToast(UpdateUserInfoActivity.this, "修改成功!");
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                            binding.saveProgress.setVisibility(View.GONE);
                            UtilMethod.showToast(UpdateUserInfoActivity.this, "网络出错请稍后重试");
                        }
                    });
                } else {
                    binding.saveProgress.setVisibility(View.GONE);
                    binding.alertText.setVisibility(View.VISIBLE);
                    binding.alertText.setText("请输入正确的体重!");

                }
            } else {
                binding.saveProgress.setVisibility(View.GONE);
                binding.alertText.setVisibility(View.VISIBLE);
                binding.alertText.setText("请输入正确的体重!");
            }
        } else {
            binding.saveProgress.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}