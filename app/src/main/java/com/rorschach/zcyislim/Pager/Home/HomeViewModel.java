package com.rorschach.zcyislim.Pager.Home;

import android.view.View;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    View rootView;

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

}
