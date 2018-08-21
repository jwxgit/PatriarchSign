package com.jwx.patriarchsign.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MessageLisenerRegister {
    private static   Map<String ,MessageLisener>  REGISTER = new  HashMap();
    private  MessageLisenerRegister(){
    }
    /**
     * 注册消息监听
     * @param msgtype
     * @param lisener
     * @return
     */
    public static boolean registMessageLisener(String msgtype,MessageLisener lisener){
        try {
            REGISTER.put(msgtype,lisener);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(MessageLisenerRegister.class.getName()).log(Level.WARNING,"注册监听失败");
        }
        return false;
    }


    /**
     * 注册消息监听
     * @param msgtype
     * @return
     */
    public static void unRegistMessageLisener(String msgtype){
            REGISTER.remove(msgtype);
    }

    /**
     * 获取消息监听器
     * @param msgtype
     * @return
     */
    public static MessageLisener getMessageLisener(String  msgtype){
          return   REGISTER.get(msgtype);
    }
}
