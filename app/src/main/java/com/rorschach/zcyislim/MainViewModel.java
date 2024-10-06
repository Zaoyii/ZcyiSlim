package com.rorschach.zcyislim;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.rorschach.zcyislim.Pager.Home.HomeFragment;
import com.rorschach.zcyislim.Pager.Profile.ProfileFragment;
import com.rorschach.zcyislim.Pager.Exercise.ExerciseFragment;

public class MainViewModel extends ViewModel {
    HomeFragment homeFragment;
    ExerciseFragment runningFragment;
    ProfileFragment profileFragment;
    Fragment currentFragment;

    public MainViewModel() {
        homeFragment = new HomeFragment();
        runningFragment = new ExerciseFragment();
        profileFragment = new ProfileFragment();
    }

    public HomeFragment getNoteFragment() {
        return homeFragment;
    }

    public ExerciseFragment getkBaoFragment() {
        return runningFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }
}
