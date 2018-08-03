package com.jwx.patriarchsign.msg;

/*
 * @description 是否完成签核
 * @author wurenqing
 */
public class Completion extends BaseInfo {
    private int complete;

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }
}
