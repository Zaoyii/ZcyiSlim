package com.rorschach.zcyislim.Pager.Exercise;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.rorschach.zcyislim.Entity.Events;
import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.Entity.PointLongiLati;
import com.rorschach.zcyislim.Entity.User;
import com.rorschach.zcyislim.Net.ExerciseRequest;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityExerciseResultBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExerciseResultActivity extends AppCompatActivity {
    ActivityExerciseResultBinding binding;
    Retrofit retrofit;
    ExerciseRequest exerciseRequest;
    User currentUser;
    Polyline polyline;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseResultBinding.inflate(getLayoutInflater());
        UtilMethod.setStatusBarTrans(this);
        setContentView(binding.getRoot());
        initMethod(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setExerciseResultType(Events<Exercise> events) {
        if (events.getType() == 12) {
            if (events.getData() != null) {
                Exercise e = events.getData();
                if (e != null) {
                    String showDistance = String.format(Locale.CHINA, "%.2f", e.getExerciseDistance());
                    String showSpeed = String.format(Locale.CHINA, "%.2f", e.getExerciseSpeed());
                    String showCalories = String.format(Locale.CHINA, "%.2f", e.getExerciseCalories());
                    binding.distance.setText(showDistance);
                    binding.speed.setText(showSpeed);
                    binding.calories.setText(showCalories);
                    ArrayList<PointLongiLati> pointLongiLatis = UtilMethod.transStringToPoints(e.getExerciseData());
                    ArrayList<LatLng> latLngs = UtilMethod.LongiLatiToLatLng(pointLongiLatis);
                    if (aMap == null) {
                        aMap = binding.mapView.getMap();
                    }
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 18f));
                    polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(20).color(Color.argb(235, 1, 180, 247)));
                    View startPoint = LayoutInflater.from(this).inflate(R.layout.amap_marker_start_point, null);
                    View endPoint = LayoutInflater.from(this).inflate(R.layout.amap_marker_end_point, null);
                    MarkerOptions startLocation = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(startPoint)).position(latLngs.get(0));
                    MarkerOptions endLocation = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(endPoint)).position(latLngs.get(latLngs.size() - 1));
                    aMap.addMarker(startLocation);
                    aMap.addMarker(endLocation);
                }
            }
        }
    }

    private void initMethod(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.baseUrl).build();
            exerciseRequest = retrofit.create(ExerciseRequest.class);
        }
        binding.mapView.onCreate(savedInstanceState);
        currentUser = UtilMethod.getObject(this, "currentUser");
        if (aMap == null) {
            aMap = binding.mapView.getMap();
        }
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScaleControlsEnabled(true);
        aMap.setMinZoomLevel(14);
        aMap.setMaxZoomLevel(20);
        binding.backButton.setOnClickListener(view -> finish());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}