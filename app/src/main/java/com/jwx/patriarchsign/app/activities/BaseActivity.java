package com.jwx.patriarchsign.app.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jwx.patriarchsign.app.application.ActivityManager;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
