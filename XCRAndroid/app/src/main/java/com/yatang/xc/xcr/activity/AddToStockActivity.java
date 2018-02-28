package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.HashMap;

/**
 * 商品入库
 * Created by Jocerly on 2017/5/22.
 */
@ContentView(R.layout.activity_add_to_stock)
public class AddToStockActivity extends BaseActivity {
    private final int PRICE_DECIMAL_DIGITALS = 2;//价格栏小数点后最大允许输入的位数
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textGoodsName)
    private TextView textGoodsName;
    @BindView(id = R.id.textGoodsCode)
    private TextView textGoodsCode;
    @BindView(id = R.id.textGoodsClassfy)
    private TextView textGoodsClassfy;

    @BindView(id = R.id.textUnitName)
    private TextView textUnitName;
    @BindView(id = R.id.editGoodsCostPrice)
    private EditText editGoodsCostPrice;
    @BindView(id = R.id.editNum)
    private EditText editNum;
    @BindView(id = R.id.editStockPrice)
    private EditText editStockPrice;
    @BindView(id = R.id.btnScan, click = true)
    private Button btnScan;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;

    private NomalDialog dialog;

    private String code;
    private HashMap<String, String> mapData = new HashMap<>();
    private String goodsCostPrice, num, stockPrice;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("商品入库");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品入库");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("商品入库");
        btnRight.setVisibility(View.GONE);
        editGoodsCostPrice.addTextChangedListener(editChangedListener);
        editNum.addTextChangedListener(editChangedListener);
        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(onNoamlLickListene);
        editStockPrice.setEnabled(false);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mapData = (HashMap<String, String>) bundle.getSerializable("mapData");
            if (mapData != null) {
                textGoodsName.setText(mapData.get("GoodsName"));
                textGoodsCode.setText(mapData.get("GoodsCode"));
                textGoodsClassfy.setText(StringUtils.isEmpty(mapData.get("ClassifyId")) ? "无分类" : mapData.get("ClassifyName"));
                textUnitName.setText(mapData.get("UnitName"));
                editGoodsCostPrice.setText(mapData.get("CostPrice"));
            }
        }
    }

    TextWatcher editChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String goodsCostPrice = editGoodsCostPrice.getText().toString().trim();
            String num = editNum.getText().toString().trim();

            try {
                if (!StringUtils.isEmpty(goodsCostPrice) && !StringUtils.isEmpty(num)) {
                    editStockPrice.setText(Common.doubleFormat(Double.parseDouble(goodsCostPrice) * Integer.parseInt(num)));
                } else {
                    editStockPrice.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
                toast("数量过大!");
                editNum.setText("");
                editStockPrice.setText("");
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnSave:
                goodsCostPrice = editGoodsCostPrice.getText().toString().trim();
                num = editNum.getText().toString().trim();
                stockPrice = editStockPrice.getText().toString().trim();
                if (mapData == null) {
                    toast("数据异常");
                    return;
                }
                if (StringUtils.isEmpty(goodsCostPrice)) {
                    toast("请输入成本价");
                    return;
                } else if (isPriceOK(goodsCostPrice).length() > 0) {
                    return;
                }
                if (StringUtils.isEmpty(num)) {
                    toast("请输入入库数量");
                    return;
                }
                if (num.startsWith("-")) {
                    toast("请输入正确的入库数量");
                    return;
                }
                if (editStockPrice.getText().toString().indexOf(".") > 9) {
                    toast("入库金额过大");
                    return;
                }
                dialog.show(String.format(getResources().getString(R.string.add_to_stock_dialog), goodsCostPrice, num), "再次修改");
                break;
        }
    }

    /**
     * 商品入库
     */
    private void doAddToStock() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsId", mapData.get("GoodsId"));
        params.put("ClassifyId", mapData.get("ClassifyId"));
        params.put("ClassifyName", mapData.get("ClassifyName"));
        params.put("GoodsName", mapData.get("GoodsName"));
        params.put("GoodsCode", mapData.get("GoodsCode"));
        params.put("GoodsCostPrice", goodsCostPrice);
        params.put("UnitName", mapData.get("UnitName"));
        params.put("Num", num);
        params.put("StockPrice", stockPrice);
        httpRequestService.doRequestData(aty, "User/AddToStock", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Firm_Stock_Storage");
                    toast("入库成功");
                    skipActivity(aty, AddToStockListActivity.class);
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

    /**
     * 判断售价的长度和小数点后允许的位数
     *
     * @param price 用户输入的价格
     * @return 返回错误提示 如果长度为0 则没有错误
     */
    private String isPriceOK(String price) {
        String result = "";
        boolean isLengthOk = true;
        boolean isPointOK = true;
        int position = 0;
        int length = price.length();
        if (price.contains(".")) {
            position = price.indexOf(".");
            if (length - position > 3) {
                isPointOK = false;
            }
        }
        if (position > 0) {
            if (position > 9) {
                isLengthOk = false;
            }
        } else {
            if (length > 9) {
                isLengthOk = false;
            }
        }
        if (!isLengthOk) {
            result = "成本价过大，请修改";
            if (!isPointOK) {
                result = "成本价过大，小数点后面最多2位";
            }
        } else {
            if (!isPointOK) {
                result = "成本价小数点后面最多2位";
            }
        }
        if (result.length() > 0) {
            toast(result);
        }
        return result;
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            doAddToStock();
        }
    };
}
