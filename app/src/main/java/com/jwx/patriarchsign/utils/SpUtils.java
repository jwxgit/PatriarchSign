
package com.jwx.patriarchsign.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jwx.patriarchsign.app.application.BaseApplication;


/**
 * @author 菜花 SharedPreferences 工具类
 */
public class SpUtils {
    private static SharedPreferences mSp;
    private static final String CONFIG = "config";
    private static Editor editor;

    /**
     * 取string值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(String key, String defValue) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getString(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getInt(key, defValue);
    }

    public static long getLong(String key, long defValue) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getLong(key, defValue);
    }

    public static void putString(String key, String value) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.putString(key, value);
    }

    public static void putLong(String key, long value) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.putLong(key, value);
    }

    public static void putBoolean(String key, boolean value) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.putBoolean(key, value);
    }

    public static void putInt(String key, int value) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.putInt(key, value);
    }

    public static void remove(String key) {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.remove(key);
    }

    /**
     * 清空
     */
    public static void clear() {

        if (mSp == null) {
            mSp = BaseApplication.getContext().getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = mSp.edit();
        }
        editor.clear();
        editor.commit();
    }

    /**
     * 提交
     */
    public static void commite() {

        if (editor != null)
            editor.commit();
        editor = null;
    }
}
