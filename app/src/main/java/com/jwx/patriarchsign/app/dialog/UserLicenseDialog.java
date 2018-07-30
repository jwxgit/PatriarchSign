package com.jwx.patriarchsign.app.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.data.protocols.CommentProtocol;
import com.jwx.patriarchsign.utils.UIUtil;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class UserLicenseDialog extends DialogFragment implements View.OnClickListener {


    private ImageView mCloseBtn;

    private TextView textView;

    private ProgressBar progressBar;

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
        getDialog().getWindow().setLayout((int) (UIUtil.getScreenWidth() * 0.9f),
                (int) (UIUtil.getScreenHeight() * 0.9f));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout mRootView = (LinearLayout) inflater.inflate(R.layout.dialog_user_license_layout, container);
        mCloseBtn = (ImageView) mRootView.findViewById(R.id.close_bt);
        textView = (TextView) mRootView.findViewById(R.id.textView);
        progressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mCloseBtn.setOnClickListener(this);

        CommentProtocol.getUserLicense(new CommentProtocol.UserLicenseResultCallback() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);
                textView.setText(result);
            }

            @Override
            public void onFailed() {

            }
        });

        return mRootView;
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
