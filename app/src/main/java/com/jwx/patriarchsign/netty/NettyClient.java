package com.jwx.patriarchsign.netty;

import com.alibaba.fastjson.JSON;
import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.msg.SocketMessage;
import com.jwx.patriarchsign.netty.coder.MessageByteDecoder;
import com.jwx.patriarchsign.netty.coder.MessageByteEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyClient {
        private int port;
        private String host;
        public  static  SocketChannel socketChannel;
        private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);
        public NettyClient(int port, String host) {
            this.port = port;
            this.host = host;
            start();
        }
        private void start(){
            ChannelFuture future = null;
            try {
                EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
                Bootstrap bootstrap=new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
                bootstrap.group(eventLoopGroup);
                bootstrap.remoteAddress(host,port);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new IdleStateHandler(20,10,0));
                        p.addLast(new MessageByteDecoder());
                        p.addLast(new MessageByteEncoder());
                        p.addLast(new NettyClientHandler());
                    }
                });
                future =bootstrap.connect(host,port).sync();
                if (future.isSuccess()) {
                    socketChannel = (SocketChannel)future.channel();
                    System.out.println("connect server  成功---------");
                }else{
                    System.out.println("连接失败！");
                    System.out.println("准备重连！");
                    start();
                }
            } catch (Exception e) {

            }finally{
//    		if(null != future){
//    			if(null != future.channel() && future.channel().isOpen()){
//    				future.channel().close();
//    			}
//    		}
//    		System.out.println("准备重连！");
//    		start();
            }
        }

    public void stop() {
        if (null != socketChannel)
            socketChannel.close();
    }

    public  static  boolean  sendMessage(SocketMessage message){
        try {
            if(socketChannel!=null){
                socketChannel.writeAndFlush(JSON.toJSON(message).toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) throws InterruptedException {
        NettyClient bootstrap = new NettyClient(9999, "192.168.199.110");
           MessageLisenerRegister.registMessageLisener(MessageType.SERVER_SIGNATURE_START, new MessageLisener() {
               @Override
               public void onMessage(SocketMessage message) {

               }
           });

    }



}






