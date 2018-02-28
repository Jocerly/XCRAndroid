package com.yatang.xc.xcr.entity;

import java.io.Serializable;

/**
 * Created by zengxiaowen on 2017/7/24.
 */

public class MainViewEntity implements Serializable{

    private String title;
    private String txtNum;
    private int drawable;
    private String maxClassTime = "0";
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTxtNum() {
        return txtNum;
    }

    public void setTxtNum(String txtNum) {
        this.txtNum = txtNum;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getMaxClassTime() {
        return maxClassTime;
    }

    public void setMaxClassTime(String maxClassTime) {
        this.maxClassTime = maxClassTime;
    }

    @Override
    public String toString() {
        return "MainViewEntity{" +
                "title='" + title + '\'' +
                ", txtNum='" + txtNum + '\'' +
                ", drawable=" + drawable +
                ", maxClassTime='" + maxClassTime + '\'' +
                '}';
    }
}
