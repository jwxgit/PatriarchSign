package com.jwx.patriarchsign.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public abstract class BaseFragment<T> extends Fragment {
    private LoadingPager<T> mPager;
    private boolean         disallowFadeTranslation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (skipLoad()) {
            return onSuccessView(null);
        } else {
            if (mPager == null) {
                mPager = new LoadingPager<T>(getActivity()) {

                    @Override
                    protected View onCreateSuccessView(T data) {
                        if (getActivity() == null)
                            return null;
                        return onSuccessView(data);
                    }

                    @Override
                    protected void onStartLoadData(int pagerIndex, LoadResultCallback<T> resultCallback) {
                        onLoadData(pagerIndex, resultCallback);
                    }
                };
            } else {
                ViewParent parent = mPager.getParent();
                if (parent != null && parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(mPager);
                }
            }
            mPager.setEmptyView(onEmptyPager());
            mPager.disallowFadeTranslation(disallowFadeTranslation);
            mPager.loadData();
            return mPager;
        }

    }

    public View onEmptyPager() {

        return null;
    }

    /**
     * 是否跳过加载 即直接显示布局
     *
     * @return
     */
    protected boolean skipLoad() {
        return false;
    }

    // 拿数据的行为
    public void loadData() {

        if (mPager != null) {
            mPager.loadData();
        }
    }

    public void refresh() {
        if (mPager != null) {
            mPager.refresh(new LoadingPager.RefreshCallback<T>() {
                @Override
                public void onResult(boolean isSucceed, T data) {
                    onRefresh(isSucceed, data);
                }
            });
        }
    }

    public void loadMore() {
        if (mPager != null) {
            mPager.loadMore(new LoadingPager.RefreshCallback<T>() {
                @Override
                public void onResult(boolean isSucceed, T data) {
                    onLoadmore(isSucceed, data);
                }
            });
        }
    }

    public void release() {

    }

    public FragmentActivity getFragmentActivity() {

        return getActivity();
    }

    public void disallowFadeTranslation(boolean b) {
        disallowFadeTranslation = b;
    }


    /**
     * 刷新回调
     *
     * @param isSucceed
     * @param data
     */
    protected void onRefresh(boolean isSucceed, T data) {

    }

    /**
     * 加载更多回调
     *
     * @param isSucceed
     * @param data
     */
    protected void onLoadmore(boolean isSucceed, T data) {

    }

    // 子类去实现加载成功的View
    public abstract View onSuccessView(T data);

    // 子类去实现加载的结果
    public abstract void onLoadData(int pagerIndex, LoadingPager.LoadResultCallback<T> resultCallback);

}
