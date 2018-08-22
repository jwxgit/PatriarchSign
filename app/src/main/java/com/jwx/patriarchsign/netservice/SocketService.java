package com.jwx.patriarchsign.netservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;


import com.jwx.patriarchsign.app.activities.IpConfigActivity;
import com.jwx.patriarchsign.data.constants.Constances;
import com.jwx.patriarchsign.netty.NettyClient;
import com.jwx.patriarchsign.utils.SpUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ZHANGZHIYU on 2018/8/7.
 */

public class SocketService extends Service {

    public static NettyClient nettyClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.getLogger(SocketService.class.getName()).info("socket服务销毁");
        // 销毁netty
        if (null != nettyClient)
            nettyClient.stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           Logger.getLogger(SocketService.class.getName()).info("socket服务启动");
          //获取监听不断监听服务端消息，只要客户端不退出的情况下，始终保持连接
        String ip = SpUtils.getString(Constances.SP_KEY_ID_ADDRESS, "");
        String port = SpUtils.getString(Constances.SP_KEY_PORT, "");
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            intent = new Intent(this, IpConfigActivity.class);
            this.startActivity(intent);
        }
        nettyClient = new NettyClient(Integer.parseInt(port), ip);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Logger.getLogger(SocketService.class.getName()).log(Level.INFO, "socket服务创建");
        super.onCreate();
    }

}
