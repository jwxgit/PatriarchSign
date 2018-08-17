package com.jwx.patriarchsign.netservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.jwx.patriarchsign.netty.NettyClient;

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
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           Logger.getLogger(SocketService.class.getName()).info("socket服务启动");
          //获取监听不断监听服务端消息，只要客户端不退出的情况下，始终保持连接
        nettyClient = new NettyClient(9999, "192.168.199.112");
           return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Logger.getLogger(SocketService.class.getName()).log(Level.INFO,"socket服务创");
        super.onCreate();
    }

}
