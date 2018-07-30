package com.jwx.patriarchsign.data.protocols;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.data.domain.DoctorItemInfo;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorItemsProtocol extends BaseProtocol<List<DoctorItemInfo>> {
    @Override
    protected HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected void wrapRequestParams(RequestParams params) {

    }

    @Override
    protected String getInterface() {
        return Interface.getBaseUrl() + Interface.URL_DOCTOR_INFO_LIST;
    }

    @Override
    protected List<DoctorItemInfo> parseJson(String json) {
        return new Gson().fromJson(json, new TypeToken<List<DoctorItemInfo>>() {
        }.getType());
    }
}
