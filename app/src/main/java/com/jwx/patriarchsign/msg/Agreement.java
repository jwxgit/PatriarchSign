package com.jwx.patriarchsign.msg;

/*
 * @description 2.4.1 是否同意知情同意书
 * @author wurenqing
 */
public class Agreement extends BaseInfo {

    private int agree;

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }
}
