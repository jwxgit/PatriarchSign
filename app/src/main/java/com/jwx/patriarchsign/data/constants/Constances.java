package com.jwx.patriarchsign.data.constants;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public interface Constances {
    //Sp keys
    String SP_KEY_ID_ADDRESS     = "sp_key_id_address";
    String SP_KEY_PORT           = "sp_key_port";
    String SP_KEY_WORKBENCH_ID   = "sp_key_workbench_id";//接种台ID
    String SP_KEY_WORKBENCH_NAME = "sp_key_workbench_name";//接种台名称
    //Intent keys
    String INTENT_KEY_CLOSEABLE  = "sp_key_closeable";//Activity是否可由用户关闭
    //全局code值
    int    CODE_OK               = 200;

    //通用密码
    String PSW_TO_DOCTORS = "123";//进入医生信息页面
    String PSW_TO_BIND    = "456";//进入设备绑定页面
    String PSW_TO_CONFIG  = "789";//进入IP设置页面
}
