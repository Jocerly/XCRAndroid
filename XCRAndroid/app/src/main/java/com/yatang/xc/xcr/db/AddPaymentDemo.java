package com.yatang.xc.xcr.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjiang on 2017/5/25.
 */

public class AddPaymentDemo {

    //是否被选中
    public boolean isSelected;
    //日期
    public String date;
    //当日所有的价格总和
    public String allPrice;
    //小票号
    public String NO;
    //下票时间
    public String time;
    //下票价格
    public String price;
    //小票类型 销售还是退回
    public String type;
    //是否是父级列表
    public boolean isParent;
    //包含的所有子列表
    public List<AddPaymentDemo> childs;
    //属于哪个父级
    public AddPaymentDemo mParent;
    //子列表是否课件
    public boolean isvisiable;
    //父级控件是否打开
    public boolean isOpen;

    public AddPaymentDemo(String NO, String time, String price, String type,AddPaymentDemo parent) {
        this.isSelected = false;
        this.NO = NO;
        this.time = time;
        this.price = price;
        this.type = type;
        this.isParent = false;
        this.mParent = parent;
        this.isvisiable = false;

    }

    public AddPaymentDemo(String date, String allPrice){
        this.date = date;
        this.allPrice = allPrice;
        this.isSelected = false;
        this.childs = new ArrayList<>();
        this.isParent = true;
        this.isvisiable = true;
        this.isOpen = false;
    }

    public void addChild(AddPaymentDemo demo) {
        childs.add(demo);
    }
}
