package com.rorschach.zcyislim.Pager.Profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityUpdateEmailBinding;

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

public class UpdateEmailActivity extends BaseActivity {
    ActivityUpdateEmailBinding binding;
    Retrofit retrofit;
    UserRequest userRequest;
    User currentUser;
    boolean send = false;
    private Handler mHandler;
    private Timer mTimer;
    int progressIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        initListener();
    }

    private void initListener() {
        binding.backButton.setOnClickListener(view -> finish());
        binding.send.setOnClickListener(view -> {
            if (binding.send.getText().equals(getString(R.string.send))) {
                if (progressIndex == 0) {
                    binding.send.setTextColor(getColor(R.color.underline));
                    binding.registerProgress.setVisibility(View.VISIBLE);
                    sendCode(currentUser.getUserEmail());
                } else if (progressIndex == 1) {
                    if (checkValidEmail()) {
                        binding.send.setTextColor(getColor(R.color.underline));
                        binding.registerProgress.setVisibility(View.VISIBLE);
                        sendCode(binding.newEmailInput.getText().toString());
                    }

                }
            }
        });
        binding.verify.setOnClickListener(view -> {
            if (!checkCodeIsEmpty()) {
                return;
            }
            if (progressIndex == 0) {
                String code = binding.codeInput.getText().toString();
                Call<ApiResult<String>> verifyEmail = userRequest.verifyEmail(currentUser.getUserEmail(), code, Constant.VERIFY_EMAIL_TYPE_UPDATE);
                verifyEmail.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                        if (response.body() != null && response.body().success) {
                            UtilMethod.showToast(UpdateEmailActivity.this, "验证成功");
                            EventBus.getDefault().post(new Events<>(16));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                        System.out.println("网络错误：" + t);
                    }
                });
            } else if (progressIndex == 1) {
                String code = binding.codeInput.getText().toString();
                Call<ApiResult<String>> updateEmail = userRequest.updateEmail(currentUser.getUserToken(), binding.newEmailInput.getText().toString(), code);
                updateEmail.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                        if (response.body() != null && response.body().success) {
                            UtilMethod.showToast(UpdateEmailActivity.this, "修改成功");
                            finish();
                        } else {

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

    public void sendCode(String email) {
        Call<ApiResult<String>> sendCode = userRequest.sendEmail(email, Constant.VERIFY_EMAIL_TYPE_UPDATE);
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
        String email = Objects.requireNonNull(binding.newEmailInput.getText()).toString();
        if (email.isEmpty()) {
            binding.newEmail.setErrorEnabled(true);
            binding.newEmail.setError("请输入邮箱!");
            return false;
        }
        if (!UtilMethod.checkValidEmail(email)) {
            binding.newEmail.setErrorEnabled(true);
            binding.newEmail.setError("邮箱格式错误!");
            return false;
        }
        if (currentUser.getUserEmail().equals(binding.newEmailInput.getText().toString())) {
            binding.newEmail.setErrorEnabled(true);
            binding.newEmail.setError("不能和旧邮箱相同!");
            return false;
        }
        return true;
    }

    private void initMethod() {
        EventBus.getDefault().register(this);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
        currentUser = UtilMethod.getObject(this, "currentUser");
        if (currentUser != null) {
            binding.userEmail.setText(getString(R.string.old_email, currentUser.getUserEmail()));
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
            EventBus.getDefault().post(new Events<>(1, "发送"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSendText(Events<String> events) {
        if (events.getType() == 1) {
            binding.send.setText(events.getData());
            binding.send.setTextColor(getColor(R.color.ver_text));
        } else if (events.getType() == 16) {
            progressIndex = 1;
            binding.userEmail.setVisibility(View.GONE);
            binding.newEmail.setVisibility(View.VISIBLE);
            binding.codeInput.setText("");
            binding.newEmailInput.requestFocus();
            binding.code.setErrorEnabled(false);
            stopCount();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}