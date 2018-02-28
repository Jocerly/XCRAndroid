package com.yatang.xc.xcr.db;

import java.io.Serializable;

/**
 * Created by zengxiaowen on 2017/7/18.
 */

public class RevenueEntity implements Serializable{
    private double RevenueZhifubao; // 支付宝
    private double RevenueWeixin; // 微信
    private double CouponCash; // 现金
    private double RevenueCash; // 现金券
    private double RevenueAllValue; // 收入
    private double ProfitValue;  //利润
    private double DeliveryFee; //配送费
    private double GoodsNum; //商品
    private double Coupon; //优惠券

    public double getRevenueZhifubao() {
        return RevenueZhifubao;
    }

    public void setRevenueZhifubao(double revenueZhifubao) {
        RevenueZhifubao = revenueZhifubao;
    }

    public double getRevenueWeixin() {
        return RevenueWeixin;
    }

    public void setRevenueWeixin(double revenueWeixin) {
        RevenueWeixin = revenueWeixin;
    }

    public double getCouponCash() {
        return CouponCash;
    }

    public void setCouponCash(double couponCash) {
        CouponCash = couponCash;
    }

    public double getRevenueCash() {
        return RevenueCash;
    }

    public void setRevenueCash(double revenueCash) {
        RevenueCash = revenueCash;
    }

    public double getRevenueAllValue() {
        return RevenueAllValue;
    }

    public void setRevenueAllValue(double revenueAllValue) {
        RevenueAllValue = revenueAllValue;
    }

    public double getProfitValue() {
        return ProfitValue;
    }

    public void setProfitValue(double profitValue) {
        ProfitValue = profitValue;
    }

    public double getDeliveryFee() {
        return DeliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        DeliveryFee = deliveryFee;
    }

    public double getGoodsNum() {
        return GoodsNum;
    }

    public void setGoodsNum(double goodsNum) {
        GoodsNum = goodsNum;
    }

    public double getCoupon() {
        return Coupon;
    }

    public void setCoupon(double coupon) {
        Coupon = coupon;
    }

    @Override
    public String toString() {
        return "RevenueEntity{" +
                "RevenueZhifubao=" + RevenueZhifubao +
                ", RevenueWeixin=" + RevenueWeixin +
                ", CouponCash=" + CouponCash +
                ", RevenueCash=" + RevenueCash +
                ", RevenueAllValue=" + RevenueAllValue +
                ", ProfitValue=" + ProfitValue +
                ", DeliveryFee=" + DeliveryFee +
                ", GoodsNum=" + GoodsNum +
                ", Coupon=" + Coupon +
                '}';
    }
}
