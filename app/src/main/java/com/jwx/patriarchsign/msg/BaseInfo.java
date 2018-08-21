package com.jwx.patriarchsign.msg;

import java.io.Serializable;

public class BaseInfo  implements Serializable{

    /*
     * 儿童编码
     */
    private String chilCode;

    /*
     * 登记台id
     */
    private String workbenchId;

    public String getChilCode() {
        return chilCode;
    }

    public void setChilCode(String chilCode) {
        this.chilCode = chilCode;
    }

    public String getWorkbenchId() {
        return workbenchId;
    }

    public void setWorkbenchId(String workbenchId) {
        this.workbenchId = workbenchId;
    }
}
