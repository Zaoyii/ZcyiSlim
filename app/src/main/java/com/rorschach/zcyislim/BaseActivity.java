package com.rorschach.zcyislim;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rorschach.zcyislim.Util.UtilMethod;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilMethod.changeStatusBarFrontColor(true, this, R.color.background);
    }
}
