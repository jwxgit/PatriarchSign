package com.jwx.patriarchsign.data.cache;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/9 0009.
 * Bitmap 缓存类
 */

public class BitmapCache {

    private static Bitmap mParentFaceBmp; //家长图片缓存
    private static Bitmap mParentFingerBmp;//家长指纹缓存
    private static Bitmap mParentSignBmp;//家长签名缓存

    public static void setParentFaceBmp(Bitmap bmp) {
        mParentFaceBmp = bmp;
    }

    public static void setParentFingerBmp(Bitmap bmp) {
        mParentFingerBmp = bmp;
    }

    public static void setParentSignBmp(Bitmap bmp) {
        mParentSignBmp = bmp;
    }

    public static Bitmap getParentFaceBmp() {
        return mParentFaceBmp;
    }

    public static Bitmap getParentFingerBmp() {
        return mParentFingerBmp;
    }

    public static Bitmap getParentSignBmp() {
        return mParentSignBmp;
    }

    public static void clearParentCache() {
        if (mParentFaceBmp != null) {
            mParentFaceBmp.recycle();
            mParentFaceBmp = null;
        }
        if (mParentFingerBmp != null) {
            mParentFingerBmp.recycle();
            mParentFingerBmp = null;
        }
        if (mParentSignBmp != null) {
            mParentSignBmp.recycle();
            mParentSignBmp = null;
        }
    }
}
