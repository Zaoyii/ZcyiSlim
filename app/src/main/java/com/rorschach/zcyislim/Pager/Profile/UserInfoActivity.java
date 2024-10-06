package com.rorschach.zcyislim.Pager.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.rorschach.zcyislim.BaseActivity;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.UserRequest;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityUserInfoBinding;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoActivity extends BaseActivity {
    ActivityUserInfoBinding binding;
    Retrofit retrofit;
    UserRequest userRequest;
    User currentUser;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    MaterialAlertDialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        initListener();
        loadMyself();
    }

    @SuppressLint("SetTextI18n")
    private void initMethod() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            userRequest = retrofit.create(UserRequest.class);
        }
        currentUser = UtilMethod.getObject(this, "currentUser");
        binding.userAvatar.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(ShapeAppearanceModel.PILL).build());
        dialogBuilder = new MaterialAlertDialogBuilder(this);

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        String path = UtilMethod.getPath(this, uri);
                        UtilMethod.showToast(this, "上传中");
                        updateAvatar(path);
                    }
                });
    }

    private void initListener() {
        binding.backButton.setOnClickListener(view -> finish());
        binding.userAvatarBox.setOnClickListener(view -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        binding.userEmailBox.setOnClickListener(view -> startActivity(new Intent(UserInfoActivity.this, UpdateEmailActivity.class)));
        binding.userWeightBox.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(14, 2));
            startActivity(new Intent(UserInfoActivity.this, UpdateUserInfoActivity.class));
        });
        binding.usernameBox.setOnClickListener(view -> UtilMethod.showToast(UserInfoActivity.this, "用户名不可更改"));

        binding.userNickNameBox.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(14, 0));
            startActivity(new Intent(this, UpdateUserInfoActivity.class));
        });
        binding.userHeightBox.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Events<>(14, 1));
            startActivity(new Intent(this, UpdateUserInfoActivity.class));
        });
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            loadMyself();
        });
    }

    private void setUserInfo() {
        currentUser = UtilMethod.getObject(this, "currentUser");
        String userAvatar;
        if (currentUser.getUserAvatar().equals("defaultAvatar.jpg")) {
            userAvatar = Constant.userAvatar + currentUser.getUserAvatar();
        } else {
            userAvatar = Constant.userAvatar + currentUser.getUserId() + "/" + currentUser.getUserAvatar();
        }
        Glide.with(this).load(userAvatar).into(binding.userAvatar);
        String userNickName = currentUser.getUserNickName();
        String userName = currentUser.getUserName();
        String weight = currentUser.getUserWeight().toString();
        String height = currentUser.getUserHeight().toString();
        String userEmail = currentUser.getUserEmail();
        String userGender = currentUser.getUserGender() == 0 ? "男" : "女";
        String userAge = currentUser.getUserAge() + "";
        binding.userNickName.setText(userNickName);
        binding.username.setText(userName);
        binding.userWeight.setText(weight);
        binding.userHeight.setText(height);
        binding.userEmail.setText(userEmail);
        binding.userGender.setText(userGender);
        binding.userAge.setText(userAge);
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
                    UtilMethod.setObject(UserInfoActivity.this, "currentUser", data);
                    setUserInfo();
                    binding.refreshLayout.finishRefresh();
                } else {
                    UtilMethod.showToast(UserInfoActivity.this, "服务器有误，请稍后重试~");
                }
                binding.refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ApiResult<User>> call, Throwable t) {
                UtilMethod.showToast(getApplicationContext(), "无法连接到服务器");
            }
        });
    }

    private void updateAvatar(String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart.form-data"), file);
        MultipartBody.Part avatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
        Call<ApiResult<String>> updateAvatar = userRequest.updateAvatar(currentUser.getUserToken(), avatar);
        updateAvatar.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                if (response.body() != null && response.body().success) {
                    currentUser.setUserAvatar(response.body().data);
                    UtilMethod.showToast(UserInfoActivity.this, "更新成功!");
                    Glide.with(UserInfoActivity.this).load(path).into(binding.userAvatar);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                System.out.println(t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        UtilMethod.setObject(getApplicationContext(), "currentUser", currentUser);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        loadMyself();
        super.onResume();
    }
}