package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 开户行数据表
 * Created by Jocerly on 2017/8/3.
 */

public class BankCardDao extends DBSqliteHelper {
    public final static String TABLENAME = "BankCardDao";

    public final static String BANKCARDID = "BankCardId";//银行Id
    public final static String BANKCARDNAME = "BankCardName";//银行名称

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + BANKCARDID + " varchar(20),"
            + BANKCARDNAME + " varchar(100))";

    public BankCardDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String BankCardId, String BankCardName) {
        if (checkExists(BankCardId)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(BANKCARDID, BankCardId);
            values.put(BANKCARDNAME, BankCardName);
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
     * @param BankCardId
     * @return
     */
    public boolean checkExists(String BankCardId) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{BANKCARDID}, new String[]{BankCardId});
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
     * 分页获取数据
     *
     * @return
     */
    public ArrayList<ConcurrentHashMap<String, String>> getAllDataByPage(int pageIndex, int pageSize) {
        return super.doList(null, null, pageIndex, pageSize);
    }

    /**
     * 模糊查询获取数据
     *
     * @return
     */
    public ArrayList<ConcurrentHashMap<String, String>> getAllData(String msg, int pageIndex, int pageSize) {
        String sql_sel = "SELECT * FROM " + TABLENAME + " where " + BANKCARDNAME
                + " like '%" + msg + "%'"
                + " order by " + ID + " limit " + pageSize + " offset " + (pageIndex - 1) * pageSize;//offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        return super.doQueryBySql2(sql_sel);
    }

    /**
     * 获取数据表数据条数
     *
     * @return
     */
    public long getDBNum() {
        return super.getDBNum(TABLENAME);
    }
}
