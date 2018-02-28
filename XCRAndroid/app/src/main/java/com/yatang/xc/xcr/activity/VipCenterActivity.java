package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.VipCenterAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.listView.CustomerListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会员中心
 * Created by dengjiang on 2017/11/05.
 */
@ContentView(R.layout.activity_vip_center)
public class VipCenterActivity extends BaseActivity {
    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.lin_LevelBack)
    private LinearLayout lin_LevelBack;
    @BindView(id = R.id.imgVip)
    private ImageView imgVip;
    @BindView(id = R.id.textAccumulatedIntegral)
    private TextView textAccumulatedIntegral;
    @BindView(id = R.id.textTimeInterval)
    private TextView textTimeInterval;
    @BindView(id = R.id.btn_MyIntegral, click = true)
    private PressTextView btn_MyIntegral;
    @BindView(id = R.id.btn_VipLevel, click = true)
    private PressTextView btn_VipLevel;
    @BindView(id = R.id.listPrivilege)
    private CustomerListView listPrivilege;
    @BindView(id = R.id.listIntegral)
    private CustomerListView listIntegral;

    private VipCenterAdapter adapterPrivilege;
    private VipCenterAdapter adapterIntegral;

    private List<ConcurrentHashMap<String, String>> privilegeData;
    private List<ConcurrentHashMap<String, String>> integralData;
    private int currentLevel = 0;

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.vipback_start);
        rlTitle.setBackgroundColor(color);
        textTitle.setText("会员中心");
        btnRight.setVisibility(View.GONE);
        setWindowColor(color);
        privilegeData = new ArrayList<>();
        integralData = new ArrayList<>();
        adapterPrivilege = new VipCenterAdapter(aty, privilegeData, 0);
        adapterIntegral = new VipCenterAdapter(aty, integralData, 1);
        listPrivilege.setAdapter(adapterPrivilege);
        listIntegral.setAdapter(adapterIntegral);
    }

    @Override
    public void initData() {
        getVipInfo();
        initIntegral();
    }

    /**
     * 获取vip会员信息
     */
    private void getVipInfo() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/MemberInfo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                resultParam.resultId = Constants.M00;
                if (Constants.M00.equals(resultParam.resultId)) {
                    String sCurrentLevel = resultParam.mapData.get("VipIdentify");
                    MyApplication.instance.vipIdentify = sCurrentLevel;
                    Common.setAppInfo(aty, Constants.Preference.VipIdentify, MyApplication.instance.vipIdentify);
                    if (StringUtils.isEmpty(sCurrentLevel)) {
                        sCurrentLevel = "0";
                    }
                    currentLevel = Integer.parseInt(sCurrentLevel);
                    setBack(lin_LevelBack, currentLevel);
                    setVipIcon(imgVip, sCurrentLevel);
                    textAccumulatedIntegral.setText(resultParam.mapData.get("AccumulatedIntegral"));
                    textTimeInterval.setText(resultParam.mapData.get("TimeInterval"));
                    if (resultParam.listData != null && resultParam.listData.size() > 0) {
                        privilegeData.clear();
                        privilegeData.addAll(resultParam.listData);
                    }
                    //添加末尾一项固定数据
                    ConcurrentHashMap map = new ConcurrentHashMap();
                    map.put("PrivilegeName", "更多特权期待中");
                    map.put("PrivilegeInfo", "获得的积分越多，等级越高，享有的特权越丰富");
                    map.put("PrivilegeType", "-1");
                    privilegeData.add(map);
                    adapterPrivilege.notifyDataSetChanged();
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
     * 设置会员表
     *
     * @param img          显示图标的imageview
     * @param currentLevel 当前等级
     */
    private void setVipIcon(ImageView img, String currentLevel) {
        int imgId;
        switch (currentLevel) {
            case "0":
                imgId = R.drawable.v0;
                break;
            case "1":
                imgId = R.drawable.v1;
                break;
            case "2":
                imgId = R.drawable.v2;
                break;
            case "3":
                imgId = R.drawable.v3;
                break;
            case "4":
                imgId = R.drawable.v4;
                break;
            case "5":
                imgId = R.drawable.v5;
                break;
            case "6":
                imgId = R.drawable.v6;
                break;
            case "7":
                imgId = R.drawable.v7;
                break;
            case "8":
                imgId = R.drawable.v8;
                break;
            case "9":
                imgId = R.drawable.v9;
                break;
            case "10":
                imgId = R.drawable.v10;
                break;
            default:
                imgId = R.drawable.v10;
                break;
        }
        img.setImageResource(imgId);
    }

    /**
     * 初始化赚积分列表数据
     * 本地固定数据
     */
    private void initIntegral() {
        if (integralData.size() <= 0) {
            String[] integralNames = getResources().getStringArray(R.array.IntegralName);
            String[] integralInfos = getResources().getStringArray(R.array.IntegralInfo);
            int length = integralNames.length;
            for (int i = 0; i < length; i++) {
                ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                map.put("IntegralName", integralNames[i]);
                map.put("IntegralInfo", integralInfos[i]);
                map.put("Type", i + "");
                integralData.add(map);
            }
        }
        adapterIntegral.notifyDataSetChanged();
    }

    /**
     * 设置会员等级背景
     */
    private void setBack(LinearLayout lin_LevelBack, int currentLevel) {
        int backId;
        switch (currentLevel) {
            case 0:
                backId = R.drawable.v0_bg;
                break;
            case 1:
            case 2:
            case 3:
                backId = R.drawable.v1_v2_v3_bg;
                break;
            case 4:
            case 5:
                backId = R.drawable.v4_v5_bg;
                break;
            case 6:
            case 7:
                backId = R.drawable.v6_v7_bg;
                break;
            case 8:
            case 9:
                backId = R.drawable.v8_v9_bg;
                break;
            case 10:
                backId = R.drawable.v10_bg;
                break;
            default:
                backId = R.drawable.v10_bg;
                break;
        }
        lin_LevelBack.setBackgroundResource(backId);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle b = new Bundle();
        b.putInt("CurrentLevel", currentLevel);
        switch (v.getId()) {
            case R.id.btn_MyIntegral:
                //跳转到我的积分页面
                skipActivity(aty, MyIntegralActivity.class, b);
                break;
            case R.id.btn_VipLevel:
                //跳转到会员等级页面
                skipActivity(aty, VipLevelActivity.class, b);
                break;
            case R.id.btnLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
