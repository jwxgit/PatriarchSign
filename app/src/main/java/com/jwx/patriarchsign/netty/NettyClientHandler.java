package com.jwx.patriarchsign.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.msg.Agreement;
import com.jwx.patriarchsign.msg.ChildInfo;
import com.jwx.patriarchsign.msg.Command;
import com.jwx.patriarchsign.msg.Completion;
import com.jwx.patriarchsign.msg.ImageInfo;
import com.jwx.patriarchsign.msg.SignImage;
import com.jwx.patriarchsign.msg.SocketMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyClientHandler extends SimpleChannelInboundHandler {

    public static ChannelHandlerContext context = null;
    //利用写空闲发送心跳检测消息
    public static Map<String, Class> messageMapping = new HashMap<>();
    static {
        // 将消息类型和对应的实体进行绑定，用于接收消息时自动反序列化
        messageMapping.put(MessageType.CLIENT_AGREEMENT, Agreement.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_START, ChildInfo.class);
        messageMapping.put(MessageType.CLIENT_GET_CONSENT_FORM, ImageInfo.class);
        messageMapping.put(MessageType.SERVER_PUSH_IMAGE, ImageInfo.class);
        messageMapping.put(MessageType.SERVER_DO_SIGNATURE, Command.class);
        messageMapping.put(MessageType.CLIENT_UPLOAD_IMG, Completion.class);
        messageMapping.put(MessageType.SERVER_SIGNATURE_CANCEL, Completion.class);
        messageMapping.put(MessageType.SERVER_COMPLETE, Completion.class);
    }

    // 先处理消息
    public SocketMessage resolveMessage(SocketMessage message) {
        // 序列化消息
        if (null == messageMapping)
            throw new RuntimeException("消息序列化映射表不能为空");
        Class msgObjClass = messageMapping.get(message.getMsgType());
        if (null == msgObjClass)
            throw new RuntimeException("消息对应的反序列化类不存在");
        message.setData(JSONObject.toJavaObject((JSONObject) message.getData(), msgObjClass));
        return message;
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //在此发送心跳消息
    }

    private void  handlerMessage(SocketMessage message){
          String mstType = message.getMsgType();
          MessageLisener messageLisener  = MessageLisenerRegister.getMessageLisener(mstType);
          if(messageLisener==null){
              System.out.println("消息类型【"+mstType+"】"+"未注册监听器");
              return;
          }
          messageLisener.onMessage(message);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object byteBuf) throws Exception {
        SocketMessage   message = getMessageFromBuf(byteBuf);
        if(message!=null){
            System.out.println("【channelRead0】-----收到消息：");
            System.out.println(message.toString());
            handlerMessage(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object byteBuf) throws Exception {
        SocketMessage   message = getMessageFromBuf(byteBuf);
        if(message!=null){
            System.out.println("【channelRead】收到消息"+message.toString());
            System.out.println(message.toString());
            handlerMessage(message);
        }
    }

    private SocketMessage getMessageFromBuf(Object byteBuf) {
        String message="";
        try {
            ByteBuf bf = (ByteBuf) byteBuf;
            byte[] byteArray = new byte[bf.capacity()];
            bf.readBytes(byteArray);
            message = new String(byteArray);
            return resolveMessage(JSON.parseObject(message, SocketMessage.class));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(NettyClientHandler.class.getName()).log(Level.WARNING,"消息转换失败"+message);
        }
        return null;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      /*  super.channelActive(ctx);*/


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //重新连接服务器
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
