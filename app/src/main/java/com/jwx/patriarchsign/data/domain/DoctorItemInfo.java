package com.jwx.patriarchsign.data.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class DoctorItemInfo implements Serializable {
    private String userId;
    private String username;//名称
    private int    state; //状态 0:完整 1:不全 2:无信息
    private String fingerprintsPath;//指纹图片地址
    private String signaturePath;//签名图片地址
    private String picPath;//照片地址

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFingerprintsPath() {
        return fingerprintsPath;
    }

    public void setFingerprintsPath(String fingerprintsPath) {
        this.fingerprintsPath = fingerprintsPath;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
