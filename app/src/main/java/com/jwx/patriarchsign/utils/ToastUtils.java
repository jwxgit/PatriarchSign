package com.jwx.patriarchsign.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jwx.patriarchsign.app.application.BaseApplication;

/**
 * Created by jett on 2016/10/9.
 * 避免多次弹出Toast造成覆盖
 */
public class ToastUtils {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast   toast   = null;
    private static Object  synObj  = new Object();

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     *
     * @param msg
     */
    public static void showMessageShort(final String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     *
     * @param msg
     */
    public static void showMessageLong(final String msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息
     *
     * @param msg
     * @param len
     */
    public static void showMessage(final String msg,
                                   final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(BaseApplication.getContext(), msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 关闭当前Toast
     */
    public static void cancelCurrentToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
