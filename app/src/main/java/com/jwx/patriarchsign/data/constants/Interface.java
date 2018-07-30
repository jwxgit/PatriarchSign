package com.jwx.patriarchsign.data.constants;

import android.text.TextUtils;

import com.jwx.patriarchsign.utils.SpUtils;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class Interface {

    private static String mBaseUrl;

    public static void updateBaseUrl() {
        mBaseUrl = "http://" + SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, "") + ":" + SpUtils.getString(Constances.SP_KEY_PORT, "");
    }

    public static String getBaseUrl() {
        if (TextUtils.isEmpty(mBaseUrl) || "http://:".equals(mBaseUrl)) {
            updateBaseUrl();
        }
        return mBaseUrl;
    }

    public static String getFullUrl(String suffix) {
        if (TextUtils.isEmpty(mBaseUrl) || "http://:".equals(mBaseUrl)) {
            updateBaseUrl();
        }
        if (!TextUtils.isEmpty(mBaseUrl)) {
            return mBaseUrl + suffix;
        }
        return  suffix;
    }

    /**
     * 用户同意与不同意接口
     */
    public static String URL_USER_ALLOW_SIGNATURE = "/app/allowSignature.do";
    /**
     * 获取用户协议
     */
    public static String URL_USER_LICENSE         = "/app/getSignaturePermission.do";
    /**
     * 获取医生信息列表
     */
    public static String URL_DOCTOR_INFO_LIST     = "/app/getUserInfoList.do";
    /**
     * 获取医生详情
     */
    public static String URL_DOCTOR_INFO_DETAIL   = "/app/getLastImgByUserId.do";
    /**
     * 上传医生图片接口
     */
    public static String URL_UPLOAD_DOCTOR_IMG    = "/app/uploadUserImg.do";
    /**
     * 上传家长图片接口
     */
    public static String URL_UPLOAD_PATRIACH_IMG  = "/app/uploadpatriarchalImg.do";
    /**
     * 清除图片接口
     */
    public static String URL_CLEAR_IMAGES         = "/app/clearUserImg.do";
    /**
     * 获取接种台列表
     */
    public static String URL_GET_TABLE_LIST       = "/app/registerList.do";
    /**
     * 待机网页地址
     */
    public static String URL_INDEX                = "/page/index.do";
    /**
     * 图片接口
     */
    public static String URL_SHOW_IMAGE = "/common/showImage?path=";
}
