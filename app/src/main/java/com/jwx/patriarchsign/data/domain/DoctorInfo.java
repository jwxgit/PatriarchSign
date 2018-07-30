package com.jwx.patriarchsign.data.domain;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class DoctorInfo {
    private String  userId;
    private ImgInfo imgInfo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ImgInfo getImgInfo() {
        return imgInfo;
    }

    public void setImgInfo(ImgInfo imgInfo) {
        this.imgInfo = imgInfo;
    }
}
