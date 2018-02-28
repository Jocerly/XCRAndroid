package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类
 * Created by Jocerly on 2017/5/22.
 */

public class ClassifyDao extends DBSqliteHelper {
    public final static String TABLENAME = "ClassifyDao";

    public final static String CALSSIFYID = "ClassifyId";//三级分类Id
    public final static String CALSSIFFIRSTYID = "ClassifyFirstId";//一级级分类Id
    public final static String CALSSIFYNAME = "ClassifyName";//三级分类名称
    public final static String CALSSIFYPIC = "ClassifyPic";//三级分类图片

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + CALSSIFYID + " varchar(20),"
            + CALSSIFFIRSTYID + " varchar(20),"
            + CALSSIFYNAME + " varchar(1000),"
            + CALSSIFYPIC + " varchar(1000))";

    public ClassifyDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String ClassifyId, String ClassifyFirstId, String ClassifyName, String ClassifyPic) {
        if (checkExists(ClassifyId)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(CALSSIFYID, ClassifyId);
            values.put(CALSSIFFIRSTYID, ClassifyFirstId);
            values.put(CALSSIFYNAME, ClassifyName);
            values.put(CALSSIFYPIC, ClassifyPic);
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
     * @param ClassifyId
     * @return
     */
    public boolean checkExists(String ClassifyId) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{CALSSIFYID}, new String[]{ClassifyId});
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
    public ArrayList<ConcurrentHashMap<String, String>> getAllData(String ClassifyFirstId) {
        return super.doList(new String[]{CALSSIFFIRSTYID}, new String[]{ClassifyFirstId});
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
