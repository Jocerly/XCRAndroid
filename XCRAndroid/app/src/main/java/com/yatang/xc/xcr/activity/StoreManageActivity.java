package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalInputDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.CheckSwitchButton;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线上店铺管理
 * Created by lusha on 2017/06/08.
 */
@ContentView(R.layout.activity_storemanage)
public class StoreManageActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.cbBusinessStatus)
    private CheckSwitchButton cbBusinessStatus;//营业状态
    @BindView(id = R.id.textBusinessStatus)
    private TextView textBusinessStatus;

    @BindView(id = R.id.cbReciveStatus)
    private CheckSwitchButton cbReciveStatus;//自动接单
    @BindView(id = R.id.textReciveStatus)
    private TextView textReciveStatus;

    @BindView(id = R.id.rlStoreAbbrevy, click = true)
    private LinearLayout rlStoreAbbrevy;
    @BindView(id = R.id.textStoreAbbrevy)
    private TextView textStoreAbbrevy;

    @BindView(id = R.id.rlStoreSet, click = true)
    private RelativeLayout rlStoreSet;

    private NomalInputDialog nomalInputDialog;
    private int flag = 1;
    private ConcurrentHashMap<String, String> mapData = new ConcurrentHashMap<>();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("线上店铺管理");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("线上店铺管理");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("线上店铺管理");
        btnRight.setVisibility(View.GONE);
        rlStoreAbbrevy.setEnabled(false);

        cbBusinessStatus.setOnCheckedChangeListener(onCheckedChangeListener);
        cbReciveStatus.setOnCheckedChangeListener(onCheckedChangeListener2);
    }

    @Override
    public void initData() {
        getStoreInfo();
    }

    /**
     * 营业状态开关回调
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
     * 自动接单开关回调
     */
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener2 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) { //未选中
                setReciveStatus(0);
            } else { //选中
                setReciveStatus(1);
            }
        }
    };

    /**
     * 设置营业状态
     *
     * @param businessStatus 0：暂停营业，1：正常营业
     */
    public void setShopBusiness(final int businessStatus) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("BusinessStatus", businessStatus);
        httpRequestService.doRequestData(aty, "User/SetShopBusiness", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    setBusinessStatue(businessStatus);
                    switch (businessStatus) {
                        case 0:
                            toast("暂停营业");
                            break;
                        case 1:
                            toast("正常营业");
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

    /**
     * 设置自动接单
     *
     * @param reciveStatus 0：手动接单，1：自动接单
     */
    public void setReciveStatus(final int reciveStatus) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ReciveStatus", reciveStatus);
        httpRequestService.doRequestData(aty, "User/SetReciveStatus", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    switch (reciveStatus) {
                        case 0:
                            toast("关");
                            break;
                        case 1:
                            toast("开");
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

    /**
     * 设置店铺简称
     *
     * @param storeAbbrevy
     */
    public void setStoreAbbrevy(final String storeAbbrevy) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("StoreAbbrevy", storeAbbrevy);
        httpRequestService.doRequestData(aty, "User/SetStoreAbbrevy", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    textStoreAbbrevy.setText(storeAbbrevy);
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
    public void getStoreInfo() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/GetShopInfo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    mapData.clear();
                    mapData.putAll(resultParam.mapData);

                    if (!StringUtils.isEmpty(mapData.get("BusinessStatus"))) {
                        setBusinessStatue(Integer.parseInt(mapData.get("BusinessStatus")));
                        cbBusinessStatus.setmBroadcasting(true);
                        cbBusinessStatus.setChecked("0".equals(mapData.get("BusinessStatus")));
                    }

                    if (!StringUtils.isEmpty(mapData.get("ReciveStatus"))) {
                        cbReciveStatus.setmBroadcasting(true);
                        cbReciveStatus.setChecked("0".equals(mapData.get("ReciveStatus")));
                    }

                    textStoreAbbrevy.setText(mapData.get("StoreAbbrevy"));
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else if (Constants.M02.equals(resultParam.resultId)) {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 设置营业状态
     * @param businessStatus 0：暂停营业，1：正常营业
     */
    private void setBusinessStatue(int businessStatus) {
        switch (businessStatus) {
            case 0:
                textBusinessStatus.setText("暂停营业");
                break;
            case 1:
                textBusinessStatus.setText("正常营业");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.rlStoreAbbrevy:
                if (nomalInputDialog == null) {
                    nomalInputDialog = new NomalInputDialog(aty);
                    nomalInputDialog.setOnNomalInputClickListener(onNomalInputClickListener);
                }
                nomalInputDialog.show("店铺简称", "最多输入7个字", "请输入店铺简称", 7);
                break;
            case R.id.rlStoreSet:
                Bundle bundle = new Bundle();
                bundle.putString("mapData", new JSONObject(mapData).toString());
                skipActivityForResult(aty, StoreManageBusSetActivity.class, bundle, Constants.ForResult.SET_STORE_MSG);
                break;
        }
    }

    NomalInputDialog.OnNomalInputClickListener onNomalInputClickListener = new NomalInputDialog.OnNomalInputClickListener() {
        @Override
        public void onOkClick(String msg) {
            setStoreAbbrevy(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SET_STORE_MSG:
                if (resultCode == RESULT_OK) {
                    getStoreInfo();
                }
                break;
        }
    }
}
