package com.jwx.patriarchsign.socket;

import com.alibaba.fastjson.JSON;
import com.jwx.patriarchsign.msg.SocketMessage;

/**
 * TCP Socket客户端
 *
 * @author wurenqing
 * @since 2018-08-03
 */
public class DefaultSocketClient extends AbstractSocketClient {

    private UIHandler uiHandler;

    public DefaultSocketClient(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }
    @Override
    public void onConnect(SocketTransceiver transceiver) {
        System.out.println("已连接到服务端......");
    }

    @Override
    public void onConnectFailed() {
        System.out.println("连接失败......");
    }

    @Override
    public void onReceive(SocketTransceiver transceiver, SocketMessage message) {
        System.out.println("接收到服务端数据：" + JSON.toJSONString(message));
        uiHandler.resolveMessage(message);
    }

    @Override
    public void onDisconnect(SocketTransceiver transceiver) {
        System.out.println("与服务端断开了连接.....");
    }

}