package com.jwx.patriarchsign.socket;

import com.alibaba.fastjson.JSONObject;
import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.msg.Completion;
import com.jwx.patriarchsign.msg.SocketMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class UIHandler {

    public static Map<String, Class> messageMapping = new HashMap<>();

    protected AbstractSocketClient client;

    // TODO
    static {
        // 将消息类型和对应的实体进行绑定，用于接收消息时自动反序列化
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, Completion.class);
    }


    public void setSocketClient(AbstractSocketClient socketClient) {
        this.client = socketClient;
    }

    // 先处理消息
    public void resolveMessage(SocketMessage message) {
        // 序列化消息
        if (null == messageMapping)
            throw new RuntimeException("消息序列化映射表不能为空");
        Class msgObjClass = messageMapping.get(message.getMsgType());
        if (null == msgObjClass)
            throw new RuntimeException("消息对应的反序列化类不存在");
        message.setData(JSONObject.toJavaObject((JSONObject) message.getData(), msgObjClass));
        handle(message);
    }

    public abstract void handle(SocketMessage message);
}