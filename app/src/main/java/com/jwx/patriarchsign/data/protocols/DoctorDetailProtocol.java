package com.jwx.patriarchsign.data.protocols;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.data.domain.DoctorItemInfo;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

/**
 * Created by Administrator on 2017/11/7 0007.
 */

public class DoctorDetailProtocol extends BaseProtocol<DoctorItemInfo> {

    private String userId;

    public DoctorDetailProtocol(String userId) {
        this.userId = userId;
    }

    @Override
    protected HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected void wrapRequestParams(RequestParams params) {
        params.addQueryStringParameter("userId", userId);
    }

    @Override
    protected String getInterface() {
        return Interface.getBaseUrl() + Interface.URL_DOCTOR_INFO_DETAIL;
    }

    @Override
    protected DoctorItemInfo parseJson(String json) {
        return new Gson().fromJson(json, new TypeToken<DoctorItemInfo>() {
        }.getType());
    }
}
