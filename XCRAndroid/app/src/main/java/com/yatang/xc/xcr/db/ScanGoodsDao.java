package com.yatang.xc.xcr.db;

import android.content.ContentValues;
import android.content.Context;

import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品临时数据库
 *
 * @author Jocerly
 */
public class ScanGoodsDao extends DBSqliteHelper {
    public final static String TABLENAME = "ScanGoodsDao";

    public final static String GOODSID = "GoodsId";//商品Id
    public final static String GOODSNAME = "GoodsName";//商品名称
    public final static String GOODSPRICE = "GoodsPrice";//商品价格
    public final static String COSTPRICE = "CostPrice";//商品成本价
    public final static String NEWGOODSPRICE = "NewGoodsPrice";//商品新价格
    public final static String GOODSCODE = "GoodsCode";//商品一维码
    public final static String GOODSSTATUE = "GoodsStatue";//商品类型
    public final static String UNITNAME = "UnitName";//商品单位

    public final static String SQL = "create table IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " integer primary key autoincrement,"
            + GOODSID + " varchar(40),"
            + GOODSNAME + " varchar(256),"
            + GOODSPRICE + " varchar(20),"
            + COSTPRICE + " varchar(20),"
            + NEWGOODSPRICE + " varchar(20),"
            + GOODSCODE + " varchar(200),"
            + GOODSSTATUE + " varchar(5),"
            + UNITNAME + " varchar(100))";

    public ScanGoodsDao(Context context) {
        super(context, TABLENAME);
    }

    /**
     * 新增数据
     *
     * @return
     */
    public boolean doAdd(String GoodsId, String GoodsName, String GoodsPrice, String CostPrice, String NewGoodsPrice, String GoodsCode, String UnitName, String GoodsStatue) {
        if (checkGoodsIdIsExists(GoodsCode)) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(GOODSID, StringUtils.isEmpty(GoodsId) ? "" : GoodsId);
            values.put(GOODSNAME, StringUtils.isEmpty(GoodsName) ? "" : GoodsName);
            values.put(GOODSPRICE, StringUtils.isEmpty(GoodsPrice) ? "" : Common.doubleFormat(Double.parseDouble(GoodsPrice)));
            values.put(COSTPRICE, StringUtils.isEmpty(CostPrice) ? "" : Common.doubleFormat(Double.parseDouble(CostPrice)));
            values.put(NEWGOODSPRICE, StringUtils.isEmpty(NewGoodsPrice) ? "" : Common.doubleFormat(Double.parseDouble(NewGoodsPrice)));
            values.put(GOODSCODE, StringUtils.isEmpty(GoodsCode) ? "" : GoodsCode);
            values.put(GOODSSTATUE, StringUtils.isEmpty(GoodsStatue) ? "" : GoodsStatue);
            values.put(UNITNAME, StringUtils.isEmpty(UnitName) ? "" : UnitName);
            JCLoger.debug("values" + values.toString());
            super.doAdd(values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查数据时候存在
     *
     * @param GoodsCode
     * @return
     */
    public boolean checkGoodsIdIsExists(String GoodsCode) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{GOODSCODE}, new String[]{GoodsCode});
            if (list.size() != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查数据时候存在
     *
     * @param GoodsCode
     * @return
     */
    public boolean checkGoodsCodeIsExists(String GoodsCode) {
        try {
            List<ConcurrentHashMap<String, String>> list = super.doList(new String[]{GOODSCODE}, new String[]{GoodsCode});
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
        return super.doList(null, null, true);
    }

    /**
     * 修改数据
     */
    public void updateGoodsMsgByCode(String GoodsId, String GoodsName, String GoodsPrice, String NewGoodsPrice, String GoodsCode, String UnitName, String GoodsStatue) {
        ContentValues values = new ContentValues();
        values.put(GOODSID, GoodsId);
        values.put(GOODSNAME, GoodsName);
        values.put(GOODSPRICE, Common.doubleFormat(Double.parseDouble(GoodsPrice)));
        values.put(NEWGOODSPRICE, Common.doubleFormat(Double.parseDouble(NewGoodsPrice)));
        values.put(UNITNAME, UnitName);
        values.put(GOODSSTATUE, GoodsStatue);
        super.doUpdate(values, new String[]{GOODSCODE}, new String[]{GoodsCode});
    }

    /**
     * 修改数据
     */
    public void updateGoodsMsgById(String GoodsId, String GoodsName, String GoodsPrice, String NewGoodsPrice, String GoodsCode, String UnitName, String GoodsStatue) {
        ContentValues values = new ContentValues();
        values.put(GOODSNAME, GoodsName);
        values.put(GOODSPRICE, Common.doubleFormat(Double.parseDouble(GoodsPrice)));
        values.put(NEWGOODSPRICE, Common.doubleFormat(Double.parseDouble(NewGoodsPrice)));
        values.put(GOODSCODE, GoodsCode);
        values.put(GOODSSTATUE, GoodsStatue);
        values.put(UNITNAME, UnitName);
        super.doUpdate(values, new String[]{GOODSID}, new String[]{GoodsId});
    }

    /**
     * 修改数据——价格
     */
    public void updateGoodsPriceById(String GoodsId, String NewGoodsPrice) {
        ContentValues values = new ContentValues();
        values.put(NEWGOODSPRICE, Common.doubleFormat(Double.parseDouble(NewGoodsPrice)));
        super.doUpdate(values, new String[]{GOODSID}, new String[]{GoodsId});
    }

    /**
     * 修改数据——价格
     */
    public void updateGoodsPriceByCode(String GoodsCode, String NewGoodsPrice) {
        ContentValues values = new ContentValues();
        values.put(NEWGOODSPRICE, Common.doubleFormat(Double.parseDouble(NewGoodsPrice)+0.001, 2).toString());
        super.doUpdate(values, new String[]{GOODSCODE}, new String[]{GoodsCode});
    }

    /**
     * 获取商品条数
     *
     * @return
     */
    public int getGoodsNum() {
        return (int) super.getDBNum(TABLENAME);
    }

    /**
     * 删除一条记录
     *
     * @return
     */
    public boolean deleteByGoodsId(String GoodsId) {
        try {
            super.doDelete(new String[]{GOODSID}, new String[]{GoodsId});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除一条记录
     *
     * @return
     */
    public boolean deleteByGoodsCode(String GoodsCode) {
        try {
            super.doDelete(new String[]{GOODSCODE}, new String[]{GoodsCode});
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
