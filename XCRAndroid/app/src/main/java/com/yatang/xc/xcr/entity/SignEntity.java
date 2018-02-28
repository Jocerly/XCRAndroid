package com.yatang.xc.xcr.entity;

/**
 * SignEntity 签到每一天的对象
 * Created by dengjiang on 2017/7/8.
 */
public class SignEntity {
    private int dayOfMonth;
    private int dayType;

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }
}
