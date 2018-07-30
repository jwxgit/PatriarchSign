package com.jwx.patriarchsign.data.protocols;


import com.jwx.patriarchsign.utils.LogUtils;
import com.jwx.patriarchsign.utils.NetUtils;
import com.jwx.patriarchsign.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.ConnectException;

import javax.net.ssl.SSLContext;


public abstract class BaseProtocol<T> {
    private static final String DIR      = "json";
    private static final long   DURATION = 5 * 60 * 1000;
    private static SSLContext mSslContext;

    /**
     * 同步的http请求
     *
     * @return
     * @throws Throwable
     */
    public T getDataFromNet() throws Throwable {
        RequestParams params = getParams();
        if (doCache()) {
            params.setCacheMaxAge(DURATION);
        }
        String jsonResult = x.http().requestSync(getMethod(), params, String.class);

        // 解析json
        return parseJson(jsonResult);
    }


    /**
     * 异步的http请求
     *
     * @param callback
     */
    public void getDataFromNet(final LoadCallback<T> callback) {

        if (!NetUtils.isNetworkAvailable()) {
            ToastUtils.showMessageShort("网络未连接");
            callback.onFailed(new Throwable("网络未连接"), null);
        } else {
            final RequestParams params = getParams();
            LogUtils.i("请求接口  " + params.getUri());
            if (doCache()) {
                params.setCacheMaxAge(DURATION);
            }

            x.http().request(getMethod(), params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int resultCode = jsonObject.optInt("code");
                        if (resultCode == 0) {
                            callback.onSuccess(parseJson(jsonObject.optString("data")));
                        } else {
                            String msg = jsonObject.optString("msg");
                            callback.onFailed(new Throwable(msg), msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailed(e, "Json解析错误");
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                    if (ex.getClass().getName().equals(ConnectException.class.getName())) {
                        callback.onFailed(new Throwable("服务器异常"), null);
                    } else {
                        callback.onFailed(ex, null);
                    }
                    ex.printStackTrace();
                    LogUtils.i("ERROR " + ex.getMessage());


                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
        }
    }


    public interface LoadCallback<T> {
        void onSuccess(T data);

        void onFailed(Throwable e, String errorCode);
    }


    /**
     * 是否做本地缓存
     *
     * @return
     */
    protected boolean doCache() {
        return false;
    }

    /**
     * 获取请求方式
     *
     * @return
     */
    protected abstract HttpMethod getMethod();

    /**
     * 获取参数
     *
     * @return
     */
    protected RequestParams getParams() {
        RequestParams params = new RequestParams(getInterface());
        params.setCharset("UTF-8");
        wrapRequestParams(params);
        return params;
    }

    protected abstract void wrapRequestParams(RequestParams params);

    //    /**
//     * 获得接口
//     *
//     * @return
//     */
    protected abstract String getInterface();

    /**
     * 解析json
     *
     * @param json
     * @return
     */
    protected abstract T parseJson(String json);
}
