package com.jwx.patriarchsign.msg;


import android.graphics.Bitmap;
import android.util.Base64;

import com.jwx.patriarchsign.constant.MessageType;
import com.jwx.patriarchsign.utils.BitmapUtils;
import com.jwx.patriarchsign.utils.DateUtil;
import com.jwx.patriarchsign.utils.ImageUtils;
import com.jwx.patriarchsign.utils.Md5Utils;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ZHANGZHIYU on 2018/8/7.
 */

public class MessageFactory {

    public static class ClientMessages {
        /**
         * 获取签名消息测试消息
         *
         * @return
         */
        public static SocketMessage getClientSignMessage(byte[] bmp) {
            String fileName = "sign.jpg";
            int imgType = 1;
            return getMutilClientMessage(bmp, fileName, MessageType.CLIENT_UPLOAD_IMG, imgType);
        }
//        public static SocketMessage getClientSignMessage() {
//            return getClientSignMessage("0101.jpg");
//        }

        /**
         * 获取指纹消息测试消息
         *
         * @return
         */
        public static SocketMessage getClienteFignerPrintMessag(byte[] bmp) {
            String fileName = "finger.jpg";
            int imgType = 2;
            return getMutilClientMessage(bmp, fileName, MessageType.CLIENT_UPLOAD_IMG, imgType);
        }

        /**
         * 获取面部照片测试消息
         *
         * @return
         */
        public static SocketMessage getClientFacePictureMessage(byte[] bmp) {
            String fileName = "picture.jpg";
            int imgType = 3;
            return getMutilClientMessage(bmp, fileName, MessageType.CLIENT_UPLOAD_IMG, imgType);
        }

        /**
         * 获取桌面快照测试消息
         *
         * @return
         */
        public static SocketMessage getClientDestTopSnapShotMessage(byte[] bmp) {
            String fileName = "desktop.jpg";
            int imgType = 4;
            return getMutilClientMessage(bmp, fileName, MessageType.CLIENT_UPLOAD_IMG, imgType);
        }

        /**
         * 用户是否同意消息
         *
         * @return
         */
        public static SocketMessage getClientAgreementMessage(int isAgree) {
            SocketMessage message = new SocketMessage();
            message.setMsgId(UUID.randomUUID().toString());
            String dateStr = DateUtil.dateToString(new Date(), DateUtil.YEAR_TO_MINSECOND);
            message.setMsgTimestamp(dateStr);
            message.setMsgType("CLIENT_AGREEMENT");
            message.setMsgMd5(Md5Utils.convertMD5(dateStr));
            Agreement aggrement = new Agreement();
            aggrement.setAgree(isAgree);
            aggrement.setChilCode(UUID.randomUUID().toString());
            message.setData(aggrement);
            return message;
        }

        /**
         * 根据文件md5要求获取服务器对应文件的消息
         *
         * @return
         */
        public static SocketMessage getClientGetConsentFormMessage(String imgId) {
            SocketMessage message = new SocketMessage();
            message.setMsgId(UUID.randomUUID().toString());
            String dateStr = DateUtil.dateToString(new Date(), DateUtil.YEAR_TO_MINSECOND);
            message.setMsgTimestamp(dateStr);
            message.setMsgType("CLIENT_GET_CONSENT_FORM");
            message.setMsgMd5(Md5Utils.convertMD5(dateStr));
            ImgId  imgId1=new ImgId();
            imgId1.setImgId(imgId);
            message.setData(imgId1);
            return message;
        }
    }


    /**
     *    获取客户端签核及桌面图片测试工具类
     * @param fileName  文件名称
     * @param msgType
     * @param imgType      --1签字图像/2指纹图像/3拍照图像/4摄像头图片
     * @return
     */
    private static SocketMessage getMutilClientMessage(byte[] bmp, String fileName, String msgType, int imgType) {
        SocketMessage message = new SocketMessage();
        message.setMsgId(UUID.randomUUID().toString());
        String dateStr =  DateUtil.dateToString(new Date(),DateUtil.YEAR_TO_MINSECOND);
        message.setMsgTimestamp(dateStr);
        message.setMsgType(msgType);
        SignImage  signImage = new SignImage();
        signImage.setImgName("");
        signImage.setImgType(imgType);
        String bmpStr = Base64.encodeToString(bmp, Base64.DEFAULT);
        signImage.setImgContent(bmpStr);
        signImage.setChilCode(UUID.randomUUID().toString());
        String msgMd5 = Md5Utils.string2MD5(dateStr + bmpStr);
        message.setMsgMd5(msgMd5);
        message.setData(signImage);
        return  message;
    }



    public static void main(String[] args) {

  /*      System.out.println( SocketMessageFactory.ServerCommand.getServerCommandSignMessage("0"));
        System.out.println( SocketMessageFactory.ServerCommand.getServerCommandFacePictureMessage("0"));
        System.out.println( SocketMessageFactory.ServerCommand.getServerCommandFignerPrintMessag("0"));
        System.out.println( SocketMessageFactory.ServerCommand.getServerCommandSignMessage("1"));
        System.out.println( SocketMessageFactory.ServerCommand.getServerCommandFacePictureMessage("1"));
        System.out.println( SocketMessageFactory.ServerCommand.getServerCommandFignerPrintMessag("1"));*/


    }
}
