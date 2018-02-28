package com.yatang.xc.xcr.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券详情
 * Created by zengxiaowen on 2017/8/31.
 */

public class CouponDetails implements Serializable{
    private String CouponID;//优惠券ID
    private String CouponBalance;//优惠券金额/折扣
    private int CouponType;//优惠券类型 0：满减券，1：折扣券
    private String UseCondition;//使用条件描述
    private String CouponDesc;//优惠券描述信息
    private int CouponStatus;//优惠券状态  0:未发布、1：进行中、2：已结束、3：已过期、4已下架(待定)
    private String StartTime;//开始时间
    private String EndTime;//结束时间
    private int MaxCountEveryOne;//每人限领
    private int TotalCount;//发券数量
    private int IsNewUserCanUse;//仅限新用户使用
    private int ReceivedCount;//已领取数量
    private int UsedCount;//已使用数量
    private int ReceivedUserCount;//领取人数
    private int Channel;//推广渠道 0：店铺页，1：领券中心
    private int Scope;//使用范围 0：全部商品，2：指定商品
    private List<Goods> goodses;

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

    public int getCouponStatus() {
        return CouponStatus;
    }

    public void setCouponStatus(int couponStatus) {
        CouponStatus = couponStatus;
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

    public int getMaxCountEveryOne() {
        return MaxCountEveryOne;
    }

    public void setMaxCountEveryOne(int maxCountEveryOne) {
        MaxCountEveryOne = maxCountEveryOne;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public int getIsNewUserCanUse() {
        return IsNewUserCanUse;
    }

    public void setIsNewUserCanUse(int isNewUserCanUse) {
        IsNewUserCanUse = isNewUserCanUse;
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

    public int getChannel() {
        return Channel;
    }

    public void setChannel(int channel) {
        Channel = channel;
    }

    public int getScope() {
        return Scope;
    }

    public void setScope(int scope) {
        Scope = scope;
    }

    public List<Goods> getGoodses() {
        return goodses;
    }

    public void setGoodses(List<Goods> goodses) {
        this.goodses = goodses;
    }

    //商品
    public class Goods{
        private String GoodsName;//商品名称
        private String GoodsCode;//商品条码
        private String GoodsPrice;//售价
        private String UnitName;//单位
        private String PicUrl; //商品图片

        public String getGoodsName() {
            return GoodsName;
        }

        public void setGoodsName(String goodsName) {
            GoodsName = goodsName;
        }

        public String getGoodsCode() {
            return GoodsCode;
        }

        public void setGoodsCode(String goodsCode) {
            GoodsCode = goodsCode;
        }

        public String getGoodsPrice() {
            return GoodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            GoodsPrice = goodsPrice;
        }

        public String getUnitName() {
            return UnitName;
        }

        public void setUnitName(String unitName) {
            UnitName = unitName;
        }

        public String getPicUrl() {
            return PicUrl;
        }

        public void setPicUrl(String picUrl) {
            PicUrl = picUrl;
        }

        @Override
        public String toString() {
            return "Goods{" +
                    "GoodsName='" + GoodsName + '\'' +
                    ", GoodsCode='" + GoodsCode + '\'' +
                    ", GoodsPrice='" + GoodsPrice + '\'' +
                    ", UnitName='" + UnitName + '\'' +
                    ", PicUrl='" + PicUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CouponDetails{" +
                "CouponID='" + CouponID + '\'' +
                ", CouponBalance='" + CouponBalance + '\'' +
                ", CouponType=" + CouponType +
                ", UseCondition='" + UseCondition + '\'' +
                ", CouponDesc='" + CouponDesc + '\'' +
                ", CouponStatus=" + CouponStatus +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", MaxCountEveryOne=" + MaxCountEveryOne +
                ", TotalCount=" + TotalCount +
                ", IsNewUserCanUse=" + IsNewUserCanUse +
                ", ReceivedCount=" + ReceivedCount +
                ", UsedCount=" + UsedCount +
                ", ReceivedUserCount=" + ReceivedUserCount +
                ", Channel=" + Channel +
                ", Scope=" + Scope +
                ", goodses=" + goodses +
                '}';
    }
}
