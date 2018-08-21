package com.jwx.patriarchsign.netty;


import com.jwx.patriarchsign.msg.SocketMessage;

public interface MessageLisener {
    public  void  onMessage(SocketMessage message);
}
