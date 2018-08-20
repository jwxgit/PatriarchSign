package com.jwx.patriarchsign.app.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.jwx.patriarchsign.app.tts.SpeechUtilOffline;
import com.jwx.patriarchsign.msg.ChildInfo;

import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    //    public static LruCache<String, Bitmap> mMemoryCache;
    public static String BASE_PATH = "/data/data/com.jwx.patriarchsign/cache/images/";
    public static Map<String, String> IMAGE_CACHE = new HashMap<>();


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

        // 缓存图片
        File directory = new File(BASE_PATH);
        if (directory.exists() && directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                IMAGE_CACHE.put(f.getName(), f.getAbsolutePath());
            }
        } else {
            // 创建目录
            directory.mkdirs();
        }
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
