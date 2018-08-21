package com.jwx.patriarchsign.constant;

public class MessageType {
    /*
     * 同意协议
     */
    public static String CLIENT_AGREEMENT = "CLIENT_AGREEMENT";

    /*
     * 跳转签核页面指令
     */
    public static String SERVER_SIGNATURE_START = "SERVER_SIGNATURE_START";

    /*
     * 请求图片
     */
    public static String CLIENT_GET_CONSENT_FORM = "CLIENT_GET_CONSENT_FORM";

    /*
     * 推送图片
     */
    public static String SERVER_PUSH_IMAGE = "SERVER_PUSH_IMAGE";

    /*
     * 2.4.5	签字指令/指纹指令/拍照指令
     */
    public static String SERVER_DO_SIGNATURE = "SERVER_DO_SIGNATURE";

    /*
     * 2.4.6	上传签字/指纹/拍照 图像信息
     */
    public static String CLIENT_UPLOAD_IMG = "CLIENT_UPLOAD_IMG";

    /*
     * 2.4.7	取消指令
     */
    public static String SERVER_SIGNATURE_CANCEL = "SERVER_SIGNATURE_CANCEL";

    /*
     * 2.4.8	确认完成签核
     */
    public static String SERVER_COMPLETE = "SERVER_COMPLETE";


}
