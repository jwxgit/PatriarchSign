package com.jwx.patriarchsign.data.domain;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class ImgInfo {
    private String imgDataStr;
    private String ext;
    private int    necessary;
    private int    imgType; //图片种类  1:指纹图片 2:签名图片 3:医生照片

    public ImgInfo() {
    }

    public ImgInfo(String imgDataStr, String ext, int imgType) {
        this.imgDataStr = imgDataStr;
        this.ext = ext;
        this.imgType = imgType;
    }

    public String getImgDataStr() {
        return imgDataStr;
    }

    public void setImgDataStr(String imgDataStr) {
        this.imgDataStr = imgDataStr;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getNecessary() {
        return necessary;
    }

    public void setNecessary(int necessary) {
        this.necessary = necessary;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }
}
