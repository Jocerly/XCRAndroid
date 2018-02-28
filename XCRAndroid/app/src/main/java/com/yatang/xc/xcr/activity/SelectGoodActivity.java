package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjiang on 2017/10/16.
 */
@ContentView(R.layout.activity_selectgood)
public class SelectGoodActivity extends BaseActivity {
    public static final int SELECT_GOOD_CODE = 0;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.GoodName, click = true)
    private TextView GoodName;
    @BindView(id = R.id.oldPrice)
    private TextView oldPrice;
    @BindView(id = R.id.rbSpecialPrice)
    private RadioButton rbSpecialPrice;
    @BindView(id = R.id.rbDiscount)
    private RadioButton rbDiscount;
    @BindView(id = R.id.textType)
    private TextView textType;
    @BindView(id = R.id.textTypeValue)
    private EditText textTypeValue;
    @BindView(id = R.id.textRestrictionsNO)
    private EditText textRestrictionsNO;
    @BindView(id = R.id.textStock)
    private EditText textStock;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    private String type = "1";

    private String goodsId, goodsCode, goodsName, goodsPrice, unitName;
    private String typeValue, restrictionsNO, stock;
    private ArrayList<String> list_GoodsCode;

    @Override

    public void initWidget() {
        rbSpecialPrice.setOnCheckedChangeListener(onCheckedChangeListener);
        rbDiscount.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void initData() {
        textTitle.setText("添加折扣商品");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            list_GoodsCode = bundle.getStringArrayList("ListCode");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("添加折扣商品");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("添加折扣商品");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.GoodName:
                Bundle b = new Bundle();
                b.putInt("costType", 3);
                b.putStringArrayList("ListCode",list_GoodsCode);
                skipActivityForResult(aty, OutGoodsListActivity.class, b, SELECT_GOOD_CODE);
                break;
            case R.id.btnSave:
                checkDiscountData();
                break;
        }
    }

    private void checkDiscountData() {
        if ("请选择".equals(goodsName) || StringUtils.isEmpty(goodsName)) {
            toast("请选择商品");
            return;
        }
        if (StringUtils.isEmpty(goodsPrice)) {
            toast("商品原价不能为空");
            return;
        }
        typeValue = textTypeValue.getText().toString().trim();
        switch (type) {
            case "1":
                if (StringUtils.isEmpty(typeValue)) {
                    toast("请输入特价金额");
                    return;
                } else if (!isPointOK(typeValue, 2)) {
                    toast("特价金额最多两位小数");
                    return;
                } else if (Float.parseFloat(typeValue) <= 0f) {
                    toast("特价金额必须大于0");
                    return;
                } else if (Float.parseFloat(typeValue) > Float.parseFloat(goodsPrice)) {
                    toast("特价不能大于原价");
                    return;
                }
                break;
            case "2":
                if (StringUtils.isEmpty(typeValue)) {
                    toast("请输入折扣值");
                    return;
                } else if (!isPointOK(typeValue, 1)) {
                    toast("折扣值最多1位小数");
                    return;
                } else if (Float.parseFloat(typeValue) <= 0f) {
                    toast("折扣值必须大于0");
                    return;
                } else if (Float.parseFloat(typeValue) > 9.9f) {
                    toast("折扣值不得大于9.9");
                    return;
                }
                break;
        }

        restrictionsNO = textRestrictionsNO.getText().toString().trim();
        if (StringUtils.isEmpty(restrictionsNO)) {
            toast("请输入限购数量");
            return;
        } else if (Float.parseFloat(restrictionsNO) <= 0f) {
            toast("限购数量必须大于0");
            return;
        } else if (Float.parseFloat(restrictionsNO) > 1000f) {
            toast("限购数量不得大于1000");
            return;
        }

        stock = textStock.getText().toString().trim();
        if (StringUtils.isEmpty(stock)) {
            toast("请输入活动库存");
            return;
        } else if (Float.parseFloat(stock) < Float.parseFloat(restrictionsNO)) {
            toast("活动库存必须大于每单限购数量");
            return;
        } else if (Float.parseFloat(stock) > 100000f) {
            toast("限购数量不得大于10万");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("GoodsId", goodsId);
        intent.putExtra("GoodsCode", goodsCode);
        intent.putExtra("GoodsPrice", goodsPrice);
        intent.putExtra("GoodsName", goodsName);
        intent.putExtra("UnitName", unitName);

        intent.putExtra("Type", type);
        intent.putExtra("TypeValue", typeValue);
        intent.putExtra("RestrictionsNO", restrictionsNO);
        intent.putExtra("Stock", stock);
        setResult(RESULT_OK, intent);
        finish();

    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.rbSpecialPrice:
                    if (b) {
                        textType.setText("特价金额(元)");
                        textTypeValue.setText("");
                        textTypeValue.setHint("输入特价金额");
                        type = "1";
                    }
                    break;
                case R.id.rbDiscount:
                    if (b) {
                        textType.setText("折扣值(折)");
                        textTypeValue.setText("");
                        textTypeValue.setHint("输入折扣值，如9.8");
                        type = "2";
                    }
                    break;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_GOOD_CODE && data != null) {
                goodsId = String.valueOf(data.getExtras().get("GoodsId"));
                goodsCode = String.valueOf(data.getExtras().get("GoodsCode"));
                goodsName = String.valueOf(data.getExtras().get("GoodsName"));
                goodsPrice = String.valueOf(data.getExtras().get("GoodsPrice"));
                unitName = String.valueOf(data.getExtras().get("UnitName"));
                GoodName.setText(goodsName);
                oldPrice.setText(Common.formatFloat(goodsPrice));
            }
        }
    }

    /**
     * 判断售字符串小数点后允许的位数
     *
     * @param string 用户输入的字符串
     * @param temp   小数点后面允许的位数
     * @return 返回错误提示 如果长度为0 则没有错误
     */
    private boolean isPointOK(String string, int temp) {
        boolean isPointOK = true;
        int position = 0;
        int length = string.length();
        if (string.contains(".")) {
            position = string.indexOf(".");
            if (length - position > temp + 1) {
                isPointOK = false;
            }
        }
        return isPointOK;
    }

}
