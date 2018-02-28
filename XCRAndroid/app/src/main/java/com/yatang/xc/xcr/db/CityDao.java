package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 城市数据表
 * Created by Jocerly on 2017/8/3.
 */

public class CityDao extends DBSqliteHelper {
    public final static String TABLENAME = "CityDao";

    public final static String PROVINCEID = "ProvinceId";//省份Id
    public final static String CITYID = "CityId";//城市Id
    public final static String CITY = "City";//城市

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + CITYID + " varchar(20),"
            + PROVINCEID + " varchar(20),"
            + CITY + " varchar(100))";

    public CityDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String CityId, String ProvinceId, String City) {
        if (checkExists(CityId)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(CITYID, CityId);
            values.put(PROVINCEID, ProvinceId);
            values.put(CITY, City);
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
     * @param CityId
     * @return
     */
    public boolean checkExists(String CityId) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{CITYID}, new String[]{CityId});
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
     * 根据省份ID获取数据
     * @param ProvinceId 省份ID
     * @return
     */
    public ArrayList<ConcurrentHashMap<String, String>> getAllDataByProvinceId(String ProvinceId) {
        return super.doList(new String[]{PROVINCEID}, new String[]{ProvinceId});
    }

    /**
     * 获取数据表数据条数
     * @return
     */
    public long getDBNum() {
        return super.getDBNum(TABLENAME);
    }
}
