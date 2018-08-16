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
    @ViewInject(R.id.ip_et2)
    private EditText mIpEt2;
    @ViewInject(R.id.ip_et3)
    private EditText mIpEt3;
    @ViewInject(R.id.ip_et4)
    private EditText mIpEt4;
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
        mIpEt.setText(SpUtils.getString(Constances.SP_KEY_ID_ADDRESS1, ""));
        mIpEt2.setText(SpUtils.getString(Constances.SP_KEY_ID_ADDRESS2, ""));
        mIpEt3.setText(SpUtils.getString(Constances.SP_KEY_ID_ADDRESS3, ""));
        mIpEt4.setText(SpUtils.getString(Constances.SP_KEY_ID_ADDRESS4, ""));
        mPortEt.setText(SpUtils.getString(Constances.SP_KEY_PORT, ""));
    }

    public void nextStep(View view) {
        String s = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

        //获取IP地址
        String ipText = mIpEt.getText().toString().trim();
        String ipText2 = mIpEt2.getText().toString().trim();
        String ipText3 = mIpEt3.getText().toString().trim();
        String ipText4 = mIpEt4.getText().toString().trim();
        String portText = mPortEt.getText().toString().trim();
        String iPAddress = ipText + '.' + ipText2 + '.' + ipText3 + '.' + ipText4;
        if (TextUtils.isEmpty(ipText) || TextUtils.isEmpty(ipText2) || TextUtils.isEmpty(ipText3) || TextUtils.isEmpty(ipText4)) {
            Toast.makeText(this, "IP地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!iPAddress.matches(s)) {
            Toast.makeText(this, "IP地址不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(portText)) {
            Toast.makeText(this, "端口号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //拼好的IP
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS, iPAddress);
        //IP段
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS1, ipText);
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS2, ipText2);
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS3, ipText3);
        SpUtils.putString(Constances.SP_KEY_ID_ADDRESS4, ipText4);
        SpUtils.putString(Constances.SP_KEY_PORT, portText);
        SpUtils.commite();
        //更新IP 和 端口
      //  Interface.updateBaseUrl();
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);

    }
}
