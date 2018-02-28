package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import org.jocerly.jcannotation.utils.JCLoger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索数据库
 * Created by Jocerly on 2017/5/17.
 */

public class SearchDao extends DBSqliteHelper {
    public final static String TABLENAME = "SearchDao";

    public final static String SEARCHTYPE = "SearchType";//搜索类型：1：商品，2：小票、3：小区/写字楼/学校、4：外送商品
    public final static String SEARCHMSG = "SearchMsg";//搜索内容

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + SEARCHTYPE + " varchar(5),"
            + SEARCHMSG + " varchar(1000))";

    public SearchDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String SearchType, String SearchMsg) {
        //如果存在 删除老的 存最新的
        checkExists(SearchType, SearchMsg);
        try {
            ContentValues values = new ContentValues();
            values.put(SEARCHTYPE, SearchType);
            values.put(SEARCHMSG, SearchMsg);
            JCLoger.debug("INSERT=" + SearchType + "  " + SearchMsg);
            super.doAdd(values);
            return true;
        } catch (Exception e) {
            JCLoger.debug(e.toString());
            return false;
        }
    }

    /**
     * 检查数据时候存在
     *
     * @param SearchMsg
     * @return
     */
    public boolean checkExists(String type, String SearchMsg) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{SEARCHMSG}, new String[]{SearchMsg});
            if (list.size() != 0) {
                delete(type + "", SearchMsg);
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
    public ArrayList<ConcurrentHashMap<String, String>> getAllData(String SearchType) {
        return super.doList(new String[]{SEARCHTYPE}, new String[]{SearchType}, true);
    }


    /**
     * 删除指定type 和 msg 的 数据
     *
     * @return
     */
    public boolean delete(String type, String msg) {
        try {
            super.doDelete(new String[]{SEARCHTYPE, SEARCHMSG}, new String[]{type, msg});
            doDeleteBySql("DELETE FROM sqlite_sequence WHERE name = '" + TABLENAME + "'");//将自增列归零
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除所有的记录
     *
     * @return
     */
    public boolean delete(String type) {
        try {
            super.doDelete(new String[]{SEARCHTYPE}, new String[]{type});
            doDeleteBySql("DELETE FROM sqlite_sequence WHERE name = '" + TABLENAME + "'");//将自增列归零
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
