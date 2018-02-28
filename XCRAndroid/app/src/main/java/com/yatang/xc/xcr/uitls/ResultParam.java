package com.yatang.xc.xcr.uitls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户封装服务器端传递过来的json数据
 *
 * @author Jocerly
 */
public class ResultParam implements Serializable {

    private static final long serialVersionUID = 4842114200596702271L;

    public String resultId;//请求是否成功
    public String message;//提示信息
    public int totalpage;//总页数

    public ArrayList<ConcurrentHashMap<String, String>> listData;//列表数据
    public ConcurrentHashMap<String, String> mapData;//单条数据

    private static ResultParam instance;

    private ResultParam() {
    }

    public static ResultParam getInstance() {
        if (instance == null) {
            instance = new ResultParam();
            instance.listData = new ArrayList<ConcurrentHashMap<String, String>>();
            instance.mapData = new ConcurrentHashMap<String, String>();
        }
        instance.resultId = "";//请求是否成功
        instance.message = "";//提示信息
        instance.totalpage = 1;
        instance.mapData.clear();
        instance.listData.clear();
        return instance;
    }

}
