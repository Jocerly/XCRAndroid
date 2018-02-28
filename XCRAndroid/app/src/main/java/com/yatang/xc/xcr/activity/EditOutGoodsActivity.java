package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.CheckSwitchButton;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外送商品上下架和调价页面
 * Created by lusha on 2017/07/18.
 */
@ContentView(R.layout.activity_add_goods)
public class EditOutGoodsActivity extends BaseActivity {
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
    @BindView(id = R.id.textBusinessStatus)
    private TextView textBusinessStatus;
    @BindView(id = R.id.cbBusinessStatus)
    private CheckSwitchButton cbBusinessStatus;
    @BindView(id = R.id.llClassifyFirstName)
    private LinearLayout llClassifyFirstName;
    @BindView(id = R.id.editClassifyFirstName)
    private TextView editClassifyFirstName;
    @BindView(id = R.id.rlFrametype)
    private RelativeLayout rlFrametype;
    @BindView(id = R.id.line)
    private View line;
    @BindView(id = R.id.lline)
    private LinearLayout lline;
    @BindView(id = R.id.lineClassifyFirstName)
    private LinearLayout lineClassifyFirstName;
    @BindView(id = R.id.lineClassifySecName)
    private LinearLayout lineClassifySecName;
    @BindView(id = R.id.lineGoodsCostPrice)
    private LinearLayout lineGoodsCostPrice;
    @BindView(id = R.id.llClassifySecName)
    private LinearLayout llClassifySecName;
    @BindView(id = R.id.llClassifyThirdName)
    private LinearLayout llClassifyThirdName;
    @BindView(id = R.id.llGoodsCostPrice)
    private LinearLayout llGoodsCostPrice;
    @BindView(id = R.id.editClassifySecName)
    private EditText editClassifySecName;
    @BindView(id = R.id.editClassifyThirdName)
    private EditText editClassifyThirdName;
    @BindView(id = R.id.editGoodsCostPrice)
    private EditText editGoodsCostPrice;

    @BindView(id = R.id.btnScan, click = true)
    private Button btnScan;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    @BindView(id = R.id.textTip)
    private TextView textTip;

    private String type;
    private String code;
    private JSONArray jsonArray;
    /**
     * 价格栏小数点后最大允许输入的位数
     */
    private final int PRICE_DECIMAL_DIGITALS = 2;

    @Override
    public void initWidget() {
        textTitle.setText("编辑商品");
        btnRight.setVisibility(View.GONE);
        btnScan.setVisibility(View.GONE);
        editGoodsName.setEnabled(false);
        editGoodsCode.setEnabled(false);
        editClassifySecName.setEnabled(false);
        editClassifyThirdName.setEnabled(false);
        editClassifyFirstName.setEnabled(false);
        textUnitName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0 ,0);
        textTip.setVisibility(View.GONE);
        cbBusinessStatus.setOnCheckedChangeListener(onCheckedChangeListener);
        lline.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        lineClassifyFirstName.setVisibility(View.VISIBLE);
        lineClassifySecName.setVisibility(View.VISIBLE);
        lineGoodsCostPrice.setVisibility(View.VISIBLE);
        llClassifyFirstName.setVisibility(View.VISIBLE);
        rlFrametype.setVisibility(View.VISIBLE);
        llClassifySecName.setVisibility(View.VISIBLE);
        llClassifyThirdName.setVisibility(View.VISIBLE);
        llGoodsCostPrice.setVisibility(View.VISIBLE);
        btnSave.setText("保存");
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString("code");
            doModifyGoodsFrameType();
            getGoodsOutDetial();
        }
    }

    /**
     * 商品类别的文字显示
     *
     * @param goodsStatue
     */
    public void setGoodsStatue(final int goodsStatue) {
        switch (goodsStatue) {
            case 1:
                textGoodsStatue.setText("常规商品");
                break;
            case 2:
                textGoodsStatue.setText("打包商品");
                break;
            case 3:
                textGoodsStatue.setText("附属商品");
                break;
            case 4:
                textGoodsStatue.setText("称重商品");
                break;
            case 5:
                textGoodsStatue.setText("加工称重商品");
                break;
            case 6:
                textGoodsStatue.setText("加工非称重商品");
                break;
        }
    }


    /**
     * 上下架状态开关回调
     */
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) { //未选中
                setShopBusiness(0);
            } else { //选中
                setShopBusiness(1);
            }
        }
    };

    /**
     * 设置商品状态
     *
     * @param frameType 1：售卖中，0：待上架
     */
    private void setFrameType(int frameType) {
        switch (frameType) {
            case 1:
                textBusinessStatus.setText("售卖中");
                break;
            case 0:
                textBusinessStatus.setText("待上架");
                break;
        }
    }

    /**
     * 数据处理
     */
    private void doModifyGoodsFrameType() {
        List<ConcurrentHashMap<String, String>> listTmpData = new ArrayList<>();
        ConcurrentHashMap<String, String> map = null;
        map = new ConcurrentHashMap<>();
        map.put("GoodsCode", code);
        listTmpData.add(map);

        jsonArray = new JSONArray(listTmpData);
        map = null;
        listTmpData = null;
    }

    /**
     * 设置商品状态
     *
     * @param frameType 1：售卖中，0：待上架
     */
    public void setShopBusiness(final int frameType) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("FrameType", frameType);
        params.put("GoodsList", jsonArray);
        params.put("GoodsCode", code);
        httpRequestService.doRequestData(aty, "User/ModifyGoodsFrameType", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    setFrameType(frameType);
                    switch (frameType) {
                        case 1:
                            toast("上架成功");
                            break;
                        case 0:
                            toast("下架成功");
                            break;
                    }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btnSave:
                String goodsPrice = editGoodsPrice.getText().toString().trim();
                String goodsCostPrice = editGoodsCostPrice.getText().toString().trim();
                if (isPriceOK(goodsCostPrice, 1).length()>0){
                    return;
                }
                if (goodsCostPrice.startsWith(".") || goodsCostPrice.endsWith(".")){
                    toast("成本价输入格式有误");
                    return;
                }
                if (StringUtils.isEmpty(goodsPrice)) {
                    toast("请输入商品售价");
                    return;
                } else if (isPriceOK(goodsPrice, 2).length() > 0) {
                    return;
                }
                if (goodsPrice.startsWith(".") || goodsPrice.endsWith(".")){
                    toast("售价输入格式有误");
                    return;
                }
                doModifyOutGoodsPrice(goodsPrice, goodsCostPrice);
                break;
        }
    }


    /**
     * 外送商品上传调价记录
     */
    private void doModifyOutGoodsPrice(String newGoodsPrice, final String goodsCostPrice) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", code);
        params.put("NewGoodsPrice", newGoodsPrice);
        params.put("CostPrice", goodsCostPrice);
        httpRequestService.doRequestData(aty, "User/ModifyOutGoodsPrice", params, new HttpRequestService.IHttpRequestCallback() {
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
     * 获取外送商品详情
     */
    public void getGoodsOutDetial() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", code);
        httpRequestService.doRequestData(aty, "User/GoodsOutDetial", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    editGoodsName.setText(resultParam.mapData.get("GoodsName"));
                    editGoodsCode.setText(resultParam.mapData.get("GoodsCode"));
                    textUnitName.setText(StringUtils.isEmpty(resultParam.mapData.get("UnitName")) ? " " : resultParam.mapData.get("UnitName"));
                    editGoodsPrice.setText(Common.formatFloat(resultParam.mapData.get("GoodsPrice")));
                    editClassifyFirstName.setText(resultParam.mapData.get("ClassifyFirstName"));
                    editClassifyThirdName.setText(resultParam.mapData.get("ClassifyThirdName"));
                    editClassifySecName.setText(resultParam.mapData.get("ClassifySecName"));
                    editGoodsCostPrice.setText(StringUtils.isEmpty(resultParam.mapData.get("GoodsCostPrice")) ? " " :Common.formatFloat(resultParam.mapData.get("GoodsCostPrice")));
                    int frameType = Integer.parseInt(resultParam.mapData.get("FrameType"));
                    setFrameType(frameType);
                    cbBusinessStatus.setmBroadcasting(true);
                    cbBusinessStatus.setChecked(0 == frameType);

                    setGoodsStatue(Integer.parseInt(resultParam.mapData.get("GoodsOutStatue")));
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
    private String isPriceOK(String price, int type) {
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
            switch (type) {
                case 1:
                    result="成本价过大，请修改";
                    break;
                case 2:
                    result = "售价过大，请修改";
                    break;
            }
            if (!isPointOK) {
                switch (type) {
                    case 1:
                        result="成本价过大，小数点后面最多2位";
                        break;
                    case 2:
                        result = "售价过大，小数点后面最多2位";
                        break;
                }
            }
        } else {
            if (!isPointOK) {
                switch (type) {
                    case 1:
                        result="成本价小数点后面最多2位";
                        break;
                    case 2:
                        result = "售价小数点后面最多2位";
                        break;
                }
            }
        }
        if (result.length() > 0) {
            toast(result);
        }
        return result;
    }

}
