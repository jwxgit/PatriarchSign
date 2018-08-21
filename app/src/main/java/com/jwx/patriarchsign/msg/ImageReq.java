package com.jwx.patriarchsign.msg;

public class ImageReq {
    private String imgMd5;
    /*
     * 1健康询问，2疫苗通知书
     */
    private int imgType;

    public String getImgMd5() {
        return imgMd5;
    }

    public void setImgMd5(String imgMd5) {
        this.imgMd5 = imgMd5;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }
}
