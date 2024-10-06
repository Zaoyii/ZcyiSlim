package com.rorschach.zcyislim;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.rorschach.zcyislim.Pager.Start.InitUserDataActivity;
import com.rorschach.zcyislim.Util.UtilMethod;
import com.rorschach.zcyislim.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    MainViewModel mainViewModel;
    SharedPreferences sharedPreferences;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMethod();
        initListener();
        checkUserDataInit();
        System.out.println(UtilMethod.sha1(this)+"---------111--------");
    }

    private void checkUserDataInit() {
        if (!sharedPreferences.getBoolean("hasGoal", false)) {
            startActivity(new Intent(this, InitUserDataActivity.class));
            finish();
        }
    }

    private void initListener() {
        binding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (mainViewModel.getCurrentFragment() != mainViewModel.getNoteFragment()) {
                        changeFragment(mainViewModel.getCurrentFragment(), mainViewModel.getNoteFragment());
                    }
                    break;
                case R.id.navigation_running:
                    if (mainViewModel.getCurrentFragment() != mainViewModel.getkBaoFragment()) {
                        changeFragment(mainViewModel.getCurrentFragment(), mainViewModel.getkBaoFragment());
                    }
                    break;
                case R.id.navigation_profile:
                    if (mainViewModel.getCurrentFragment() != mainViewModel.getProfileFragment()) {
                        changeFragment(mainViewModel.getCurrentFragment(), mainViewModel.getProfileFragment());
                    }
                    break;
            }
            item.setChecked(true);
            return false;
        });
    }

    private void initMethod() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sharedPreferences = UtilMethod.getPreferences(getApplication());
        if (mainViewModel.getCurrentFragment() != null) {
            changeFragment(null, mainViewModel.getCurrentFragment());
            binding.navView.setSelectedItemId(R.id.navigation_home);

        } else {
            changeFragment(null, mainViewModel.getNoteFragment());
            mainViewModel.setCurrentFragment(mainViewModel.getNoteFragment());
        }
    }

    public void changeFragment(Fragment from, Fragment to) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!to.isAdded()) {
            if (from != null) {
                fragmentTransaction.hide(from);
            }
            fragmentTransaction.add(binding.navFragment.getId(), to).commit();
        } else {
            if (from != null) {
                fragmentTransaction.hide(from);
            }
            fragmentTransaction.show(to).commit();
        }
        mainViewModel.setCurrentFragment(to);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }
}