package com.jwx.patriarchsign.app.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.BaseApplication;
import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.data.cache.BitmapCache;
import com.jwx.patriarchsign.data.domain.ParentInfo;
import com.jwx.patriarchsign.msg.SocketMessage;
import com.jwx.patriarchsign.netty.MessageLisener;
import com.jwx.patriarchsign.netty.MessageLisenerRegister;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.UploadUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import android_serialport_api.FingerprintAPI;
import android_serialport_api.SerialPortManager;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class FingerCollectActivity extends BaseActivity {

    @ViewInject(R.id.linlay_prompt)
    private LinearLayout linear_prompt;
    @ViewInject(R.id.img_prompt)
    private ImageView    img_prompt;
    @ViewInject(R.id.text_prompt)
    private TextView     text_prompt;

    @ViewInject(R.id.finger_img)
    private ImageView mFingerImg;
    @ViewInject(R.id.commit_layout)
    private View      mCommitLayout;
    @ViewInject(R.id.commit_bt)
    private Button    mCommitBt;
    @ViewInject(R.id.skip_bt)
    private Button    mSkipBt;
    @ViewInject(R.id.back_img)
    private ImageView mBackImg;

    private FingerprintAPI mFingerApi;
    private Bitmap         mFingerBmp;
    private boolean        isRunning;
    private ParentInfo     mParentInfo;
    private AnimatorSet    animatorSet;

    private int     fingerPrints;
    private boolean hasBack; //是否有返回键

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMessageLisener();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFingerApi();
    }

    private void initView() {
        setContentView(R.layout.activity_finger_print);
        x.view().inject(this);
        mParentInfo = (ParentInfo) getIntent().getSerializableExtra("parentInfo");
        fingerPrints = getIntent().getIntExtra("fingerPrints", 0);
        hasBack = getIntent().getBooleanExtra("hasBack", true);
        if (mParentInfo == null) {
            mCommitBt.setText("确认指纹");
        } else {
            BaseApplication.getOffline().stop();
            BaseApplication.getOffline().play(getResources().getString(R.string.finger_speech));

            if (!hasBack) {
                mBackImg.setVisibility(View.GONE);
            }
            if (fingerPrints == 0) {
                mSkipBt.setVisibility(View.VISIBLE);
            }
            mCommitBt.setText("完成");
        }
    }

    private void initFingerApi() {
        if (!SerialPortManager.getInstance().isOpen()) {
            SerialPortManager.getInstance().openSerialPort();
        }
        if (mFingerApi == null)
            mFingerApi = new FingerprintAPI();
        mFingerApi.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
        register(null);
    }

    public void register(View view) {
        mFingerBmp = null;
        mCommitLayout.setVisibility(View.INVISIBLE);
        mFingerImg.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                int getImage = -1;
                while (getImage != 0x00 && isRunning) {
                    getImage = mFingerApi.PSGetImage();
                    Log.i("getImage==", getImage + "");
                }
                byte[] imgData = mFingerApi.PSUpImage();
                if (imgData != null && imgData.length > 0) {
                    showImg(imgData);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(FingerCollectActivity.this, "采集失败", Toast.LENGTH_SHORT).show();
                            cancelAnimatorSet();
                            setErrorPrompt();
                            startAnimation(linear_prompt);
                        }
                    });
                }
            }
        }.start();
    }

    public void commitSign(View view) {
        if (mFingerBmp == null)
            return;
        if (mParentInfo == null) {
//            Bundle bundle = new Bundle();
//            bundle.putByteArray("bmp", BitmapUtils.Bitmap2Bytes(mFingerBmp));
            Intent intent = new Intent();
//            intent.putExtras(bundle);
            intent.putExtra("fingerBmp", BitmapUtils.Bitmap2Bytes(mFingerBmp));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            BitmapCache.setParentFingerBmp(mFingerBmp);
            UploadUtil.uploadParentInfo(this, mParentInfo.getChildCode(), mParentInfo.getChildName());
        }
    }

    private void showImg(byte[] imgData) {
        final Bitmap image = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
        int w = image.getWidth(), h = image.getHeight();

        int[] imgSrc = new int[w * h];
        image.getPixels(imgSrc, 0, w, 0, 0, w, h);
        int[] imgDstSize = new int[2];
        int[] imgDst = BitmapUtils.removeBackground(imgSrc, new int[]{w, h}, imgDstSize);
        if (imgDst == null || imgDst.length == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(FingerCollectActivity.this, "指纹不完整，请重试", Toast.LENGTH_SHORT).show();
                    cancelAnimatorSet();
                    setErrorPrompt();
                    startAnimation(linear_prompt);
                    register(null);
                }
            });
            return;
        }
        mFingerBmp = Bitmap.createBitmap(imgDstSize[0], imgDstSize[1], Bitmap.Config.ARGB_8888);
        mFingerBmp.setPixels(imgDst, 0, imgDstSize[0], 0, 0, imgDstSize[0], imgDstSize[1]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCommitLayout.setVisibility(View.VISIBLE);
                mFingerImg.setImageBitmap(mFingerBmp);
                //Toast.makeText(FingerCollectActivity.this, "采集完成", Toast.LENGTH_SHORT).show();
                cancelAnimatorSet();
                setOkPrompt();
                startAnimation(linear_prompt);
            }
        });
    }


    /**
     * 跳过
     *
     * @param view
     */
    public void onSkip(View view) {
        if (mParentInfo == null) {
            Bundle bundle = new Bundle();
            bundle.putByteArray("bmp", BitmapUtils.Bitmap2Bytes(mFingerBmp));
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            BitmapCache.setParentFingerBmp(null);
            UploadUtil.uploadParentInfo(this, mParentInfo.getChildCode(), mParentInfo.getChildName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApplication.getOffline().stop();
        isRunning = false;
        SerialPortManager.getInstance().closeSerialPort();
    }


    public void startAnimation(final View view) {
        linear_prompt.setVisibility(View.VISIBLE);
        animatorSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleY", 0f, 2.0f, 1f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 2.0f, 1f);

        animatorSet.playTogether(anim1, anim2);
        animatorSet.setDuration(3000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (null != mFingerBmp) {
                    Intent intent = new Intent();
                    intent.putExtra("fingerBmp", BitmapUtils.Bitmap2Bytes(mFingerBmp));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    private void cancelAnimatorSet() {
        if (animatorSet != null)
            animatorSet.cancel();
    }

    private void setOkPrompt() {
        img_prompt.setImageResource(R.mipmap.ok);
        text_prompt.setText("采集完成");
    }

    private void setErrorPrompt() {
        img_prompt.setImageResource(R.mipmap.error);
        text_prompt.setText("采集失败,请重试");
    }


    private void initMessageLisener() {
        // 强制退出
        MessageLisenerRegister.registMessageLisener(MessageType.SERVER_SIGNATURE_CANCEL, new MessageLisener() {
            @Override
            public void onMessage(SocketMessage message) {
                Intent intent = new Intent(FingerCollectActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        });
    }

}
