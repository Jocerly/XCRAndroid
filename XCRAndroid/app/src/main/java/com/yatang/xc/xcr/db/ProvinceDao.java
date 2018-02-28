package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 省份数据表
 * Created by Jocerly on 2017/8/3.
 */

public class ProvinceDao extends DBSqliteHelper {
    public final static String TABLENAME = "ProvinceDao";

    public final static String PROVINCEID = "ProvinceId";//省份Id
    public final static String PROVINCE = "Province";//省份

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + PROVINCEID + " varchar(20),"
            + PROVINCE + " varchar(100))";

    public ProvinceDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String ProvinceId, String Province) {
        if (checkExists(ProvinceId)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(PROVINCEID, ProvinceId);
            values.put(PROVINCE, Province);
            super.doAdd(values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查数据时候存在
     *
     * @param ProvinceId
     * @return
     */
    public boolean checkExists(String ProvinceId) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{PROVINCEID}, new String[]{ProvinceId});
            if (list.size() != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public ArrayList<ConcurrentHashMap<String, String>> getAllData() {
        return super.doList(null, null);
    }

    /**
     * 获取数据表数据条数
     * @return
     */
    public long getDBNum() {
        return super.getDBNum(TABLENAME);
    }
}
