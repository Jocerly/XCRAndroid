package com.yatang.xc.xcr.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.ScanCodeConfirmAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.ScanGoodsDao;
import com.yatang.xc.xcr.dialog.ChangeGoodsPriceDialog;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫码调价确定Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_scan_code_confirm)
public class ScanCodeConfirmActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.recyclerViewGoods)
    private RecyclerView recyclerViewGoods;
    @BindView(id = R.id.btnOK, click = true)
    private TextView btnOK;

    private ScanCodeConfirmAdapter adapter;
    private ArrayList<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private ScanGoodsDao goodsDao;
    private ChangeGoodsPriceDialog dialog;
    private NomalDialog nomalDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("商品调价");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品调价");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("商品调价");
        btnRight.setVisibility(View.GONE);
        detachLayout();
    }

    @Override
    public void initData() {
        goodsDao = new ScanGoodsDao(aty);
        adapter = new ScanCodeConfirmAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewGoods.setLayoutManager(layoutManager);
        recyclerViewGoods.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerViewGoods.setAdapter(adapter);

        dialog = new ChangeGoodsPriceDialog(aty);
        dialog.setOnChangeGoodsPriceClickListener(clickListener);

        refrashData();
    }

    public void getDialogEixt() {
        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
            @Override
            public void onOkClick() {
                finish();
            }
        });
        nomalDialog.show("还未提交，确定要关闭吗？");
    }


    /**
     * 刷新数据
     */
    private void refrashData() {
        listData.clear();
        listData.addAll(goodsDao.getAllData());
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击列表item回调
     */
    ScanCodeConfirmAdapter.OnItemClickListener onItemClickListener = new ScanCodeConfirmAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String goodsId, String goodsCode, String goodsName, String goodsPrice) {
            if (StringUtils.isEmpty(goodsId)) {
                Bundle bundle = new Bundle();
                bundle.putString("code", goodsCode);
                bundle.putBoolean("needReturn", true);
                skipActivityForResult(aty, AddGoodsActivity.class, bundle, Constants.ForResult.ADD_GOODS);
            } else {
                dialog.show(goodsId, goodsCode, goodsName, goodsPrice);
            }
        }
    };

    /**
     * 改价弹框回调
     */
    ChangeGoodsPriceDialog.OnChangeGoodsPriceClickListener clickListener = new ChangeGoodsPriceDialog.OnChangeGoodsPriceClickListener() {
        @Override
        public void onOkClick(String goodsId, String goodsCode, String price) {
            goodsDao.updateGoodsPriceByCode(goodsCode, price);
            refrashData();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                getDialogEixt();
                break;
            case R.id.btnOK:
                checkData();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getDialogEixt();

        }
        return false;
    }

    private void checkData() {
        ArrayList<ConcurrentHashMap<String, String>> listDataTmp = new ArrayList<>();
        for (ConcurrentHashMap<String, String> map : listData) {
            if (!(StringUtils.isEmpty(map.get("NewGoodsPrice")) && "该商品不存在".equals(map.get("GoodsName")))) {
                listDataTmp.add(map);
            }
        }
        if (listDataTmp.size() <= 0) {
            toast("没有可调价的商品");
        } else {
            JSONArray jsonArray = new JSONArray(listDataTmp);
            doModifyGoodsPrices(jsonArray);
        }
    }

    /**
     * 上传调价记录
     */
    private void doModifyGoodsPrices(JSONArray jsonArray) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsList", jsonArray);
        httpRequestService.doRequestData(aty, "User/ModifyGoodsPrices", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Firm_Goods_ChangeDone");
                    goodsDao.delete();
                    setResult(RESULT_OK);
                    finish();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.ADD_GOODS:
                if (resultCode == RESULT_OK) {
                    refrashData();
                }
                break;
        }
    }
}
