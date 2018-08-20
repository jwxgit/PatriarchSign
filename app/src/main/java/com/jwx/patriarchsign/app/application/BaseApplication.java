package com.jwx.patriarchsign.app.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.jwx.patriarchsign.app.tts.SpeechUtilOffline;
import com.jwx.patriarchsign.msg.ChildInfo;

import org.xutils.x;

/**
 * By: 菜花
 * Time: 2016/8/31
 * Dec :TODO
 */
public class BaseApplication extends Application {
    private static Context           mContext;
    private static Thread            mMainThread;
    private static Handler           mMainThreadHandler;
    private static Looper            mMainThreadLooper;
    private static long              mMainThreadId;
    private   static     SpeechUtilOffline offline;

    public static ChildInfo childInfo;
    public static Bitmap screenShot;
    public static String BASE_PATH = "";

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mMainThread = Thread.currentThread();
        // 主线程id
        // mMainThreadId = mMainThread.getId();
        // android.os.Process.myPid();// 进程id
        mMainThreadId = android.os.Process.myTid();// 当前线程id
        // android.os.Process.myUid();//用户id
        // 主线程handler
        mMainThreadHandler = new Handler();
        //
        mMainThreadLooper = getMainLooper();
        init();
        offline = new SpeechUtilOffline(mContext);
    }

    private void init() {
        x.Ext.init(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static Thread getMainThread() {

        return mMainThread;
    }

    public static long getMainThreadId() {

        return mMainThreadId;
    }

    public static Handler getMainThreadHandler() {

        return mMainThreadHandler;
    }

    public static Looper getMainThreadLooper() {

        return mMainThreadLooper;
    }

    public static SpeechUtilOffline getOffline() {
        return offline;
    }

}
