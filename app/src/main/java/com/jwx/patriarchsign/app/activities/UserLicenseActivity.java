package com.jwx.patriarchsign.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.dialog.UserLicenseDialog;
import com.jwx.patriarchsign.data.domain.ParentInfo;
import com.jwx.patriarchsign.data.protocols.CommentProtocol;
import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 用户许可页面
 */
public class UserLicenseActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @ViewInject(R.id.checkbox)
    private CheckBox mCheckBox;
    @ViewInject(R.id.agree_bt)
    private Button   mAgreeBt;

    private String childCode;
    private String childName;
    private int    fingerPrints;
    private int    signature;
    private int    pic;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_user_license);
        x.view().inject(this);
        activity = this;
        childCode = getIntent().getStringExtra("childCode");
        childName = getIntent().getStringExtra("childName");
        fingerPrints = getIntent().getIntExtra("fingerPrints", 0);
        signature = getIntent().getIntExtra("signature", 0);
        pic = getIntent().getIntExtra("pic", 0);
        mCheckBox.setOnCheckedChangeListener(this);

    }


    public void onDisAgree(View view) {
        CommentProtocol.userAllowSignature(childCode, 1, new CommentProtocol.ResultCallback() {
            @Override
            public void onSuccess() {
                activity.finish();
            }

            @Override
            public void onFailed() {
                ToastUtils.showMessageShort("请求错误");
            }
        });

    }

    public void onAgree(View view) {
        CommentProtocol.userAllowSignature(childCode, 0, new CommentProtocol.ResultCallback() {
            @Override
            public void onSuccess() {
                ParentInfo parentInfo = new ParentInfo();
                parentInfo.setChildCode(childCode);
                parentInfo.setChildName(childName);
                Intent intent = null;

                if (pic >= 0) {
                    toCameraActivity(activity, parentInfo, pic, fingerPrints, signature);
                } else {

                    LogUtils.i("拍照无");

                    if (signature >= 0) {
                        intent = new Intent(activity, SignCollectActivity.class);
                        intent.putExtra("signature", signature);
                        intent.putExtra("fingerPrints", fingerPrints);
                        intent.putExtra("hasBack", false);  //页面不显示返回键
                    } else {

                        LogUtils.i("拍照无 签字无");

                        if (fingerPrints >= 0) {
                            intent = new Intent(activity, FingerCollectActivity.class);
                            intent.putExtra("fingerPrints", fingerPrints);
                            intent.putExtra("hasBack", false);  //页面不显示返回键
                        } else {
                            LogUtils.i("拍照无 签字无 指纹采集无");
                        }
                    }
                }

                if (intent != null) {
                    intent.putExtra("parentInfo", parentInfo);
                    activity.startActivity(intent);
                }

                activity.finish();
            }

            @Override
            public void onFailed() {
                ToastUtils.showMessageShort("请求错误");
            }
        });
    }

    public void onUserlicense(View view) {
        UserLicenseDialog userLicenseDialog = new UserLicenseDialog();
        userLicenseDialog.setCancelable(false);
        userLicenseDialog.show(getSupportFragmentManager(), "dialog");
    }


    private void toCameraActivity(final Activity activity, final ParentInfo parentInfo, final int pic, final int fingerPrints, final int signature) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    Intent intent = new Intent(activity, FaceCollectionActivity.class);
                    intent.putExtra("pic", pic);
                    intent.putExtra("fingerPrints", fingerPrints);
                    intent.putExtra("signature", signature);
                    intent.putExtra("parentInfo", parentInfo);
                    intent.putExtra("hasBack", false);  //页面不显示返回键
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                // Failure.
                if (requestCode == 200) {
                    ToastUtils.showMessageShort("请允许相机权限哦！");
                }
            }
        };
        AndPermission.with(this).requestCode(200).permission(Manifest.permission.CAMERA).callback(listener).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mAgreeBt.setEnabled(true);
        } else {
            mAgreeBt.setEnabled(false);
        }
    }
}
