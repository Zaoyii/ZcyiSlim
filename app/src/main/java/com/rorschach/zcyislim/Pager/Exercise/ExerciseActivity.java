package com.rorschach.zcyislim.Pager.Exercise;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.rorschach.zcyislim.Entity.ApiResult;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.Entity.PointLongiLati;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.ExerciseRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityExerciseBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExerciseActivity extends AppCompatActivity implements AMapLocationListener, LocationSource {
    ActivityExerciseBinding binding;
    private static final int RUNNING_INTERVAL = 3000;
    private static final int WALKING_INTERVAL = 8000;
    private static final int RIDING_INTERVAL = 1000;
    private static int EXERCISE_TYPE;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private AMap aMap = null;
    OnLocationChangedListener mListener;
    User currentUser;
    ArrayList<LatLng> latLngs = new ArrayList<>();
    ArrayList<PointLongiLati> savePoint = new ArrayList<>();

    SharedPreferences sp;
    SharedPreferences.Editor edit;
    Polyline polyline;
    boolean start = false;
    private Timer mTimer;
    boolean isCreateChannel = false;
    private static final String NOTIFICATION_CHANNEL_NAME = "LOCATION";
    private NotificationManager notificationManager = null;
    long startTime = -1;
    long lastTime;
    Double distance = 0d;
    Double calories = 0d;
    Double speed;
    Retrofit retrofit;
    ExerciseRequest exerciseRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UtilMethod.setStatusBarTrans(this);
        initMap(savedInstanceState);
        initLocation();
        requestPermission();
        binding.actionButton.setOnClickListener(view -> {
            if (UtilMethod.isLocationEnabled(getApplicationContext())) {
                if (start) {
                    String title;
                    if (distance < 400) {
                        title = "本次运动距离过短，将不会记录数据,确定结束吗?";
                    } else {
                        title = "确定停止运动吗?";
                    }
                    new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                            .setTitle(title)
                            .setPositiveButton("是", (dialogInterface, i) -> stopExercise())
                            .setNegativeButton("否", (dialogInterface, i) -> {
                            })
                            .show();
                } else {
                    new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                            .setTitle("确定开始吗?")
                            .setPositiveButton("是", (dialogInterface, i) -> startExercise())
                            .setNegativeButton("否", (dialogInterface, i) -> {
                            })
                            .show();
                }
            } else {
                UtilMethod.showToast(getApplicationContext(), "请打开定位~");
            }

        });

        binding.moveToCurrent.setOnClickListener(view -> {
            if (mLocationClient.getLastKnownLocation() != null) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude()), 18f));
            } else {
                mLocationClient.startLocation();
            }
        });
        //后续增加弹窗，判断数据是否存储，提醒用户
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.lackButton.setOnClickListener(view -> {
            Exercise lastExercise = UtilMethod.getObject(ExerciseActivity.this, "lastExercise");
        });
    }

    public void startExercise() {
        binding.actionImage.setImageResource(R.drawable.baseline_pause_24);
        startCount();
    }

    @SuppressLint("SimpleDateFormat")
    private void stopExercise() {
        binding.actionImage.setImageResource(R.drawable.baseline_play_arrow_24);
        start = false;
        mLocationClient.stopLocation();
        mLocationClient.disableBackgroundLocation(true);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (distance >= 400) {
            Date date = new Date(startTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = dateFormat.format(date);
            date = new Date(lastTime);
            String end = dateFormat.format(date);
            Exercise exercise = new Exercise(currentUser.getUserId(), 0, UtilMethod.transPointsToString(savePoint), distance, speed, calories, start, end);
            ArrayList<Exercise> list = UtilMethod.loadArrExerciseData(this, "ExerciseData");
            list.add(exercise);
            UtilMethod.saveArrExerciseData(this, "ExerciseData", list);
            edit.putBoolean("hasUnLoadExerciseData", true);
            edit.apply();
            Call<ApiResult<String>> addExercise = exerciseRequest.addExercise(currentUser.getUserToken(), exercise);
            addExercise.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResult<String>> call, Response<ApiResult<String>> response) {
                    edit.putBoolean("hasUnLoadExerciseData", false);
                    edit.apply();
                }

                @Override
                public void onFailure(Call<ApiResult<String>> call, Throwable t) {
                    System.out.println(t);
                }
            });
            exercise.setExerciseData(UtilMethod.transPointsToString(savePoint));
            EventBus.getDefault().postSticky(new Events<>(12, exercise));
            startActivity(new Intent(this, ExerciseResultActivity.class));
            finish();
        } else {
            finish();
        }

    }

    private Notification buildNotification() {
        Notification.Builder builder;
        Notification notification;
        if (null == notificationManager) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String channelId = getPackageName() + "zcyi";
        if (!isCreateChannel) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
            notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
            notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(notificationChannel);
            isCreateChannel = true;
        }
        builder = new Notification.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.logo).setContentTitle("Go Slim").setContentText("正在后台运行").setWhen(System.currentTimeMillis());

        notification = builder.build();
        return notification;
    }

    private void initMap(Bundle savedInstanceState) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            exerciseRequest = retrofit.create(ExerciseRequest.class);
        }
        sp = getSharedPreferences(UtilMethod.TAG, Context.MODE_PRIVATE);
        edit = sp.edit();
        currentUser = UtilMethod.getObject(this, "currentUser");
        binding.mapView.onCreate(savedInstanceState);
        aMap = binding.mapView.getMap();
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScaleControlsEnabled(true);
//        mUiSettings.setAllGesturesEnabled(false);
        aMap.setMinZoomLevel(14);
        aMap.setMaxZoomLevel(20);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.point));
        myLocationStyle.anchor(0.5f, 0.5f);
        // 自定义精度范围的圆形边框颜色  都为0则透明
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度  0 无宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色  都为0则透明
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置定位监听
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        EventBus.getDefault().register(this);
    }

    private void initLocation() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            mLocationOption.setHttpTimeOut(20000);
            switch (EXERCISE_TYPE) {
                case 0:
                    mLocationOption.setInterval(RUNNING_INTERVAL);
                    binding.exerciseType.setText("跑步模式");
                    break;
                case 1:
                    mLocationOption.setInterval(WALKING_INTERVAL);
                    binding.exerciseType.setText("快走模式");
                    break;
                case 2:
                    mLocationOption.setInterval(RIDING_INTERVAL);
                    binding.exerciseType.setText("骑行模式");
                    break;
            }
            mLocationOption.setMockEnable(false);
            //关闭缓存机制，高精度定位会产生缓存。
            mLocationOption.setLocationCacheEnable(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

        }
    }

    public void startRunning() {
        mLocationClient.stopLocation();
        mLocationClient.enableBackgroundLocation(2001, buildNotification());
        mLocationClient.startLocation();
        View startPoint = LayoutInflater.from(this).inflate(R.layout.amap_marker_start_point, null);
        MarkerOptions startLocation = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(startPoint)).position(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude()));
        aMap.addMarker(startLocation);
    }

    private void requestPermission() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.ACCESS_COARSE_LOCATION).permission(Permission.ACCESS_FINE_LOCATION).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            UtilMethod.showToast(ExerciseActivity.this, "获取部分权限成功，但部分权限未正常授予");
                        }
                        mLocationClient.startLocation();
                        if (mLocationClient.getLastKnownLocation() != null) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude()), 18f));
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            UtilMethod.showToast(ExerciseActivity.this, "被永久拒绝授权，请手动授予全部定位权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(ExerciseActivity.this, permissions);
                        } else {
                            UtilMethod.showToast(ExerciseActivity.this, "获取定位权限失败");
                        }
                    }
                });

    }

    public void startCount() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            int index = 3;

            @Override
            public void run() {
                EventBus.getDefault().post(new Events<>(7, index + ""));
                if (index < 0) {
                    stopCount();
                }
                index--;
            }
        }, 0, 1000);

    }

    private void stopCount() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            start = true;
            EventBus.getDefault().post(new Events<>(8));
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                if (start) {
                    long currentTimeMillis = System.currentTimeMillis();
                    lastTime = startTime;
                    latLngs.add(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    savePoint.add(new PointLongiLati(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(20).color(Color.argb(235, 1, 180, 247)));
                    float tempDistance = AMapUtils.calculateLineDistance(latLngs.get(latLngs.size() - 2), latLngs.get(latLngs.size() - 1));
                    distance += tempDistance;

                    speed = UtilMethod.calculateSpeed(distance, startTime, currentTimeMillis);
                    if (tempDistance > 0) {
                        calories += UtilMethod.calculateRunningCalories(70, lastTime, currentTimeMillis, speed);
                    }
                    lastTime = currentTimeMillis;
                    Map<String, String> data = new HashMap<>();
                    String showDistance = String.format(Locale.CHINA, "%.2f", distance);
                    String showSpeed = String.format(Locale.CHINA, "%.2f", speed);
                    String showCalories = String.format(Locale.CHINA, "%.2f", calories);
//                    System.out.println("d2:" + showDistance + "---speed:" + showSpeed + "-----calories:" + showCalories);
                    data.put("distance", showDistance);
                    data.put("speed", showSpeed);
                    data.put("calories", showCalories);
                    EventBus.getDefault().post(new Events<>(9, data));
                }
                //地址
                String location = "纬度：" + aMapLocation.getLatitude() + "\n" + "经度：" + aMapLocation.getLongitude() + "\n";
                System.out.println(location + "----==============----");
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
        requestPermission();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
        binding.mapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient != null) {
            mLocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRunningData(Events<Map<String, String>> events) {
        if (events.getType() == 9) {
            Map<String, String> data = events.getData();
            binding.distance.setText(data.get("distance"));
            binding.speed.setText(data.get("speed"));
            binding.calories.setText(data.get("calories"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCountDown(Events<String> events) {
        if (events.getType() == 7) {
            if (events.getData().equals("0")) {
                binding.countDown.setText("GO!");
            } else if (events.getData().equals("-1")) {
                binding.countDown.setText("");
            } else {
                binding.countDown.setText(events.getData());
            }
        } else if (events.getType() == 8) {
            startRunning();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setExerciseType(Events<Integer> events) {
        if (events.getType() == 11) {
            switch (events.getData()) {
                case 0:
                    EXERCISE_TYPE = 0;
                    break;
                case 1:
                    EXERCISE_TYPE = 1;
                    break;
                case 2:
                    EXERCISE_TYPE = 2;
                    break;
            }
        }
    }
}