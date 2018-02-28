package com.yatang.xc.xcr.entity;

/**
 * 添加活动列表对象
 * Created by dengjiang on 2017/10/11.
 */

public class PromotionEntity {
    private String type;//活动类型
    private String name;//活动名称
    private String describe;//活动描述

    public PromotionEntity(String type, String name, String describe) {
        this.type = type;
        this.name = name;
        this.describe = describe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
