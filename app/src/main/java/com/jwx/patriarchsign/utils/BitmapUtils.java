package com.jwx.patriarchsign.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class BitmapUtils {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 去除指纹图片背景颜色
     *
     * @param bmpSrc  输入图像数组
     * @param srcSize 输入图像宽高 长度为2的int数组
     * @param dstSize 输出图像宽高 长度为2的int数组
     * @return 输出图像数组
     */
    public native static int[] removeBackground(int[] bmpSrc, int[] srcSize, int[] dstSize);

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(format, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
        return Bitmap2Bytes(bm, format, 100);
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        return Bitmap2Bytes(bm, Bitmap.CompressFormat.PNG);
    }

    /**
     * 将bite字节数组转bitmap
     */
    public static Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            } else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }
}
