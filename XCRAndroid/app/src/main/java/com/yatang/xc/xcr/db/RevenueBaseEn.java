package com.yatang.xc.xcr.db;

import java.util.List;

/**
 * Created by zengxiaowen on 2017/8/11.
 */

public class RevenueBaseEn {
    private List<RevenueEntity> RevenueList; //门店列表数据
    private List<Double> RevenueSevenDay; //近7天营业收入
    private List<Double> ProfitSevenDay; //近7天营业利润

    public List<RevenueEntity> getRevenueList() {
        return RevenueList;
    }

    public void setRevenueList(List<RevenueEntity> revenueList) {
        RevenueList = revenueList;
    }

    public List<Double> getRevenueSevenDay() {
        return RevenueSevenDay;
    }

    public void setRevenueSevenDay(List<Double> revenueSevenDay) {
        RevenueSevenDay = revenueSevenDay;
    }

    public List<Double> getProfitSevenDay() {
        return ProfitSevenDay;
    }

    public void setProfitSevenDay(List<Double> profitSevenDay) {
        ProfitSevenDay = profitSevenDay;
    }

    @Override
    public String toString() {
        return "RevenueBaseEn{" +
                "RevenueList=" + RevenueList +
                ", RevenueSevenDay=" + RevenueSevenDay +
                ", ProfitSevenDay=" + ProfitSevenDay +
                '}';
    }
}
