package com.jwx.patriarchsign.app.holders;

import android.content.Context;
import android.view.View;

public abstract class BaseHolder<T> {
    private final Context mContext;
    private       View    mRootView;
    T mData;
    private boolean isGroupTitle;//是不是索引的第一个 即显不显示索引标题
    private boolean isGroupLast;//是不是分组的最后一个
    private boolean isFirst;//是不是第一条
    private boolean isLast;//是不是最后一条
    private boolean hasSetData;

    public BaseHolder(Context context) {
        mContext = context;
        mRootView = initView(context);
        // 打标记
        mRootView.setTag(this);
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isGroupTitle() {
        return isGroupTitle;
    }

    public void setGroupTitle(boolean groupTitle) {
        isGroupTitle = groupTitle;
    }

    public boolean isGroupLast() {
        return isGroupLast;
    }

    public void setGroupLast(boolean groupLast) {
        isGroupLast = groupLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(T data) {
        hasSetData = true;//标记已经设置过数据
        this.mData = data;
        refreshUI(mData);
    }

    public T getData() {

        return mData;
    }

    /**
     * 获取View
     *
     * @return
     */
    public View getRootView() {
        if (!hasSetData) {
            refreshUI(null);
        }
        return mRootView;
    }

    public View getNoneDataView() {
        return mRootView;
    }


    /**
     * 释放资源
     */
    public void release() {
    }

    protected abstract View initView(Context context);

    protected abstract void refreshUI(T data);
}
