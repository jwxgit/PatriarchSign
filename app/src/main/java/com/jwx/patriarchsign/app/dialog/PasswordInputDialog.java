package com.jwx.patriarchsign.app.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.activities.DeviceBindActivity;
import com.jwx.patriarchsign.app.activities.DoctorInfoActivity;
import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.app.application.ActivityManager;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.data.domain.InoculationTableInfo;
import com.jwx.patriarchsign.utils.UIUtil;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class PasswordInputDialog extends DialogFragment {
    private TablePickedCallback callback;
    private EditText            mPasswordEt;

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
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (UIUtil.getScreenWidth() * 0.7f),
                (int) (UIUtil.getScreenHeight() * 0.6f));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout mRootView = (LinearLayout) inflater.inflate(R.layout.dialog_password_layout, container);
        ImageView closeBt = (ImageView) mRootView.findViewById(R.id.close_bt);
        mPasswordEt = (EditText) mRootView.findViewById(R.id.password_et);
        Button commitBt = (Button) mRootView.findViewById(R.id.commit_bt);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pswStr = mPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(pswStr)) {
                    Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (Constances.PSW_TO_BIND.equals(pswStr)) {
                    ActivityManager.finishToTarget(getActivity(),DeviceBindActivity.class);
                }
                if (Constances.PSW_TO_DOCTORS.equals(pswStr)) {
                    ActivityManager.finishToTarget(getActivity(),DoctorInfoActivity.class);
                }
                if (Constances.PSW_TO_CONFIG.equals(pswStr)) {
                    ActivityManager.finishToTarget(getActivity(),IpConfigActivity.class);
                }
            }
        });
        return mRootView;
    }


    public void setTablePickedListener(TablePickedCallback callback) {
        this.callback = callback;
    }

    public interface TablePickedCallback {
        void onPicked(InoculationTableInfo tableInfo);
    }
}
