package com.jwx.patriarchsign.socket;

import com.alibaba.fastjson.JSON;

/**
 * TCP Socket客户端
 *
 * @author wurenqing
 * @since 2018-08-03
 */
public class DefaultSocketClient extends AbstractSocketClient {
    private SocketTransceiver client;

    @Override
    public void onConnect(SocketTransceiver transceiver) {
        System.out.println("已连接到服务端......");
    }

    @Override
    public void onConnectFailed() {
        System.out.println("连接失败......");
    }

    @Override
    public void onReceive(SocketTransceiver transceiver, Message message) {
        System.out.println("接收到服务端数据：" + JSON.toJSONString(message));
        // 发送报文测试
        if (message.getType() == 1) {
            Message body = mockMessage();
            String str = JSON.toJSONString(body);
            System.out.println(String.format("客户端发送报文长度：%s,内容=%s", str.length(), str));
            transceiver.send(body);
        }
    }

    @Override
    public void onDisconnect(SocketTransceiver transceiver) {
        System.out.println("与服务端断开了连接.....");
    }


    public Message mockMessage() {
        Message message = new Message();
        message.setType(1);
//		File file = new File("/APP/image/2.jpg");
//		String str = BitmapUtils.coverFileToString(file);
//		message.setData(str);
        return message;
    }


}