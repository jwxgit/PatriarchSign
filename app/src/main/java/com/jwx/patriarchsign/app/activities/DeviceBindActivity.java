package com.jwx.patriarchsign.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.ActivityManager;
import com.jwx.patriarchsign.app.dialog.PickTableDialog;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.data.domain.InoculationTableInfo;
import com.jwx.patriarchsign.utils.SpUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class DeviceBindActivity extends BaseActivity {

    @ViewInject(R.id.close_bt)
    private View     mCloseBt;
    @ViewInject(R.id.bind_layout)
    private View     mBindLayout;
    @ViewInject(R.id.unbind_layout)
    private View     mUnBindLayout;
    @ViewInject(R.id.bind_bt)
    private Button   mBindBt;
    @ViewInject(R.id.commit_bt)
    private Button   mNextBt;
    @ViewInject(R.id.selector)
    private TextView mSelectedTable;
    @ViewInject(R.id.bind_table_name)
    private TextView mBoundTableName;
    private int      mWorkbenchId;
    private String   mWorkbenchName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_device_bind);
        x.view().inject(this);
        mWorkbenchId = SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1);
        mWorkbenchName = SpUtils.getString(Constances.SP_KEY_WORKBENCH_NAME, "");
        if (mWorkbenchId > 0) {
            mNextBt.setEnabled(true);
            mBindLayout.setVisibility(View.GONE);
            mUnBindLayout.setVisibility(View.VISIBLE);
            mBoundTableName.setText(mWorkbenchName);
        } else {
            mNextBt.setEnabled(false);
            mBindLayout.setVisibility(View.VISIBLE);
            mUnBindLayout.setVisibility(View.GONE);
        }
        if (getIntent().getBooleanExtra(Constances.INTENT_KEY_CLOSEABLE, true)) {
            mCloseBt.setVisibility(View.VISIBLE);
        } else {
            mCloseBt.setVisibility(View.GONE);
        }
    }

    /**
     * 选择登记台对话框
     *
     * @param view
     */
    public void pickOptions(View view) {
        PickTableDialog dialog = new PickTableDialog();
        dialog.setTablePickedListener(new PickTableDialog.TablePickedCallback() {
            @Override
            public void onPicked(InoculationTableInfo tableInfo) {
                mWorkbenchId = tableInfo.getWorkbenchId();
                mWorkbenchName = tableInfo.getName();
                mSelectedTable.setText(mWorkbenchName);
                mBindBt.setVisibility(View.VISIBLE);
                mBindBt.setEnabled(true);
            }
        });
        dialog.show(getSupportFragmentManager(), "pick");
    }

    /**
     * 绑定登记台
     *
     * @param view
     */
    public void bindDevice(View view) {
        SpUtils.putInt(Constances.SP_KEY_WORKBENCH_ID, mWorkbenchId);
        SpUtils.putString(Constances.SP_KEY_WORKBENCH_NAME, mWorkbenchName);
        SpUtils.commite();
        mNextBt.setEnabled(true);
        mBoundTableName.setEnabled(false);
        mBindLayout.setVisibility(View.GONE);
        mUnBindLayout.setVisibility(View.VISIBLE);
        mBoundTableName.setText(mWorkbenchName);
    }

    /**
     * 解绑
     *
     * @param view
     */
    public void unBindDevice(View view) {
        SpUtils.putInt(Constances.SP_KEY_WORKBENCH_ID, -1);
        SpUtils.putString(Constances.SP_KEY_WORKBENCH_NAME, "");
        SpUtils.commite();
        mBoundTableName.setEnabled(true);
        mNextBt.setEnabled(false);
        mBindLayout.setVisibility(View.VISIBLE);
        mUnBindLayout.setVisibility(View.GONE);
        mWorkbenchId = -1;
        mSelectedTable.setText("点击选择登记台   ▼");
        mBindBt.setVisibility(View.GONE);
        mBindBt.setEnabled(false);
    }

    @Override
    public void back(View view) {
        ActivityManager.finishToTarget(this, DoctorInfoActivity.class, true);
    }

    public void nextStep(View view) {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        ActivityManager.finishAll();
    }
}
