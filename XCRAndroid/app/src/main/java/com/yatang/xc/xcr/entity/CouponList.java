package com.yatang.xc.xcr.entity;

/**
 * 优惠券列表
 * Created by zengxiaowen on 2017/8/31.
 */

public class CouponList {
    private String CouponID; //优惠券ID
    private String CouponBalance;//优惠券金额
    private int CouponType;//优惠券类型  0：满减券，1：折扣券
    private String UseCondition;//使用条件描述
    private String CouponDesc;//优惠券描述信息
    private String StartTime;//开始时间
    private String EndTime;//结束时间
    private int CouponStatus;//优惠券状态  0:未发布、1：进行中、2：已结束、3：已过期、4已下架(待定)
    private int TotalCount;//发券数量
    private int ReceivedCount;//已领取数量
    private int UsedCount;//已使用数量
    private int ReceivedUserCount;//领取人数

    public String getCouponID() {
        return CouponID;
    }

    public void setCouponID(String couponID) {
        CouponID = couponID;
    }

    public String getCouponBalance() {
        return CouponBalance;
    }

    public void setCouponBalance(String couponBalance) {
        CouponBalance = couponBalance;
    }

    public int getCouponType() {
        return CouponType;
    }

    public void setCouponType(int couponType) {
        CouponType = couponType;
    }

    public String getUseCondition() {
        return UseCondition;
    }

    public void setUseCondition(String useCondition) {
        UseCondition = useCondition;
    }

    public String getCouponDesc() {
        return CouponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        CouponDesc = couponDesc;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getCouponStatus() {
        return CouponStatus;
    }

    public void setCouponStatus(int couponStatus) {
        CouponStatus = couponStatus;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public int getReceivedCount() {
        return ReceivedCount;
    }

    public void setReceivedCount(int receivedCount) {
        ReceivedCount = receivedCount;
    }

    public int getUsedCount() {
        return UsedCount;
    }

    public void setUsedCount(int usedCount) {
        UsedCount = usedCount;
    }

    public int getReceivedUserCount() {
        return ReceivedUserCount;
    }

    public void setReceivedUserCount(int receivedUserCount) {
        ReceivedUserCount = receivedUserCount;
    }

    @Override
    public String toString() {
        return "CouponList{" +
                "CouponID='" + CouponID + '\'' +
                ", CouponBalance='" + CouponBalance + '\'' +
                ", CouponType=" + CouponType +
                ", UseCondition='" + UseCondition + '\'' +
                ", CouponDesc='" + CouponDesc + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", CouponStatus=" + CouponStatus +
                ", TotalCount=" + TotalCount +
                ", ReceivedCount=" + ReceivedCount +
                ", UsedCount=" + UsedCount +
                ", ReceivedUserCount=" + ReceivedUserCount +
                '}';
    }
}
