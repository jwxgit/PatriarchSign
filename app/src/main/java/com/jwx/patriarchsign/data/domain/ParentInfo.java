package com.jwx.patriarchsign.data.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class ParentInfo implements Serializable {
    // 登记台id
    private Integer workbenchId;
    // 儿童编码
    private String  childCode;
    // 儿童名称
    private String  childName;
    private ImgInfo fingerprints;  //指纹图片
    private ImgInfo signature;   //签名图片
    private ImgInfo pic;        //人像图片

    public Integer getWorkbenchId() {
        return workbenchId;
    }

    public void setWorkbenchId(Integer workbenchId) {
        this.workbenchId = workbenchId;
    }

    public String getChildCode() {
        return childCode;
    }

    public void setChildCode(String childCode) {
        this.childCode = childCode;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public ImgInfo getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(ImgInfo fingerprints) {
        this.fingerprints = fingerprints;
    }

    public ImgInfo getSignature() {
        return signature;
    }

    public void setSignature(ImgInfo signature) {
        this.signature = signature;
    }

    public ImgInfo getPic() {
        return pic;
    }

    public void setPic(ImgInfo pic) {
        this.pic = pic;
    }
}
