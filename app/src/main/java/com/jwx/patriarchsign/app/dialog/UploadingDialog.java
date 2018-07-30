package com.jwx.patriarchsign.app.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.activities.IndexActivity;
import com.jwx.patriarchsign.app.application.ActivityManager;
import com.jwx.patriarchsign.utils.UIUtil;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class UploadingDialog extends DialogFragment implements View.OnClickListener {

    private View               mProgressBar;
    private TextView           mTitle;
    private View               mRetryBt;
    private View               mCloseBt;
    private View               mSucceedLayout;
    private RetryClickListener mRetryClickListener;
    private Handler mHandler = new Handler();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
//        lp.windowAnimations = R.style.BottomToTopAnim;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (UIUtil.getScreenWidth() * 0.6f),
                (int) (UIUtil.getScreenHeight() * 0.4f));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout mRootView = (LinearLayout) inflater.inflate(R.layout.dialog_uploading_layout, container);
        mTitle = (TextView) mRootView.findViewById(R.id.title);
        mProgressBar = mRootView.findViewById(R.id.progressBar);
        mRetryBt = mRootView.findViewById(R.id.btn_retry);
        mCloseBt = mRootView.findViewById(R.id.close_bt);
        mSucceedLayout = mRootView.findViewById(R.id.succeed_layout);
        mRetryBt.setOnClickListener(this);
        mCloseBt.setOnClickListener(this);
        return mRootView;
    }

    public void loadStart() {
        mCloseBt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText("信息上传中，请耐心等待！");
        mSucceedLayout.setVisibility(View.GONE);
        mRetryBt.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void onLoadSucceed() {
        mCloseBt.setVisibility(View.GONE);
        mTitle.setVisibility(View.INVISIBLE);
        mSucceedLayout.setVisibility(View.VISIBLE);
        mRetryBt.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
                ActivityManager.finishToTarget(getActivity(), IndexActivity.class);
            }
        }, 2000);

    }

    public void onLoadFailed() {
        mTitle.setVisibility(View.INVISIBLE);
        mSucceedLayout.setVisibility(View.GONE);
        mRetryBt.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mCloseBt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retry:
                if (mRetryClickListener != null)
                    mRetryClickListener.onRetryClick();
                break;
            case R.id.close_bt:
                dismiss();
                break;
        }
    }

    public void setRetryClickListener(RetryClickListener listener) {
        mRetryClickListener = listener;
    }

    public interface RetryClickListener {
        void onRetryClick();
    }
}
