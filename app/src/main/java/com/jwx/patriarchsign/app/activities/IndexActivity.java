package com.jwx.patriarchsign.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jwx.patriarchsign.R;
import com.jwx.patriarchsign.app.application.ActivityManager;
import com.jwx.patriarchsign.app.application.BaseApplication;
import com.jwx.patriarchsign.app.dialog.PasswordInputDialog;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.SpUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class IndexActivity extends BaseActivity {
    @ViewInject(R.id.web_view)
    private WebView mWebView;
    private int     mWorkBenchId;
    private int     mClickStep; //点击次数  达到5次弹出密码输入框
    private Handler mHandler = new Handler();
    private boolean mIsSigning;
//    private WebHeartBeatTask mHeartBeatTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_index);
        x.view().inject(this);
        mWorkBenchId = SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1);
        initWebView();
    }


    private void screen() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        LogUtils.i(width + "==" + height);
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        //支持缩放，默认为true。
        webSettings.setSupportZoom(false);
        //调整图片至适合webview的大小
        webSettings.setUseWideViewPort(true);
        // webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //设置默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //开启javascript
        webSettings.setJavaScriptEnabled(true);
        //关闭webview中缓存
        webSettings.setAppCacheEnabled(false);
        mWebView.addJavascriptInterface(new JSInterface(this), "android");
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(Interface.getBaseUrl() + Interface.URL_INDEX + "?workbenchId=" + mWorkBenchId);
//        mHeartBeatTask = new WebHeartBeatTask();
//        mHeartBeatTask.start();
    }

    public void showMenu(View view) {
        mClickStep++;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mClickStep = 0;
                System.out.println("reset");
            }
        }, 500);
        System.out.println(mClickStep);
        if (mClickStep == 3) {
            PasswordInputDialog dialog = new PasswordInputDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "password");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsSigning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsSigning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mHeartBeatTask.stop();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
            mWebView.goBack();
    }

    /**
     * 长连接心跳
     */
    class WebHeartBeatTask extends Handler implements Runnable {


        public void start() {
            stop();
            postDelayed(this, 1000 * 60);
        }

        public void stop() {
            removeCallbacksAndMessages(null);
        }

        @Override
        public void run() {
            if (mWebView != null) {
                mWebView.clearCache(true);
                mWebView.reload();
                postDelayed(this, 1000 * 60);
            }
        }
    }

    /**
     * js调用接口
     */
    public class JSInterface {
        private Activity mActivity;

        public JSInterface(Activity activity) {
            mActivity = activity;
        }

        /**
         * @param childCode    儿童编号
         * @param childName    儿童名称
         * @param fingerPrints 验证指纹
         * @param signature    签名
         * @param pic          照片
         *                     1 必填 0选题 -1无
         */
        @JavascriptInterface
        public void startSign(String childCode, String childName, int fingerPrints, int signature, int pic) {
            LogUtils.i("$$$$$$" + pic + "$$$$$" + signature + "$$$$$" + fingerPrints);
            // Toast.makeText(mActivity, "收到推送啰", Toast.LENGTH_SHORT).show();
            if (mIsSigning)
                return;

            Intent intent = new Intent(mActivity, UserLicenseActivity.class);
            intent.putExtra("childCode", childCode);
            intent.putExtra("childName", childName);
            intent.putExtra("fingerPrints", fingerPrints);
            intent.putExtra("signature", signature);
            intent.putExtra("pic", pic);
            startActivity(intent);
        }

        /**
         * 播放文字
         *
         * @param str
         */
        @JavascriptInterface
        public void playVoice(String str) {
            LogUtils.i("收到推送");
            BaseApplication.getOffline().stop();
            BaseApplication.getOffline().play(str);
        }

        /**
         * 关闭主界面之前的activity
         */
        @JavascriptInterface
        public void closeBeforeActivity() {
            LogUtils.i("关闭其他页面");
            ActivityManager.finishBeforeActivity(IndexActivity.class);
        }

    }

}
