package com.jwx.patriarchsign.app.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.data.domain.DoctorItemInfo;
import com.jwx.patriarchsign.data.protocols.BaseProtocol;
import com.jwx.patriarchsign.data.protocols.DoctorDetailProtocol;
import com.jwx.patriarchsign.data.protocols.CommentProtocol;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.GlideUtils;
import com.jwx.patriarchsign.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorInfoDetailActivity extends BaseActivity {

    private static final int CODE_IMG_FACE   = 0;
    private static final int CODE_IMG_SIGN   = 1;
    private static final int CODE_IMG_FINGER = 2;
    @ViewInject(R.id.face_img)
    private ImageView      mFaceImg;
    @ViewInject(R.id.title)
    private TextView       mTtle;
    @ViewInject(R.id.sign_img)
    private ImageView      mSignImg;
    @ViewInject(R.id.finger_img)
    private ImageView      mFingerImg;
    @ViewInject(R.id.bt_change_photo)
    private Button         mChangeFaceBt;
    @ViewInject(R.id.bt_change_sign)
    private Button         mChangeSignBt;
    @ViewInject(R.id.bt_change_finger)
    private Button         mChangeFingerBt;
    @ViewInject(R.id.face_loading_layout)
    private View           mFaceLoadingLayout;
    @ViewInject(R.id.face_retry_bt)
    private View           mFaceRetryBt;
    @ViewInject(R.id.sign_loading_layout)
    private View           mSignLoadingLayout;
    @ViewInject(R.id.sign_retry_bt)
    private View           mSignRetryBt;
    @ViewInject(R.id.finger_loading_layout)
    private View           mFingerLoadingLayout;
    @ViewInject(R.id.finger_retry_bt)
    private View           mFingerRetryBt;
    private DoctorItemInfo mDoctorInfo;
    private Bitmap         mFaceBmp;
    private Bitmap         mSignBmp;
    private Bitmap         mFingerBmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mDoctorInfo = (DoctorItemInfo) getIntent().getSerializableExtra("info");
        mTtle.setText(mDoctorInfo.getUsername() + "的个人信息");
        if (mDoctorInfo != null && mDoctorInfo.getState() != 2) {
            new DoctorDetailProtocol((mDoctorInfo).getUserId())
                    .getDataFromNet(new BaseProtocol.LoadCallback<DoctorItemInfo>() {
                        @Override
                        public void onSuccess(DoctorItemInfo data) {
                            loadPictures(data);
                        }

                        @Override
                        public void onFailed(Throwable e, String errorCode) {
                            loadPictures(null);
                        }
                    });
        } else {
            mFaceImg.setImageResource(R.mipmap.face_loading_holder);
            mFingerImg.setImageResource(R.mipmap.finger_loading_holder);
            mSignImg.setImageResource(R.mipmap.sign_loading_holder);
            mChangeFaceBt.setText("录入照片");
            mChangeSignBt.setText("录入签名");
            mChangeFingerBt.setText("录入指纹");
        }
    }

    private void loadPictures(DoctorItemInfo data) {
        if (data == null) {
            mFaceImg.setImageResource(R.mipmap.ic_loading);
            mFingerImg.setImageResource(R.mipmap.ic_loading);
            mSignImg.setImageResource(R.mipmap.ic_loading);
            return;
        }
        String picPath = Interface.getFullUrl(Interface.URL_SHOW_IMAGE) + data.getPicPath();
        String fingerprintsPath = Interface.getFullUrl(Interface.URL_SHOW_IMAGE) + data.getFingerprintsPath();
        String signaturePath = Interface.getFullUrl(Interface.URL_SHOW_IMAGE) + data.getSignaturePath();
        if (TextUtils.isEmpty(picPath)) {
//            mFaceImg.setImageResource(R.drawable.doctor_img_holder);
            GlideUtils.loadImage(R.mipmap.face_loading_holder, mFaceImg);
            mChangeFaceBt.setText("录入照片");
        } else {
            GlideUtils.loadImage(picPath, mFaceImg, true, R.mipmap.face_loading_holder, R.mipmap.face_error_holder);
            mChangeFaceBt.setText("修改照片");
        }
        if (TextUtils.isEmpty(fingerprintsPath)) {
            GlideUtils.loadImage(R.drawable.finger_place_holder, mFingerImg);
//            mFingerImg.setImageResource(R.drawable.finger_place_holder);
            mChangeFingerBt.setText("录入指纹");
        } else {
            GlideUtils.loadImage(fingerprintsPath, mFingerImg, false, R.mipmap.finger_loading_holder, R.mipmap.sign_error_holder);
            mChangeFingerBt.setText("修改指纹");
        }
        if (TextUtils.isEmpty(signaturePath)) {
            GlideUtils.loadImage(R.drawable.sign_place_holder, mSignImg);
//            mSignImg.setImageResource(R.drawable.sign_place_holder);
            mChangeSignBt.setText("录入签名");
        } else {
            GlideUtils.loadImage(signaturePath, mSignImg, false, R.mipmap.sign_loading_holder, R.mipmap.sign_error_holder);
            mChangeSignBt.setText("修改签名");
        }
    }

    private void initView() {
        setContentView(R.layout.activity_doctor_detail);
        x.view().inject(this);
    }

    public void changePhoto(View view) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                if (requestCode == 200) {
                    Intent intent = new Intent(DoctorInfoDetailActivity.this, FaceCollectionActivity.class);
                    startActivityForResult(intent, CODE_IMG_FACE);
//                    Intent intent = new Intent(DoctorInfoDetailActivity.this, TestCameraActivity.class);
//                    startActivityForResult(intent, CODE_IMG_FACE);
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

    public void changeSign(View view) {
        Intent intent = new Intent(this, SignCollectActivity.class);
        startActivityForResult(intent, CODE_IMG_SIGN);
    }

    public void changeFinger(View view) {
        Intent intent = new Intent(this, FingerCollectActivity.class);
        startActivityForResult(intent, CODE_IMG_FINGER);
    }

    public void faceRetry(View view) {
        mFaceLoadingLayout.setVisibility(View.VISIBLE);
        mFaceRetryBt.setVisibility(View.GONE);
        uploadImg(mFaceBmp, 3);
    }

    public void signRetry(View view) {
        mSignLoadingLayout.setVisibility(View.VISIBLE);
        mSignRetryBt.setVisibility(View.GONE);
        uploadImg(mSignBmp, 2);
    }

    public void fingerRetry(View view) {
        mFingerLoadingLayout.setVisibility(View.VISIBLE);
        mFingerRetryBt.setVisibility(View.GONE);
        uploadImg(mFingerBmp, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_IMG_FACE:
                    mFaceBmp = BitmapUtils.getBitmapFromBytes(data.getByteArrayExtra("bmp"), null);
                    mFaceImg.setImageBitmap(mFaceBmp);
                    mChangeFaceBt.setText("修改照片");
                    mFaceLoadingLayout.setVisibility(View.VISIBLE);
                    uploadImg(mFaceBmp, 3);
                    break;
                case CODE_IMG_SIGN:
                    mSignBmp = BitmapUtils.getBitmapFromBytes(data.getByteArrayExtra("bmp"), null);
                    mSignImg.setImageBitmap(mSignBmp);
                    mChangeSignBt.setText("修改签名");
                    mSignLoadingLayout.setVisibility(View.VISIBLE);
                    uploadImg(mSignBmp, 2);
                    break;
                case CODE_IMG_FINGER:
                    mFingerBmp = BitmapUtils.getBitmapFromBytes(data.getByteArrayExtra("bmp"), null);
                    mFingerImg.setImageBitmap(mFingerBmp);
                    mChangeFingerBt.setText("修改指纹");
                    mFingerLoadingLayout.setVisibility(View.VISIBLE);
                    uploadImg(mFingerBmp, 1);
                    break;
            }
        }
    }

    private void uploadImg(Bitmap faceBmp, final int imgType) {
        CommentProtocol.uploadDoctorInfo(faceBmp, mDoctorInfo.getUserId(), imgType, new CommentProtocol.ResultCallback() {
            @Override
            public void onSuccess() {
                switch (imgType) {
                    case 1:
                        mFingerLoadingLayout.setVisibility(View.GONE);
                        mFingerRetryBt.setVisibility(View.GONE);
                        break;
                    case 2:
                        mSignLoadingLayout.setVisibility(View.GONE);
                        mSignRetryBt.setVisibility(View.GONE);
                        break;
                    case 3:
                        mFaceLoadingLayout.setVisibility(View.GONE);
                        mFaceRetryBt.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onFailed() {
                switch (imgType) {
                    case 1:
                        mFingerLoadingLayout.setVisibility(View.GONE);
                        mFingerRetryBt.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mSignLoadingLayout.setVisibility(View.GONE);
                        mSignRetryBt.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mFaceLoadingLayout.setVisibility(View.GONE);
                        mFaceRetryBt.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}
