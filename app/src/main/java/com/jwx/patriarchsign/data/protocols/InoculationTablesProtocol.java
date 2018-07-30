package com.jwx.patriarchsign.data.protocols;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jwx.patriarchsign.data.constants.Interface;
import com.jwx.patriarchsign.data.domain.InoculationTableInfo;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8 0008.
 */

public class InoculationTablesProtocol extends BaseProtocol<List<InoculationTableInfo>> {
    @Override
    protected HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected void wrapRequestParams(RequestParams params) {

    }

    @Override
    protected String getInterface() {
        return Interface.getBaseUrl() + Interface.URL_GET_TABLE_LIST;
    }

    @Override
    protected List<InoculationTableInfo> parseJson(String json) {
        return new Gson().fromJson(json, new TypeToken<List<InoculationTableInfo>>() {
        }.getType());
    }
}
