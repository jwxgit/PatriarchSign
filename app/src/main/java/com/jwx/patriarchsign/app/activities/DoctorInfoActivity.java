package com.jwx.patriarchsign.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.ActivityManager;
import com.jwx.patriarchsign.app.fragments.DoctorInfoListFragment;
import com.jwx.patriarchsign.data.constants.Constances;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorInfoActivity extends BaseActivity {
    @ViewInject(R.id.close_bt)
    private View                   mCloseBt;
    private DoctorInfoListFragment mIinfoListFragment;
    @ViewInject(R.id.commit_bt)
    private Button                 commitBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIinfoListFragment == null) {
            mIinfoListFragment = new DoctorInfoListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.info_list_frame, mIinfoListFragment).commit();
        } else {
            mIinfoListFragment.refresh();
        }

    }

    private void initView() {
        setContentView(R.layout.activity_doctor_info);
        x.view().inject(this);
        if (getIntent().getBooleanExtra(Constances.INTENT_KEY_CLOSEABLE, true)) {
            mCloseBt.setVisibility(View.VISIBLE);
        } else {
            mCloseBt.setVisibility(View.GONE);
        }
    }


    @Override
    public void back(View view) {
        ActivityManager.finishToTarget(this, IpConfigActivity.class, true);
    }


    public void nextStep(View view) {
        Intent intent = new Intent(this, DeviceBindActivity.class);
        startActivity(intent);
    }

    //设置跳过按钮的可点击状态
    public void setCommitBtEnable(boolean enable) {
        commitBt.setEnabled(enable);
    }

}
