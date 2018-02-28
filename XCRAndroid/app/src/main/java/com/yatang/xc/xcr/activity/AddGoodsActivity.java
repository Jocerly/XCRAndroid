package com.yatang.xc.xcr.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.ScanGoodsDao;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;

import java.util.regex.Pattern;

/**
 * 新增商品Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_add_goods)
public class AddGoodsActivity extends BaseActivity {
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
    private boolean needReturn = false;
    @BindView(id = R.id.btnScan, click = true)
    private Button btnScan;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    @BindView(id = R.id.textTip)
    private TextView textTip;
    @BindView(id = R.id.llGoodsCostPrice)
    private LinearLayout llGoodsCostPrice;
    @BindView(id = R.id.llUnitName, click = true)
    private LinearLayout llUnitName;
    private String costPrice = "";

    /**
     * 1：常规商品、2：附属商品、3：打包商品、4：称重商品
     */
    private int goodsStatue = 1;
    private String code;

    /**
     * 价格栏小数点后最大允许输入的位数
     */
    private final int PRICE_DECIMAL_DIGITALS = 2;

    private ScanGoodsDao goodsDao;
    private NomalDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("新增/编辑商品");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("新增/编辑商品");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("新增商品");
        btnRight.setVisibility(View.GONE);
        btnRight.setText("新增记录");
        textTip.setText("提示:更新的商品信息将在10分钟内下发，请在pos终端及时更新");
        llGoodsCostPrice.setVisibility(View.GONE);
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString("code");
            needReturn = bundle.getBoolean("needReturn", false);
        }
        if (StringUtils.isEmpty(code)) {
            editGoodsCode.setEnabled(true);
            btnScan.setVisibility(View.VISIBLE);
        } else {
            editGoodsCode.setText(code);
            editGoodsCode.setEnabled(false);
            btnScan.setVisibility(View.GONE);
            goodsDao = new ScanGoodsDao(aty);
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
                break;
            case R.id.btnScan:
                if (SystemTool.checkSelfPermission(aty, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.CAMERA}, Constants.Permission.CAMERA);
                } else {
                    showActivityForResult(aty, ScanActivity.class, Constants.ForResult.SCAN);
                }
                break;
            case R.id.btnSave:
                String goodsName = editGoodsName.getText().toString().trim();
                String goodsCode = editGoodsCode.getText().toString().trim();
                String goodsPrice = editGoodsPrice.getText().toString().trim();
                String unitName = textUnitName.getText().toString().trim();
                if (StringUtils.isEmpty(goodsName)) {
                    toast("请输入商品名称");
                    return;
                } else if (goodsName.length() > 30) {
                    toast("商品名称最多30个字符");
                    return;
                }
                if (StringUtils.isEmpty(goodsCode)) {
                    toast("请输入或扫描商品条形码");
                    return;
                } else if (goodsCode.length() > 20) {
                    toast("条形码最多输入20个字符");
                    return;
                }
                if (StringUtils.isEmpty(goodsPrice)) {
                    toast("请输入售价");
                    return;
                } else if (isPriceOK(goodsPrice).length() > 0) {
                    return;
                }
                if ("请选择".equals(unitName)) {
                    toast("请选择单位");
                    return;
                }
//                queryGoodsDetial(goodsCode, goodsName, goodsPrice, unitName);
                doAddGoods(goodsName, goodsCode, goodsPrice, unitName);
                break;
            case R.id.llUnitName:
                showActivityForResult(aty, UnitActivity.class, Constants.ForResult.ADD_GOODS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String code = data.getExtras().getString("code");
                    editGoodsCode.setText(code);
//                    queryGoodsDetial(code, null, null, null);
                }
                break;
            case Constants.ForResult.ADD_GOODS:
                if (data != null) {
                    String unitName = data.getExtras().getString("unitName");
                    JCLoger.debug(unitName);
                    textUnitName.setText(unitName);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.Permission.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showActivity(aty, ScanActivity.class, Constants.ForResult.SCAN);
                } else {
                    toast("获取相机权限失败，请到设置里面打开");
                }
                break;
        }
    }

    /**
     * 新增商品
     *
     * @param goodsName
     * @param goodsCode
     * @param goodsPrice
     * @param unitName
     */
    private void doAddGoods(final String goodsName, final String goodsCode, final String goodsPrice, final String unitName) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsStatue", goodsStatue + "");
        params.put("GoodsName", goodsName);
        params.put("GoodsCode", goodsCode);
        params.put("GoodsPrice", goodsPrice);
        params.put("UnitName", unitName);
        httpRequestService.doRequestData(aty, "User/AddGoods", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Firm_Goods_AddDone");
                    toast("保存成功");
                    cleanText();
                    if (needReturn) {
                        goodsDao.doAdd(resultParam.mapData.get("GoodsId"), goodsName, goodsPrice, costPrice, goodsPrice, goodsCode, unitName, String.valueOf(goodsStatue));
                    }
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
     * 查询商品是否已存在
     *
     * @param code
     * @param goodsName
     * @param goodsPrice
     * @param unitName
     */
    private void querygoodsdetial(final String code, final String goodsName, final String goodsPrice, final String unitName) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", code);
        httpRequestService.doRequestData(aty, "User/GoodsDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M04.equals(resultParam.resultId)) {
                    JCLoger.debug(code);
                    doAddGoods(goodsName, code, goodsPrice, unitName);
                } else if (Constants.M00.equals(resultParam.resultId)) {
                    if (dialog == null) {
                        dialog = new NomalDialog(aty);
                        dialog.setOnNoamlLickListener(onNoamlLickListene);
                    }
                    editGoodsCode.setText("");
                    dialog.showClose("商品国际码已在店铺中存在，请更换！");
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast("账号过期重新登录");
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
                JCLoger.debug(resultParam.resultId);
            }
        });
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
        }
    };

    private void cleanText() {
        editGoodsName.setText("");
        editGoodsCode.setText("");
        editGoodsPrice.setText("");
        textUnitName.setText("请选择");
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
            result = "售价过大，请修改";
            if (!isPointOK) {
                result = "售价过大，小数点后面最多2位";
            }
        } else {
            if (!isPointOK) {
                result = "售价最多两位小数";
            }
        }
        if (result.length() > 0) {
            toast(result);
        }
        return result;
    }

    /**
     * 过滤除了汉字，英文字母，数字和°.()（）%-*#+、/的其他特殊字符
     *
     * @param str
     * @return
     */
    public boolean isHava(String str) {
        if (str.contains("&")) {
            return false;
        }
        String regEx = "[\\u4e00-\\u9fa5°#%*、/+——（）#%*-./+／＋－％＃＊℃()a-zA-Z\\d]+";
        Pattern p = Pattern.compile(regEx);
        if (p.matcher(str).matches()) {
            return true;
        } else {
            return false;
        }
    }
}
