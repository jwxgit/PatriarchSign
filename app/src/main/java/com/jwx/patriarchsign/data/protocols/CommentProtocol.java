package com.jwx.patriarchsign.data.protocols;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.data.domain.DoctorInfo;
import com.jwx.patriarchsign.data.domain.ImgInfo;
import com.jwx.patriarchsign.data.domain.ParentInfo;
import com.jwx.patriarchsign.data.manager.ThreadManager;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.SpUtils;
import com.jwx.patriarchsign.utils.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class CommentProtocol {

    public static void uploadParentInfo(final Bitmap finger, final Bitmap sign, final Bitmap pic, final String childCode, final String childName, final ResultCallback callback) {
        ThreadManager.getLongRunPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ParentInfo parentInfo = new ParentInfo();
                    parentInfo.setWorkbenchId(SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1));
                    parentInfo.setChildCode(childCode);
                    parentInfo.setChildName(childName);
                    if (finger != null) {
                        String fingerData = new String(BitmapUtils.Bitmap2Bytes(finger, Bitmap.CompressFormat.PNG), "ISO-8859-1");
                        parentInfo.setFingerprints(new ImgInfo(fingerData, "png", 1));
                    }
                    if (sign != null) {
                        String signData = new String(BitmapUtils.Bitmap2Bytes(sign, Bitmap.CompressFormat.PNG), "ISO-8859-1");
                        parentInfo.setSignature(new ImgInfo(signData, "png", 2));
                    }
                    if (pic != null) {
                        String picData = new String(BitmapUtils.Bitmap2Bytes(pic, Bitmap.CompressFormat.JPEG), "ISO-8859-1");
                        parentInfo.setPic(new ImgInfo(picData, "jpeg", 3));
                    }

                    UIUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadInfo(parentInfo, 1, callback);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    callback.onFailed();
                }
            }
        });
    }


    public static void uploadDoctorInfo(final Bitmap bmpDst, final String userId, final int imgType, final ResultCallback callback) {
        ThreadManager.getLongRunPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String imgStrData = new String(BitmapUtils.Bitmap2Bytes(bmpDst), "ISO-8859-1");
                    final DoctorInfo doctorInfo = new DoctorInfo();
                    doctorInfo.setUserId(userId);
                    doctorInfo.setImgInfo(new ImgInfo(imgStrData, imgType == 3 ? "jpeg" : "png", imgType));
                    UIUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadInfo(doctorInfo, 0, callback);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    callback.onFailed();
                }
            }
        });
    }

    /**
     * @param obj      上传数据
     * @param type     类型 0 医生图片信息 1 家长图片信息
     * @param callback 回调
     */
    public static void uploadInfo(Object obj, int type, final ResultCallback callback) {
        String url = type == 0 ? Interface.getBaseUrl() + Interface.URL_UPLOAD_DOCTOR_IMG
                : Interface.getBaseUrl() + Interface.URL_UPLOAD_PATRIACH_IMG;
        RequestParams params = new RequestParams(url);
        params.setMultipart(true);
        List<KeyValue> keyValues = new ArrayList<>();
        String keyName = type == 0 ? "doctorInfo" : "parentInfo";
        keyValues.add(new KeyValue(keyName, new Gson().toJson(obj)));
        MultipartBody body = new MultipartBody(keyValues, "utf-8");
        params.setRequestBody(body);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
//                ToastUtils.showMessageLong(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailed();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                if (callback != null) {
                    callback.onFailed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void clearUserImgs(String userId, final ResultCallback callback) {
        RequestParams params = new RequestParams(Interface.getBaseUrl() + Interface.URL_CLEAR_IMAGES);
        params.addQueryStringParameter("userId", userId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                if (callback != null) {
                    callback.onFailed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void userAllowSignature(String childCode, int allow, final ResultCallback callback) {
        RequestParams params = new RequestParams(Interface.getBaseUrl() + Interface.URL_USER_ALLOW_SIGNATURE);
        JSONObject jsonObject = new JSONObject();//服务器需要传参的json对象
        try {
            jsonObject.put("workbenchId", SpUtils.getInt(Constances.SP_KEY_WORKBENCH_ID, -1));//根据实际需求添加相应键值对
            jsonObject.put("permission", allow);
            jsonObject.put("childCode", childCode);
            params.setAsJsonContent(true);
            params.setBodyContent(jsonObject.toString());

            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //解析result
                    LogUtils.i(result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int resultCode = jsonObject.optInt("code");
                        if (resultCode == 0) {
                            callback.onSuccess();
                        } else {
                            String msg = jsonObject.optString("msg");
                            callback.onFailed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailed();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    callback.onFailed();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * 获取用户协议
     *
     * @param callback
     */
    public static void getUserLicense(final UserLicenseResultCallback callback) {
        RequestParams params = new RequestParams(Interface.getBaseUrl() + Interface.URL_USER_LICENSE);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int resultCode = jsonObject.optInt("code");
                    if (resultCode == 0) {
                        String msg = jsonObject.optString("data");
                        callback.onSuccess(msg);
                    } else {
                        //String msg = jsonObject.optString("msg");
                        callback.onFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailed();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                if (callback != null) {
                    callback.onFailed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public interface ResultCallback {
        void onSuccess();

        void onFailed();
    }

    public interface UserLicenseResultCallback {
        void onSuccess(String result);

        void onFailed();
    }
}
