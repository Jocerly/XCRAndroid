package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一级分类
 * Created by Jocerly on 2017/5/22.
 */

public class ClassifyFirstDao extends DBSqliteHelper {
    public final static String TABLENAME = "ClassifyFirstDao";

    public final static String CALSSIFFIRSTYID = "ClassifyFirstId";//一级分类Id
    public final static String CALSSIFYFIRSTNAME = "ClassifyFirstName";//一级分类名称

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + CALSSIFFIRSTYID + " varchar(20),"
            + CALSSIFYFIRSTNAME + " varchar(1000))";

    public ClassifyFirstDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String ClassifyFirstId, String ClassifyFirstName) {
        if (checkExists(ClassifyFirstId)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(CALSSIFFIRSTYID, ClassifyFirstId);
            values.put(CALSSIFYFIRSTNAME, ClassifyFirstName);
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
     * @param ClassifyFirstId
     * @return
     */
    public boolean checkExists(String ClassifyFirstId) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{CALSSIFFIRSTYID}, new String[]{ClassifyFirstId});
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
     * 删除所有的记录
     *
     * @return
     */
    public boolean delete() {
        try {
            super.doDelete(null, null);
            doDeleteBySql("DELETE FROM sqlite_sequence WHERE name = '" + TABLENAME + "'");//将自增列归零
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
