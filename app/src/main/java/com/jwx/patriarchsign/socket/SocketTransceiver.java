package com.jwx.patriarchsign.socket;

import com.alibaba.fastjson.JSON;
import com.jwx.patriarchsign.utils.BitmapUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * socket 接收
 *
 * @author wurenqing
 * @since 2018-08-03
 */
public abstract class SocketTransceiver implements Runnable {

    protected Socket socket;
    protected InetAddress addr;
    protected BufferedInputStream in;
    protected BufferedOutputStream out;
    private boolean runFlag;


    /**
     * 实例化
     *
     * @param socket 已经建立连接的socket
     */
    public SocketTransceiver(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
    }

    /**
     * 获取连接到的Socket地址
     *
     * @return InetAddress对象
     */
    public InetAddress getInetAddress() {
        return addr;
    }

    /**
     * 开启Socket收发
     * <p>
     * 如果开启失败，会断开连接并回调{@code onDisconnect()}
     */
    public void start() {
        runFlag = true;
        new Thread(this).start();
    }

    /**
     * 断开连接(主动)
     * <p>
     * 连接断开后，会回调{@code onDisconnect()}
     */
    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送字符串
     *
     * @param message 字符串
     * @return 发送成功返回true
     */
    public boolean send(Message message) {
        if (out != null) {
            try {
                out = new BufferedOutputStream(socket.getOutputStream(), 1024 * 200);
                String json = JSON.toJSONString(message);
                byte[] data = json.getBytes("UTF-8");
                // 先发送消息体长度，4个字节
                int fileLength = data.length;
                out.write(BitmapUtils.intToByteArray(fileLength));
                // 再发送消息体
                out.write(data);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 监听Socket接收的数据(新线程中运行)
     */
//	@Override
    public void run() {
        try {
            in = new BufferedInputStream(this.socket.getInputStream(), 1024 * 200);
            out = new BufferedOutputStream(this.socket.getOutputStream(), 1024 * 200);
        } catch (IOException e) {
            e.printStackTrace();
            runFlag = false;
        }
        while (runFlag) {
            try {
                // 先读取第一个包，取出消息体长度
                byte[] data = new byte[4];
                int offset = 0;
                while (offset < data.length) {
                    offset += in.read(data, offset, data.length - offset);
                }
                int msgLength = BitmapUtils.byteArrayToInt(data);
                // 再读取消息体
                offset = 0;
                data = new byte[msgLength];
                while (offset < msgLength) {
                    offset += in.read(data, offset, msgLength - offset);
                }
                this.onReceive(addr, JSON.parseObject(new String(data, "UTF-8"), Message.class));
            } catch (IOException e) {
                // 连接被断开(被动)
                runFlag = false;
            }
        }
        // 断开连接
        try {
            in.close();
            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onDisconnect(addr);
    }

    /**
     * 接收到数据
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr 连接到的Socket地址
     * @param s    收到的字符串
     */
    public abstract void onReceive(InetAddress addr, Message s);

    /**
     * 连接断开
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr 连接到的Socket地址
     */
    public abstract void onDisconnect(InetAddress addr);
}