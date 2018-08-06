package com.jwx.patriarchsign.socket;

import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.msg.SocketMessage;

import java.util.HashMap;
import java.util.Map;

public class DefaultUIHandler extends UIHandler {

    public static Map<String, Class> messageMapping = new HashMap<>();

    @Override
    public void handle(SocketMessage message) {
        // 在这个方法里处理消息,
        // 发送消息
        // todo
        if (message.getMsgType().equals(MessageType.CLIENT_AGREEMENT)) {

        } else if (message.getMsgType().equals(MessageType.SERVER_COMPLETE)) {

        }

        // 发送消息,例如
        SocketMessage message1 = new SocketMessage();
        message1.setMsgType(MessageType.SERVER_COMPLETE);
        boolean isSuccess = client.getTransceiver().send(message1);
        // 发送成功,refreshUI
    }


}
