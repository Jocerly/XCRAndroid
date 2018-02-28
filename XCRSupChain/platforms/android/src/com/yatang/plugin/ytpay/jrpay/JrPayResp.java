package com.yatang.plugin.ytpay.jrpay;

import java.io.Serializable;

/**
 * Created by liuping on 2017/11/15.
 */

public class JrPayResp implements Serializable{
    public String status;

    public interface ErrCode {
        int ERR_OK = 1;
    }
}
