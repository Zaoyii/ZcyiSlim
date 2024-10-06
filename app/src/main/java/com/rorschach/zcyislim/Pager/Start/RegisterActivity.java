package com.rorschach.zcyislim.Pager.Start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityRegisterBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends BaseActivity {
    ActivityRegisterBinding binding;
    boolean send = false;
    UserRequest userRequest;
    Retrofit retrofit;
    private Handler mHandler;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        setListener();
    }

    private void setListener() {
        binding.backButton.setOnClickListener(view -> finish());
        binding.usernameInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.username.setErrorEnabled(false);
        });

        binding.passwordInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.password.setErrorEnabled(false);
        });
        binding.emailInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.username.setErrorEnabled(false);
        });

        binding.codeInput.setOnFocusChangeListener((view, b) -> {
            if (b) binding.code.setErrorEnabled(false);
        });
        binding.send.setOnClickListener(view -> {
            if (checkUserNameAndPasswordIsEmpty()) {
                if (binding.send.getText().equals("发送")) {
                    if (checkValidEmail()) {
                        binding.send.setTextColor(getColor(R.color.underline));
                        binding.registerProgress.setVisibility(View.VISIBLE);
                        Call<ApiResult<String>> sendCode = userRequest.sendEmail(binding.emailInput.getText().toString(), Constant.VERIFY_EMAIL_TYPE_REGISTER);
                        sendCode.enqueue(new Callback<>() {
                            @Override
                            public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                                binding.registerProgress.setVisibility(View.GONE);
                                if (response.body() != null && response.body().success) {
                                    startCount();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                                System.out.println("网络错误：" + t);
                            }
                        });
                    }
                }
            }
        });
        binding.register.setOnClickListener(view -> {
            if (checkUserNameAndPasswordIsEmpty() && checkValidEmail() && checkCodeIsEmpty()) {
                Call<ApiResult<String>> verifyEmail = userRequest.register(Objects.requireNonNull(binding.usernameInput.getText()).toString(),
                        Objects.requireNonNull(binding.passwordInput.getText()).toString(),
                        Objects.requireNonNull(binding.emailInput.getText()).toString(),
                        Objects.requireNonNull(binding.codeInput.getText()).toString());
                verifyEmail.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResult<String>> call, @NonNull Response<ApiResult<String>> response) {
                        if (response.body() != null && response.body().success) {
                            UtilMethod.showToast(getApplicationContext(), "注册成功");
                            finish();
                        } else {
                            UtilMethod.showToast(getApplicationContext(), response.body().message);
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                        System.out.println("网络错误：" + t);
                    }
                });
            }
        });
    }

    public void startCount() {
        UtilMethod.showToast(this, "验证码发送成功，五分钟内有效");
        send = true;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            int index = 60;

            @Override
            public void run() {
                index--;
                mHandler.sendMessage(Message.obtain(mHandler, index));
                if (index == 0) {
                    stopCount();
                }

            }
        }, 0, 1000);

    }

    private void stopCount() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            EventBus.getDefault().post(new Events<>(1, getString(R.string.send)));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initMethod() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
        mHandler = new Handler(message -> {
            int what = message.what;
            if (what != 0) {
                binding.send.setText(what + "");
            } else {
                binding.send.setText(getText(R.string.send));
                binding.send.setTextColor(getColor(R.color.ver_text));
            }
            return false;
        });
        binding.emailInput.setText("xxx@qq.com");
        binding.usernameInput.setText("zaoyi");
        binding.passwordInput.setText("zaoyi...");
        EventBus.getDefault().register(this);
    }

    public boolean checkUserNameAndPasswordIsEmpty() {
        if (Objects.requireNonNull(binding.usernameInput.getText()).toString().isEmpty()) {
            binding.username.setErrorEnabled(true);
            binding.username.setError("请输入用户名!");
            return false;
        }

        if (Objects.requireNonNull(binding.passwordInput.getText()).toString().isEmpty()) {
            binding.password.setErrorEnabled(true);
            binding.password.setError("请输入密码!");
            return false;
        }
        return true;
    }

    public boolean checkCodeIsEmpty() {
        if (!send) {
            binding.code.setErrorEnabled(true);
            binding.code.setError("请先发送验证码");
            return false;
        }
        String s = Objects.requireNonNull(binding.codeInput.getText()).toString();
        if (s.length() != 6) {
            binding.code.setErrorEnabled(true);
            binding.code.setError("验证码格式错误");
            return false;
        }
        return true;
    }

    public boolean checkValidEmail() {
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        if (email.isEmpty()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError("请输入邮箱!");
            return false;
        }
        if (!UtilMethod.checkValidEmail(email)) {
            binding.email.setErrorEnabled(true);
            binding.email.setError("邮箱格式错误!");
            return false;
        }
        return true;
    }

    //EventBus
    //type==1 修改send按钮字符串为send&&修改send颜色
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSendText(Events<String> events) {
        if (events.getType() == 1) {
            binding.send.setText(events.getData());
            binding.send.setTextColor(getColor(R.color.ver_text));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}