package com.jwx.patriarchsign.app.holders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.activities.BaseActivity;
import com.jwx.patriarchsign.app.activities.DoctorInfoDetailActivity;
import com.jwx.patriarchsign.app.dialog.HoloDialogFragment;
import com.jwx.patriarchsign.data.domain.DoctorItemInfo;
import com.jwx.patriarchsign.data.protocols.CommentProtocol;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorInfoItemHolder extends BaseHolder<DoctorItemInfo> implements View.OnClickListener {

    @ViewInject(R.id.name)
    private TextView       mNameText;
    @ViewInject(R.id.state)
    private TextView       mStateText;
    @ViewInject(R.id.detail_bt)
    private Button         mDetailBt;
    @ViewInject(R.id.delete_bt)
    private Button         mDeleteBt;
    @ViewInject(R.id.insert_bt)
    private Button         mInsertBt;
    @ViewInject(R.id.progressBar)
    private ProgressBar    mProgressBar;
    private DoctorItemInfo mData;

    public DoctorInfoItemHolder(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        View itemView = View.inflate(context, R.layout.doctor_info_item_layout, null);
        x.view().inject(this, itemView);
        return itemView;
    }

    @Override
    protected void refreshUI(DoctorItemInfo data) {
        mData = data;
        mNameText.setText(data.getUsername());
        mProgressBar.setVisibility(View.GONE);
        switch (data.getState()) {
            case 0:
                mStateText.setText("已录入信息");
                mStateText.setTextColor(getContext().getResources().getColor(R.color.colorTextGray));
                mDeleteBt.setVisibility(View.VISIBLE);
                mDetailBt.setVisibility(View.VISIBLE);
                mInsertBt.setVisibility(View.GONE);
                break;
            case 1:
                mStateText.setText("信息不全");
                mStateText.setTextColor(getContext().getResources().getColor(R.color.colorPink));
                mDeleteBt.setVisibility(View.VISIBLE);
                mDetailBt.setVisibility(View.VISIBLE);
                mInsertBt.setVisibility(View.GONE);
                break;
            case 2:
                mStateText.setText("无");
                mStateText.setTextColor(getContext().getResources().getColor(R.color.colorPink));
                mDeleteBt.setVisibility(View.GONE);
                mDetailBt.setVisibility(View.GONE);
                mInsertBt.setVisibility(View.VISIBLE);
                break;
        }
        mDetailBt.setOnClickListener(this);
        mDeleteBt.setOnClickListener(this);
        mInsertBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_bt:
            case R.id.insert_bt:
                Intent intent = new Intent(getContext(), DoctorInfoDetailActivity.class);
                intent.putExtra("info", mData);
                getContext().startActivity(intent);
                break;
            case R.id.delete_bt:
                final HoloDialogFragment dialogFragment = new HoloDialogFragment();
                dialogFragment.setTitle("清除资料");
                dialogFragment.setMessage("确定清除 " + mData.getUsername() + " 的所有图片信息么？");
                dialogFragment.setPosBt("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearImgs();
                        dialogFragment.dismiss();
                    }
                });
                dialogFragment.setNegBt("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogFragment.dismiss();
                    }
                });
                dialogFragment.show(((BaseActivity) getContext()).getSupportFragmentManager(), "clear");
                break;
        }
    }

    private void clearImgs() {
        mDeleteBt.setVisibility(View.GONE);
        mDetailBt.setVisibility(View.GONE);
        mInsertBt.setVisibility(View.GONE);
        mProgressBar.setProgress(View.VISIBLE);
        CommentProtocol.clearUserImgs(mData.getUserId(), new CommentProtocol.ResultCallback() {
            @Override
            public void onSuccess() {
                mData.setState(2); //修改医生信息状态为“无”
                clearCallback.onClearSuccess(); //清除信息成功

                mStateText.setText("无");
                mStateText.setTextColor(getContext().getResources().getColor(R.color.colorPink));
                mDeleteBt.setVisibility(View.GONE);
                mDetailBt.setVisibility(View.GONE);
                mInsertBt.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(View.GONE);
            }

            @Override
            public void onFailed() {
                mDeleteBt.setVisibility(View.VISIBLE);
                mDetailBt.setVisibility(View.VISIBLE);
                mInsertBt.setVisibility(View.GONE);
                mProgressBar.setProgress(View.GONE);
                Toast.makeText(getContext(), "清除失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private ClearCallback clearCallback;

    public void setClearCallback(ClearCallback clearCallback) {
        this.clearCallback = clearCallback;
    }

    /**
     * 清除信息成功的回调接口
     */
    public interface ClearCallback {
        void onClearSuccess();
    }
}
