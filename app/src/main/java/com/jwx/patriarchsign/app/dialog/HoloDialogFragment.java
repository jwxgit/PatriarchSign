package com.jwx.patriarchsign.app.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.utils.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caihua on 2017/1/4.
 * Dec:
 */

public class HoloDialogFragment extends DialogFragment {


    private Map<String, View.OnClickListener> mItemMap;
    private List<String>                      items;
    private String mTitleStr = "";
    private String mMessage;
    private String mPosStr, mNegStr;
    private View.OnClickListener mPosListener, mNegListener;
    private ImageView mImg;
    private int mItemGravirty = Gravity.LEFT | Gravity.CENTER_VERTICAL;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = UIUtil.getScreenWidth() / 2;
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(UIUtil.getScreenWidth() - UIUtil.dip2px(100),
                getDialog().getWindow().getAttributes().height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout mRootView = (LinearLayout) inflater.inflate(R.layout.dialog_layout, container);
        TextView titleText = (TextView) mRootView.findViewById(R.id.title);
        TextView messageText = (TextView) mRootView.findViewById(R.id.message);
        TextView posBt = (TextView) mRootView.findViewById(R.id.pos_bt);
        TextView negBt = (TextView) mRootView.findViewById(R.id.neg_bt);
        LinearLayout itemContainer = (LinearLayout) mRootView.findViewById(R.id.item_container);

        if (!TextUtils.isEmpty(mTitleStr)) {
            titleText.setText(mTitleStr);
        } else {
            titleText.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mMessage)) {
            messageText.setText(mMessage);
        } else {
            messageText.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mPosStr)) {
            posBt.setVisibility(View.VISIBLE);
            posBt.setText(mPosStr);
            posBt.setOnClickListener(mPosListener);
        }
        if (!TextUtils.isEmpty(mNegStr)) {
            negBt.setVisibility(View.VISIBLE);
            negBt.setText(mNegStr);
            negBt.setOnClickListener(mNegListener);
        }


        if (mImg != null) {
            itemContainer.addView(mImg);
        }

        if (items != null && items.size() > 0) {
            for (String item : items) {
                View itemView = inflater.inflate(R.layout.dialog_bt_item_layout, null);
                TextView itemText = (TextView) itemView.findViewById(R.id.item);
                itemText.setGravity(mItemGravirty);
                itemText.setText(item);
                itemView.setOnClickListener(mItemMap.get(item));
                itemContainer.addView(itemView);
            }
        }
        return mRootView;
    }

    public void setTitle(String title) {
        mTitleStr = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void addTextItem(String itemText, View.OnClickListener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (mItemMap == null) {
            mItemMap = new HashMap<>();
        }
        items.add(itemText);
        mItemMap.put(itemText, listener);
    }

    public void setItemGravirty(int gravirty) {
        mItemGravirty = gravirty | Gravity.CENTER_VERTICAL;
    }

    public void setNegBt(String str, View.OnClickListener listener) {
        mNegStr = str;
        mNegListener = listener;
    }

    public void setPosBt(String str, View.OnClickListener listener) {
        mPosStr = str;
        mPosListener = listener;
    }

    public void showImg(Bitmap bitmap) {
        mImg = new ImageView(UIUtil.getContext());
        mImg.setScaleType(ImageView.ScaleType.CENTER);
        mImg.setImageBitmap(bitmap);
    }
}
