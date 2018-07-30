package com.jwx.patriarchsign.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.utils.SpUtils;
import com.jwx.patriarchsign.utils.UIUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class IpConfigActivity extends BaseActivity {
    @ViewInject(R.id.ip_et)
    private EditText mIpEt;
    @ViewInject(R.id.port_et)
    private EditText mPortEt;
    @ViewInject(R.id.root_layout)
    private View     mRootLayout;
    @ViewInject(R.id.commit_bt)
    private Button   mCommitBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        UIUtil.sw();
    }


    private void initView() {
        setContentView(R.layout.activity_ip_config);
        x.view().inject(this);
        UIUtil.controlKeyboardLayout(mRootLayout, mCommitBt, UIUtil.dip2px(10), null);
        mPortEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    nextStep(null);
                    return true;
                }
                return false;
            }
        });
        mIpEt.setText(SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, ""));
        mPortEt.setText(SpUtils.getString(Constances.SP_KEY_PORT, ""));
    }

    public void nextStep(View view) {
        String s = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        String ipText = mIpEt.getText().toString().trim();
        String portText = mPortEt.getText().toString().trim();
        if (TextUtils.isEmpty(ipText)) {
            Toast.makeText(this, "IP地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ipText.matches(s)) {
            Toast.makeText(this, "IP地址不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(portText)) {
            Toast.makeText(this, "端口号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS, ipText);
        SpUtils.putString(Constances.SP_KEY_PORT, portText);
        SpUtils.commite();
        Interface.updateBaseUrl();//更新IP 和 端口
        Intent intent = new Intent(this, DoctorInfoActivity.class);
        startActivity(intent);
    }
}
