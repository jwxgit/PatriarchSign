package com.jwx.patriarchsign.app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.View.signpad.views.SignaturePad;
import com.jwx.patriarchsign.app.application.BaseApplication;
import com.jwx.patriarchsign.data.cache.BitmapCache;
import com.jwx.patriarchsign.data.domain.ParentInfo;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.UploadUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/7 0007.
 */

public class SignCollectActivity extends BaseActivity {
    @ViewInject(R.id.sign_pad)
    private SignaturePad mSignPad;
    @ViewInject(R.id.commit_bt)
    private Button       mCommitBt;
    @ViewInject(R.id.clear_bt)
    private Button       mClearBt;
    @ViewInject(R.id.skip_bt)
    private Button       mSkipBt;
    @ViewInject(R.id.back_img)
    private ImageView    mBackImg;

    private ParentInfo mParentInfo;
    private boolean    mNeedFinger;

    private int     signature;
    private int     fingerPrints;
    private boolean hasBack; //是否有返回键

    private boolean isSkip = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_sign_collect);
        x.view().inject(this);
        mParentInfo = (ParentInfo) getIntent().getSerializableExtra("parentInfo");
        signature = getIntent().getIntExtra("signature", 0);
        fingerPrints = getIntent().getIntExtra("fingerPrints", 0);
        hasBack = getIntent().getBooleanExtra("hasBack", true);
        if (mParentInfo != null) {
            BaseApplication.getOffline().stop();
            BaseApplication.getOffline().play(getResources().getString(R.string.sign_speech));
            if (!hasBack) {
                mBackImg.setVisibility(View.GONE);
            }
            if (signature == 0) {
                mSkipBt.setVisibility(View.VISIBLE);
            }
            if (fingerPrints >= 0) {
                mCommitBt.setText("下一步");
            } else {
                mCommitBt.setText("完成");
            }
        } else {
            mCommitBt.setText("确认签名");
        }
        mSignPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                mCommitBt.setEnabled(true);
                mClearBt.setEnabled(true);
            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {
                mCommitBt.setEnabled(false);
                mClearBt.setEnabled(false);
            }
        });
    }

    public void commitSign(View view) {
        if (mParentInfo == null) {
            Intent intent = new Intent();
            intent.putExtra("signBmp", BitmapUtils.Bitmap2Bytes(mSignPad.getTransparentSignatureBitmap()));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            if (isSkip) {
                BitmapCache.setParentSignBmp(null);
            } else {
                BitmapCache.setParentSignBmp(mSignPad.getTransparentSignatureBitmap());
            }

            Intent intent = null;

            if (fingerPrints >= 0) {
                intent = new Intent(this, FingerCollectActivity.class);
                intent.putExtra("fingerPrints", fingerPrints);
            } else {
                LogUtils.i("指纹采集无");
            }
            if (intent != null) {
                intent.putExtra("parentInfo", mParentInfo);
                startActivity(intent);
            } else {
                UploadUtil.uploadParentInfo(this, mParentInfo.getChildCode(), mParentInfo.getChildName());
            }
        }
    }

    /**
     * 跳过
     *
     * @param view
     */
    public void onSkip(View view) {
        isSkip = true;
        commitSign(view);
    }

    public void clear(View view) {
        mSignPad.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApplication.getOffline().stop();
    }
}
