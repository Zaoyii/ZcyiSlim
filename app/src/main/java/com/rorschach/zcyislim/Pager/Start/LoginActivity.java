package com.rorschach.zcyislim.Pager.Start;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.MainActivity;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Retrofit retrofit;
    UserRequest userRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        setListen();
    }

    private void setListen() {
        binding.usernameInput.setText("zaoyi");
        binding.passwordInput.setText("zaoyi...");
        binding.usernameInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.username.setErrorEnabled(false);
        });

        binding.passwordInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.password.setErrorEnabled(false);
        });
        binding.login.setOnClickListener(v -> {
            String username = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            if (username.isEmpty() || username.length() < 4) {
                binding.username.setErrorEnabled(true);
                binding.username.setError("Incorrect account input!");
                return;
            } else {
                binding.username.setErrorEnabled(false);
            }
            if (password.isEmpty() || password.length() < 6) {
                binding.password.setErrorEnabled(true);
                binding.password.setError("Incorrect password input!");
                return;
            } else {
                binding.password.setErrorEnabled(false);
            }

            binding.alertText.setVisibility(View.GONE);
            Call<ApiResult<User>> login = userRequest.login(username, password);
            binding.loginProgress.setVisibility(View.VISIBLE);
            login.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ApiResult<User>> call, @NonNull Response<ApiResult<User>> response) {
                    if (response.body() != null && response.body().success) {
                        UtilMethod.setObject(getApplication(), "currentUser", response.body().data);
                        System.out.println(response.body().data + "---=-=--=-=-=-=123123");
                        System.out.println(response.body().data.getUserGoal() + "---=-=--=-=-=-=123123");
                        if (response.body().data.getUserGoal() != -1) {
                            editor.putBoolean("hasGoal", true);
                        }
                        editor.putBoolean("isLogin", true);
                        editor.putLong("currentUserId", response.body().data.getUserId());
                        editor.apply();
                        UtilMethod.showToast(getApplication(), "登录成功");
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        finish();
                    } else {
                        binding.alertText.setVisibility(View.VISIBLE);
                    }
                    binding.loginProgress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<ApiResult<User>> call, @NonNull Throwable t) {
                    binding.loginProgress.setVisibility(View.GONE);
                    UtilMethod.showToast(getApplication(), "网络连接有问题，检查一下网络8~");
                }
            });
        });
        binding.register.setOnClickListener(view -> startActivity(new Intent(getApplication(), RegisterActivity.class)));
    }

    private void initMethod() {
        sharedPreferences = UtilMethod.getPreferences(getApplication());
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(getApplication(), MainActivity.class));
            finish();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
    }

}