package com.jwx.patriarchsign.app.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.BaseApplication;
import com.jwx.patriarchsign.data.cache.BitmapCache;
import com.jwx.patriarchsign.data.domain.ParentInfo;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.UploadUtil;
import com.pingan.paeauth.config.UserConfig;
import com.pingan.paeauth.entity.PAFaceDetectorFrame;
import com.pingan.paeauth.mananger.IPaFaceDetector;
import com.pingan.paeauth.mananger.PaFaceDetectorManager;
import com.pingan.paeauth.widget.PaDtcSurfaceView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class FaceCollectionActivity extends BaseActivity implements IPaFaceDetector {

    @ViewInject(R.id.surface_view)
    private PaDtcSurfaceView            mSurfaceView;
    @ViewInject(R.id.result_iv)
    private ImageView                   mResultImageView;
    @ViewInject(R.id.front_top)
    private View                        mFrontTopView;
    @ViewInject(R.id.front_bottom)
    private View                        mFrontBottomView;
    @ViewInject(R.id.commit_layout)
    private View                        mCommitLayout;
    @ViewInject(R.id.commit_bt)
    private Button                      mCommitBt;
    @ViewInject(R.id.skip_bt)
    private Button                      mSkipBt;
    @ViewInject(R.id.back_img)
    private ImageView                   mBackImg;
    private PaFaceDetectorManager       mDetectorManager;
    private RelativeLayout.LayoutParams mTopParams;
    private RelativeLayout.LayoutParams mBottomParams;
    private Bitmap                      mFaceBmp;
    private ParentInfo                  mParentInfo; //家长签核信息
//    private boolean                     mNeedFinger; //是否需要指纹
//    private boolean                     mNeedSign;//是否需要签名

    private int     pic;
    private int     signature;
    private int     fingerPrints;
    private boolean hasBack; //是否有返回键

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDetectorManager != null)
            mDetectorManager.onResume();
        mSurfaceView.setVisibility(View.VISIBLE);
        mResultImageView.setVisibility(View.GONE);
    }

    private void initView() {
        setContentView(R.layout.activity_face_collect);
        x.view().inject(this);
        mTopParams = (RelativeLayout.LayoutParams) mFrontTopView.getLayoutParams();
        mBottomParams = (RelativeLayout.LayoutParams) mFrontBottomView.getLayoutParams();
        mParentInfo = (ParentInfo) getIntent().getSerializableExtra("parentInfo");
        pic = getIntent().getIntExtra("pic", 0);
        signature = getIntent().getIntExtra("signature", 0);
        fingerPrints = getIntent().getIntExtra("fingerPrints", 0);
        hasBack = getIntent().getBooleanExtra("hasBack", true);
        if (mParentInfo != null) {
            //播放语音
            BaseApplication.getOffline().stop();
            BaseApplication.getOffline().play(getResources().getString(R.string.pic_speech));

            if (!hasBack) {
                mBackImg.setVisibility(View.GONE);
            }

            if (pic == 0) {
                mSkipBt.setVisibility(View.VISIBLE);
            }
            if (signature >= 0 || fingerPrints >= 0) {
                mCommitBt.setText("下一步");
            } else {
                mCommitBt.setText("完成");
            }
        } else {
            mCommitBt.setText("确认照片");
        }
    }

    private void initCamera() {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    mDetectorManager = new PaFaceDetectorManager(FaceCollectionActivity.this, mSurfaceView);

                    mDetectorManager.setCameraMode(Camera.CameraInfo.CAMERA_FACING_FRONT);//可设置使用前置或后置
                    mDetectorManager.setPreviewMode(UserConfig.PREVIEW_FULL_SCREEN);//可设置全屏或非全屏
                    mDetectorManager.setDetectModes(UserConfig.LDM_NORMAL_FACE);//设置检测模式
                    mDetectorManager.setOnFaceDetectorListener(FaceCollectionActivity.this);
                    mDetectorManager.onResume();
                }
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                // Failure.
                if (requestCode == 200) {
                    finish();
                }
            }
        };
        AndPermission.with(this).requestCode(200).permission(Manifest.permission.CAMERA).callback(listener).start();
    }

    @Override
    public void detecting(int i) {

    }

    @Override
    public void detectSuccess(PAFaceDetectorFrame paFaceDetectorFrame) {
        Bitmap tmp = paFaceDetectorFrame.getLivnessBitmap();
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        mFaceBmp = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, true);
        pictureAnim(mFaceBmp);
    }

    @Override
    public void detectFailed(int i) {

    }

    private void pictureAnim(final Bitmap resultBmp) {
        ValueAnimator va1 = ValueAnimator.ofFloat(0, mSurfaceView.getLayoutParams().height / 2f);
        final ValueAnimator va2 = ValueAnimator.ofFloat(mSurfaceView.getLayoutParams().height / 2f, 0);
        va1.setDuration(100);
        va2.setDuration(100);
        va1.setInterpolator(new LinearInterpolator());
        va2.setInterpolator(new LinearInterpolator());
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mTopParams.height = (int) value;
                mBottomParams.height = (int) value;
                mFrontTopView.requestLayout();
                mFrontBottomView.requestLayout();
            }
        };
        va1.addUpdateListener(updateListener);
        va2.addUpdateListener(updateListener);
        va1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDetectorManager.onPause();
                mResultImageView.setVisibility(View.VISIBLE);
                mResultImageView.setImageBitmap(resultBmp);
                mCommitLayout.setVisibility(View.VISIBLE);
                mSurfaceView.setVisibility(View.INVISIBLE);
                va2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va1.start();
    }

    public void retakePic(View view) {
        mDetectorManager.onResume();
        mDetectorManager.reStartDetector();
        mResultImageView.setVisibility(View.GONE);
        mCommitLayout.setVisibility(View.INVISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
    }

    public void commitImg(View view) {
        if (mParentInfo == null) {
            Bundle bundle = new Bundle();
            bundle.putByteArray("bmp", BitmapUtils.Bitmap2Bytes(mFaceBmp, Bitmap.CompressFormat.JPEG, 50));
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            BitmapCache.setParentFaceBmp(mFaceBmp);
            Intent intent = null;

            if (signature >= 0) {
                intent = new Intent(this, SignCollectActivity.class);
                intent.putExtra("signature", signature);
                intent.putExtra("fingerPrints", fingerPrints);
            } else {
                LogUtils.i("签字无");
                if (fingerPrints >= 0) {
                    intent = new Intent(this, FingerCollectActivity.class);
                    intent.putExtra("fingerPrints", fingerPrints);
                } else {
                    LogUtils.i("签字无 指纹采集无");
                }
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
        mFaceBmp = null;
        commitImg(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止播放语音
        BaseApplication.getOffline().stop();
//        if (mDetectorManager != null)
//            mDetectorManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetectorManager != null)
            mDetectorManager.onDestory();
    }
}
