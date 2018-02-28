package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.SelectTimeDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.KeyboardPatch;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.CheckSwitchButton;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 营业设置
 * Created by Jocerly on 2017/8/3.
 */
@ContentView(R.layout.activity_storemanage_bus_set)
public class StoreManageBusSetActivity extends BaseActivity {
    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    SimpleDateFormat dateFormat2 = new SimpleDateFormat(Constants.SimpleTimeFormat);
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.scrollView)
    private ScrollView scrollView;
    @BindView(id = R.id.relayLocation, click = true)
    private RelativeLayout relayLocation;
    @BindView(id = R.id.textLocation, click = true)
    private TextView textLocation;

    @BindView(id = R.id.lineOpenTime, click = true)
    private RelativeLayout lineOpenTime;
    @BindView(id = R.id.textOpenTime, click = true)
    private TextView textOpenTime;

    @BindView(id = R.id.lineCloseTime, click = true)
    private RelativeLayout lineCloseTime;
    @BindView(id = R.id.textCloseTime, click = true)
    private TextView textCloseTime;

    @BindView(id = R.id.textDeliveryMsg)
    private TextView textDeliveryMsg;
    @BindView(id = R.id.editStartPrice)
    private EditText editStartPrice;
    @BindView(id = R.id.editDeliveryFee)
    private EditText editDeliveryFee;
    @BindView(id = R.id.editDeliveryScope)
    private EditText editDeliveryScope;

    @BindView(id = R.id.cbFreeDelivery)
    private CheckSwitchButton cbFreeDelivery;//免配送费
    @BindView(id = R.id.llFreeDelivery)
    private LinearLayout llFreeDelivery;
    @BindView(id = R.id.editFreeDeliveryFee)
    private EditText editFreeDeliveryFee;

    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;

    private int flag = 1;
    //纬度
    private String latitude;
    //经度
    private String longitude;
    private String address;
    private SelectTimeDialog timeDialog;
    private KeyboardPatch keyboardPatch;

    private ConcurrentHashMap<String, String> mapData = new ConcurrentHashMap<>();
    private int isFreeDelivery = 1;//0：否，1：是

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("营业设置");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("营业设置");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("营业设置");
        btnRight.setVisibility(View.GONE);

        keyboardPatch = new KeyboardPatch(aty, scrollView);
        keyboardPatch.enable();
        editDeliveryScope.addTextChangedListener(textWatcher1);

        cbFreeDelivery.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void initData() {
        timeDialog = new SelectTimeDialog(aty);
        timeDialog.setOnUpdateTimeListener(onTimeClickListener);

        textLocation.setText("点击开始定位");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                JSONObject json = new JSONObject(bundle.getString("mapData"));
                mapData = DataAnalyze.doAnalyzeJsonArray(json);

                json = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mapData != null) {
                showStoreInfo();
            }
        }
    }

    /**
     * 免配送费开关回调
     */
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) { //未选中
                isFreeDelivery = 0;
                llFreeDelivery.setVisibility(View.GONE);
            } else { //选中
                isFreeDelivery = 1;
                llFreeDelivery.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 选择时间回调
     */
    SelectTimeDialog.OnClickListener onTimeClickListener = new SelectTimeDialog.OnClickListener() {
        @Override
        public boolean onSure(int hour, int minute, long time) {
            switch (flag) {
                case 1:
                    textOpenTime.setText(dateFormat2.format(time));
                    break;
                case 2:
                    textCloseTime.setText(dateFormat2.format(time));
                    break;
            }
            return false;
        }

        @Override
        public boolean onCancel() {
            return false;
        }
    };

    /**
     * 设置O2O店铺信息
     *
     * @param address
     * @param openTime
     * @param closeTime
     * @param startPrice
     * @param deliveryFee
     * @param freeDeliveryFee
     * @param deliveryScope
     */
    public void addStoreManage(String latitude, String longitude, String address, String openTime, String closeTime
            , String startPrice, String deliveryFee, String freeDeliveryFee, String deliveryScope) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("LocationAddress", address);
        params.put("Latitude", latitude);
        params.put("Longitude", longitude);
        params.put("OpenTime", openTime);
        params.put("CloseTime", closeTime);
        params.put("StartPrice", startPrice);
        params.put("DeliveryFee", deliveryFee);
        params.put("IsFreeDelivery", isFreeDelivery);
        params.put("FreeDeliveryFee", freeDeliveryFee);
        params.put("DeliveryScope", deliveryScope);
        httpRequestService.doRequestData(aty, "User/SetShopInfo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    toast("保存成功");
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
     * 获取O2O店铺信息表
     */
    public void showStoreInfo() {
        if (!StringUtils.isEmpty(mapData.get("Latitude")) && !StringUtils.isEmpty(mapData.get("Longitude"))) {
            textLocation.setText("N" + convertToSexagesimal(mapData.get("Latitude")) + ", " + "E" + convertToSexagesimal(mapData.get("Longitude")));
        }
        if (!StringUtils.isEmpty(mapData.get("OpenTime"))) {
            textOpenTime.setText(mapData.get("OpenTime"));
        }
        if (!StringUtils.isEmpty(mapData.get("CloseTime"))) {
            textCloseTime.setText(mapData.get("CloseTime"));
        }
        if (!StringUtils.isEmpty(mapData.get("IsFreeDelivery"))) {
            isFreeDelivery = Integer.parseInt(mapData.get("IsFreeDelivery"));
            cbFreeDelivery.setmBroadcasting(true);
            cbFreeDelivery.setChecked(isFreeDelivery == 0);

            llFreeDelivery.setVisibility(isFreeDelivery == 0 ? View.GONE : View.VISIBLE);
        }
        if ("1".equals(mapData.get("DeliveryType"))) {//2：第三方配送、1：商家配送
            cbFreeDelivery.setEnabled(true);
            textDeliveryMsg.setText(R.string.delivery_type1);
            textDeliveryMsg.setTextColor(getResources().getColor(R.color.text_light));
            editStartPrice.setEnabled(true);
            editDeliveryFee.setEnabled(true);
            editFreeDeliveryFee.setEnabled(true);
            editDeliveryScope.setEnabled(true);
        } else {
            cbFreeDelivery.setEnabled(false);
            textDeliveryMsg.setText(R.string.delivery_type2);
            textDeliveryMsg.setTextColor(getResources().getColor(R.color.red));
            editStartPrice.setEnabled(false);
            editDeliveryFee.setEnabled(false);
            editFreeDeliveryFee.setEnabled(false);
            editDeliveryScope.setEnabled(false);
        }

        if (!StringUtils.isEmpty(mapData.get("StartPrice"))) {
            editStartPrice.setText(mapData.get("StartPrice"));
        }
        if (!StringUtils.isEmpty(mapData.get("DeliveryFee"))) {
            editDeliveryFee.setText(mapData.get("DeliveryFee"));
        }
        if (!StringUtils.isEmpty(mapData.get("FreeDeliveryFee"))) {
            editFreeDeliveryFee.setText(mapData.get("FreeDeliveryFee"));
        }
        if (!StringUtils.isEmpty(mapData.get("DeliveryScope"))) {
            editDeliveryScope.setText(mapData.get("DeliveryScope"));
        }
        address = mapData.get("LocationAddress");
        if (StringUtils.isEmpty(address)) {
            address = mapData.get("Address");
        }
        longitude = mapData.get("Longitude");
        latitude = mapData.get("Latitude");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.textLocation:
            case R.id.relayLocation:
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                bundle.putString("location", textLocation.getText().toString().trim());
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                skipActivityForResult(aty, LocationActivity.class, bundle, 0);
                break;
            case R.id.textOpenTime:
            case R.id.lineOpenTime:
                flag = 1;
                String time = textOpenTime.getText().toString().trim();
                if (!"请选择".equals(time)) {
                    String[] timeArray = time.split(":");
                    timeDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]));
                } else {
                    timeDialog.show();
                }
                break;
            case R.id.textCloseTime:
            case R.id.lineCloseTime:
                flag = 2;
                String time2 = textCloseTime.getText().toString().trim();
                if (!"请选择".equals(time2)) {
                    String[] timeArray = time2.split(":");
                    timeDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]));
                } else {
                    timeDialog.show();
                }
                break;
            case R.id.btnSave:
                String location = textLocation.getText().toString().trim();
                String openTime = textOpenTime.getText().toString().trim();
                String closeTime = textCloseTime.getText().toString().trim();

                String startPrice = editStartPrice.getText().toString().trim();
                String deliveryFee = editDeliveryFee.getText().toString().trim();
                String deliveryScope = editDeliveryScope.getText().toString().trim();
                String freeDeliveryFee = editFreeDeliveryFee.getText().toString().trim();
                if ("点击开始定位".equals(location)) {
                    toast("经纬度不能为空");
                    return;
                }

                if (StringUtils.isEmpty(startPrice)) {
                    toast("请输入起送金额");
                    return;
                }

                if (StringUtils.isEmpty(deliveryFee)) {
                    toast("请输入配送费");
                    return;
                }
                if (Integer.parseInt(deliveryFee) > 100) {
                    toast("配送费需小于或等于100");
                    return;
                }

                if (isFreeDelivery == 1 && StringUtils.isEmpty(freeDeliveryFee)) {
                    toast("请输入免配送费金额");
                    return;
                }

                if (StringUtils.isEmpty(deliveryScope)) {
                    toast("请输入配送范围");
                    return;
                }
                if (deliveryScope.startsWith(".") || deliveryScope.contains("..")) {
                    toast("配送范围输入不合法");
                    return;
                }
                if (Double.parseDouble(deliveryScope) > 5) {
                    toast("配送范围不能超过5公里");
                    return;
                }
                if (Double.parseDouble(deliveryScope) <= 0) {
                    toast("配送范围必须大于0");
                    return;
                }
                if (isFreeDelivery == 1 && Integer.parseInt(freeDeliveryFee) <= 0) {
                    toast("免配送费金额必须大于0");
                    return;
                }
                if ("请选择".equals(openTime)) {
                    openTime = "";
                }
                if ("请选择".equals(closeTime)) {
                    closeTime = "";
                }
                addStoreManage(latitude, longitude, address, openTime, closeTime, startPrice, deliveryFee, freeDeliveryFee, deliveryScope);
                break;
        }
    }

    /**
     * 把经纬度转换为度分秒的格式
     *
     * @param numStr
     * @return
     */
    public static String convertToSexagesimal(String numStr) {
        double num = Double.parseDouble(numStr);
        int du = (int) Math.floor(Math.abs(num));    //获取整数部分
        double temp = getdPoint(Math.abs(num)) * 60;
        int fen = (int) Math.floor(temp); //获取整数部分
        double miao = getdPoint(temp) * 60;
        if (num < 0)
            return "-" + du + "°" + fen + "'" + miao + "\" ";

        return du + "°" + fen + "'" + Common.doubleFormat(miao, 1) + "\" ";

    }

    private static double getdPoint(double num) {
        double d = num;
        int fInt = (int) d;
        BigDecimal b1 = new BigDecimal(Double.toString(d));
        BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
        double dPoint = b1.subtract(b2).floatValue();
        return dPoint;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                JCLoger.debug("bundle" + bundle);
                if (bundle != null) {
                    address = bundle.getString("address");
                    latitude = bundle.getString("latitude");
                    longitude = bundle.getString("longitude");
                    textLocation.setText("N" + convertToSexagesimal(latitude) + "," + "E" + convertToSexagesimal(longitude));
                }
            }
        }
    }
}
