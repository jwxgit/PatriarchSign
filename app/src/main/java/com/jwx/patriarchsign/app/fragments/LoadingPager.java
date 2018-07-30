package com.jwx.patriarchsign.app.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.utils.UIUtil;

public abstract class LoadingPager<T> extends FrameLayout implements OnClickListener {
    private static final int STATE_NONE         = -1;            // 无状态
    private static final int STATE_LOADING      = 0;            // 加载中的状态
    private static final int STATE_EMPTY        = 1;            // 空的状态
    private static final int STATE_ERROR        = 2;            // 错误的状态
    private static final int STATE_SUCCESS      = 3;            // 成功的状态
    private              int mCurrentState      = STATE_NONE;    // 用来标记当前属于什么状态
    private              int mCurrentPagerIndex = 1;
    private View    mLoadingView;                    // 正在加载中的View
    private View    mEmptyView;                    // 数据为空的View
    private View    mErrorView;                    // 错误页面的View
    private View    mSuccessView;                    // 成功的view
    private View    mBtnRetry;
    private boolean disallowFadeTranslation;
    private View    mAnimView;


    public LoadingPager(Context context) {

        this(context, null);
    }

    public LoadingPager(Context context, AttributeSet attrs) {

        super(context, attrs);
        initView();
    }

    /**
     * 设置空页面展示内容
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {

        if (emptyView != null) {
            removeView(mEmptyView);
            mEmptyView = emptyView;
            mEmptyView.setVisibility(View.GONE);
            addView(mEmptyView);
        }
    }

    private void initView() {
        // 2. 加载数据为空
        if (mEmptyView == null) {
            // 初始化
            mEmptyView = View.inflate(getContext(), R.layout.pager_empty, null);
            mEmptyView.setVisibility(GONE);
            addView(mEmptyView);
        }
        // 3. 加载失败
        if (mErrorView == null) {
            // 初始化
            mErrorView = View.inflate(getContext(), R.layout.pager_error, null);
            mErrorView.setVisibility(GONE);
            addView(mErrorView);
            mBtnRetry = mErrorView.findViewById(R.id.error_btn_retry);
            mBtnRetry.setOnClickListener(this);
        }
        // 1. 加载数据
        if (mLoadingView == null) {
            // 初始化
            mLoadingView = View.inflate(getContext(), R.layout.pager_loading, null);
            mLoadingView.setVisibility(GONE);
            mAnimView = mLoadingView.findViewById(R.id.anim_view);
            addView(mLoadingView);
        }
        // 根据状态显示view
        safeUpdateUIStyle();
    }


    private void safeUpdateUIStyle() {
        safeUpdateUIStyle(null, null);
    }

    private void safeUpdateUIStyle(final T data, final Throwable ex) {

        UIUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateUIStyle(data, ex);
            }
        });
    }

    /**
     * View 显示或隐藏的动画
     */
    private void showOrHideView(final View view, boolean show) {
        if (view == null)
            return;

        AlphaAnimation mAlphaInAnim = new AlphaAnimation(0f, 1f);
        mAlphaInAnim.setDuration(200);
        AlphaAnimation mAlphaOutAnim = new AlphaAnimation(1f, 0f);
        mAlphaOutAnim.setDuration(200);
        mAlphaOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(GONE);
                System.out.println(view + " GONE ");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (show) {
            if (view.getVisibility() == VISIBLE) {
                return;
            } else {
                view.setVisibility(VISIBLE);
            }
            view.startAnimation(mAlphaInAnim);
        } else {
            if (view.getVisibility() == GONE)
                return;
            view.startAnimation(mAlphaOutAnim);
        }
    }


    private void updateUIStyle(T data, Throwable ex) {

        switch (mCurrentState) {
            case STATE_LOADING:
//                if (mSuccessView != null)
//                    mSuccessView.setVisibility(VISIBLE);
//                else {
                if (mSuccessView == null) {
                    mLoadingView.setVisibility(VISIBLE);
                }
                mEmptyView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
//                }

                break;
            case STATE_EMPTY:
                mErrorView.setVisibility(GONE);
                showOrHideView(mLoadingView, false);
                showOrHideView(mEmptyView, true);
                break;
            case STATE_ERROR:
                mEmptyView.setVisibility(GONE);
                showOrHideView(mLoadingView, false);
                showOrHideView(mErrorView, true);
                break;
            case STATE_SUCCESS:
                if (mSuccessView == null) {
                    mSuccessView = onCreateSuccessView(data);
                    if (mSuccessView == null)
                        return;
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    mSuccessView.setVisibility(GONE);
                    addView(mSuccessView, params);
                }
                showOrHideView(mLoadingView, false);
                showOrHideView(mSuccessView, true);
                mEmptyView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                break;
            default:
                break;
        }

//        // 1.loading
//        if (mLoadingView != null) {
//            mLoadingView.setVisibility(mCurrentState == STATE_LOADING || mCurrentState == STATE_NONE ? View.VISIBLE :
//                    View.GONE);
//        }
//        // 2. empty
//        if (mEmptyView != null) {
//            mEmptyView.setVisibility(mCurrentState == STATE_EMPTY ? View.VISIBLE : View.GONE);
//        }
//
//        // 3. error
//        if (mErrorView != null) {
//            mErrorView.setVisibility(mCurrentState == STATE_ERROR ? View.VISIBLE : View.GONE);
//        }
//        // 4. 成功
//        if (mSuccessView == null && mCurrentState == STATE_SUCCESS) {
//            // 初始化View
//            mSuccessView = onCreateSuccessView();
//            if (mSuccessView == null)
//                return;
//            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//            // add
////            removeAllViews();
//            if (!disallowFadeTranslation) {
//                //允许过渡动画
////                if (mContext instanceof BaseActivity && ((BaseActivity) mContext).isAnimationCompeleted()) {
//                //Activity的入场动画播放完成之后才进行过渡动画
//                AlphaAnimation aa = new AlphaAnimation(0f, 1f);
//                aa.setDuration(300);
//                mSuccessView.setAnimation(aa);
////                }
//            }
//            addView(mSuccessView, params);
//        }
//        if (mSuccessView != null) {
//            mSuccessView.setVisibility(mCurrentState == STATE_SUCCESS ? View.VISIBLE : View.GONE);
//        }
    }

    /**
     * 数据加载回调
     */
    private LoadResultCallback mLoadResultCallback = new LoadResultCallback<T>() {

        @Override
        public void onError(Throwable ex) {
            mCurrentState = STATE_ERROR;
            safeUpdateUIStyle(null, ex);
            ex.printStackTrace();
        }

        @Override
        public void onCompleted(T data) {
            mCurrentState = STATE_SUCCESS;
            safeUpdateUIStyle(data, null);
        }

        @Override
        public void onEmpty() {
            mCurrentState = STATE_EMPTY;
            safeUpdateUIStyle();
        }
    };


    /**
     * 加载数据的方法
     */
    public void loadData() {

        if (mCurrentState == STATE_EMPTY || mCurrentState == STATE_ERROR || mCurrentState == STATE_NONE) {

//            ThreadManager.getLongRunPool().execute(new Runnable() {
//                @Override
//                public void run() {
//                    onStartLoadData(mLoadResultCallback);
//                }
//            });
            mCurrentState = STATE_LOADING;
            onStartLoadData(1, mLoadResultCallback);

        }
        safeUpdateUIStyle();
    }

    public void refresh(final RefreshCallback<T> callback) {
        onStartLoadData(1, new LoadResultCallback<T>() {
            @Override
            public void onError(Throwable ex) {
                callback.onResult(false, null);
            }

            @Override
            public void onCompleted(T data) {
                callback.onResult(true, data);
                mCurrentPagerIndex = 1;
            }

            @Override
            public void onEmpty() {
                callback.onResult(true, null);
            }
        });
    }

    public void loadMore(final RefreshCallback<T> callback) {
        onStartLoadData(mCurrentPagerIndex + 1, new LoadResultCallback<T>() {
            @Override
            public void onError(Throwable ex) {
                callback.onResult(false, null);
            }

            @Override
            public void onCompleted(T data) {
                callback.onResult(true, data);
                mCurrentPagerIndex++;
            }

            @Override
            public void onEmpty() {
                callback.onResult(true, null);
            }
        });
    }

    /**
     * 是否允许过渡动画
     *
     * @param b
     */
    public void disallowFadeTranslation(boolean b) {
        disallowFadeTranslation = b;
    }


    /**
     * 加载的结果
     */
    public enum LoadedResult {
        EMPTY(STATE_EMPTY), ERROR(STATE_ERROR), SUCCESS(STATE_SUCCESS);
        int state;

        private LoadedResult(int state) {

            this.state = state;
        }

        public int getState() {

            return state;
        }
    }

    protected abstract View onCreateSuccessView(T data);

    protected abstract void onStartLoadData(int pagerIndex, LoadResultCallback<T> result);

    @Override
    public void onClick(View v) {

        // 重新加载数据
        loadData();
    }

    public interface LoadResultCallback<I> {

        void onError(Throwable ex);

        void onCompleted(I data);

        void onEmpty();
    }

    public interface RefreshCallback<I> {
        void onResult(boolean isSucceed, I data);
    }
}
