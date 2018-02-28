package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 编辑商品
 * Created by Jocerly on 2017/5/25.
 */
@ContentView(R.layout.activity_add_goods)
public class EditGoodsActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textGoodsStatue)
    private TextView textGoodsStatue;
    @BindView(id = R.id.editGoodsName)
    private EditText editGoodsName;
    @BindView(id = R.id.editGoodsCode)
    private EditText editGoodsCode;
    @BindView(id = R.id.editGoodsPrice)
    private EditText editGoodsPrice;
    @BindView(id = R.id.textUnitName)
    private TextView textUnitName;
    @BindView(id = R.id.editGoodsCostPrice)
    private EditText editGoodsCostPrice;

    @BindView(id = R.id.btnScan, click = true)
    private Button btnScan;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    @BindView(id = R.id.textTip)
    private TextView textTip;
    @BindView(id = R.id.llUnitName, click = true)
    private LinearLayout llUnitName;
    @BindView(id = R.id.textCostTip)
    private TextView textCostTip;

    private HashMap<String, String> mapData = new HashMap<>();

    /**
     * 价格栏小数点后最大允许输入的位数
     */
    private final int PRICE_DECIMAL_DIGITALS = 2;

    @Override
    public void initWidget() {
        textTitle.setText("编辑商品");
        btnRight.setVisibility(View.GONE);
        btnRight.setText("新增记录");
        btnScan.setVisibility(View.GONE);
        editGoodsCode.setEnabled(false);
        editGoodsCostPrice.setEnabled(false);
        llUnitName.setEnabled(false);
        textUnitName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        textTip.setText("提示:更新的商品信息将在10分钟内下发，请在pos终端及时更新!");
        textCostTip.setVisibility(View.VISIBLE);
        textCostTip.setText("成本价为每次商品入库后的加权平均值");
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mapData = (HashMap<String, String>) bundle.getSerializable("mapData");
            if (mapData != null) {
                editGoodsName.setText(mapData.get("GoodsName"));
                editGoodsCode.setText(mapData.get("GoodsCode"));
                textUnitName.setText(mapData.get("UnitName"));
                if (StringUtils.isEmpty(mapData.get("CostPrice"))) {
                    editGoodsCostPrice.setText(" ");
                } else {
                    editGoodsCostPrice.setText(mapData.get("CostPrice"));
                }
                if (StringUtils.isEmpty(mapData.get("GoodsPrice"))) {
                    editGoodsPrice.setText(mapData.get(""));
                } else {
                    editGoodsPrice.setText(Common.formatFloat(mapData.get("GoodsPrice")));
                }
            }
        }

//        editUnitName.addTextChangedListener(new TextWatcher() {
//            String tmp = "";
//            String digits = "/\\()@!~#$%^&[]_-+=:*?<>|\"\n\t";
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                tmp = s.toString();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                editUnitName.setSelection(s.length());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String str = s.toString();
//                if (str.equals(tmp)) {
//                    return;
//                }
//                StringBuffer sb = new StringBuffer();
//                for (int i = 0; i < str.length(); i++) {
//                    if (digits.indexOf(str.charAt(i)) < 0) {
//                        sb.append(str.charAt(i));
//                    }
//                }
//                tmp = sb.toString();
//                editUnitName.setText(tmp);
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                skipActivity(aty, AddGoodsListActivity.class);
                break;
            case R.id.btnSave:
                String goodsPrice = editGoodsPrice.getText().toString().trim();
                String goodsName = editGoodsName.getText().toString().trim();
                String unitName = textUnitName.getText().toString().trim();
                if (StringUtils.isEmpty(goodsName)) {
                    toast("请输入商品名称");
                    return;
                } else if (goodsName.length() > 30) {
                    toast("商品名称最多30个字符");
                    return;
                }
                if (mapData == null) {
                    toast("数据异常");
                    return;
                }
                if (StringUtils.isEmpty(goodsPrice)) {
                    toast("请输入售价");
                    return;
                }else if (isPriceOK(goodsPrice).length()>0){
                    return;
                }
                mapData.put("NewGoodsPrice", goodsPrice);
                mapData.put("GoodsName", goodsName);
                mapData.put("UnitName", unitName);
                ArrayList<HashMap<String, String>> listDataTmp = new ArrayList<>();
                listDataTmp.add(mapData);
                JSONArray jsonArray = new JSONArray(listDataTmp);
                doModifyGoodsPrices(jsonArray);
                break;
            case R.id.llUnitName:
                showActivityForResult(aty, UnitActivity.class, Constants.ForResult.EDIT_GOODS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.ForResult.EDIT_GOODS:
                if (resultCode == RESULT_OK && data != null){
                    String unitName = data.getExtras().getString("unitName");
                    textUnitName.setText(unitName);
                }
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

    /**
     * 判断售价的长度和小数点后允许的位数
     * @param price 用户输入的价格
     * @return 返回错误提示 如果长度为0 则没有错误
     */
    private String isPriceOK(String price) {
        String result = "";
        boolean isLengthOk = true;
        boolean isPointOK = true;
        int position = 0;
        int length = price.length();
        if(price.contains(".")) {
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
        if(!isLengthOk) {
            result = "售价过大，请修改";
            if(!isPointOK) {
                result = "售价过大，小数点后面最多2位";
            }
        }else {
            if(!isPointOK) {
                result = "售价最多两位小数";
            }
        }
        if(result.length()>0) {
            toast(result);
        }
        return result;
    }

    /**
     * 过滤除了汉字，英文字母，数字和°.()（）%-*#+、/的其他特殊字符
     * @param str
     * @return
     */
    public boolean isHava(String str) {
        if (str.contains("&")){
            return false;
        }
        String regEx = "[\\u4e00-\\u9fa5°#%*、/+——（）#%*-./+／＋－％＃＊℃()a-zA-Z\\d]+";
        Pattern p = Pattern.compile(regEx);
        if (p.matcher(str).matches()){
            return true;
        }else {
            return false;
        }


    }
}
