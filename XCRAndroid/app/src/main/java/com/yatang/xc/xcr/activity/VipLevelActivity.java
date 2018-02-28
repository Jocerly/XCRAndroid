package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.MPrivilegeListyAdapter;
import com.yatang.xc.xcr.adapter.VipLevelAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.CustomViewPager;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会员等级页面
 * Created by DengJiang on 2017/3/6.
 */

@ContentView(R.layout.activity_vip_level)
public class VipLevelActivity extends BaseActivity {
    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    @BindView(id = R.id.viewPager)
    private CustomViewPager viewPager;
    private VipLevelAdapter vipLevelAdapter;
    private List<ConcurrentHashMap<String, String>> list_Level;

    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    private MPrivilegeListyAdapter mPrivilegeListyAdapter;
    private List<ConcurrentHashMap<String, String>> list_Privilege;
    private int currentLevel = 0;

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.vipback_start);
        rlTitle.setBackgroundColor(color);
        textTitle.setText("会员等级");
        btnRight.setVisibility(View.GONE);
        setWindowColor(color);
        list_Level = new ArrayList<>();
        list_Privilege = new ArrayList<>();
        mPrivilegeListyAdapter = new MPrivilegeListyAdapter(aty, list_Privilege);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(mPrivilegeListyAdapter);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentLevel = bundle.getInt("CurrentLevel", 0);
        }
        getVipInfo();
    }

    /**
     * 获取vip会员信息
     */
    private void getVipInfo() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
//            params.put("ShopName", MyApplication.instance.StoreSerialNameDefault);
        httpRequestService.doRequestData(aty, "User/GradeInfo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    initLevelInfo(resultParam);
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }

    /**
     * 设置会员列表数据
     */
    private void initLevelInfo(ResultParam resultParam) {
        if (resultParam.listData != null && resultParam.listData.size() > 0) {
            list_Level.addAll(resultParam.listData);
        }
        if (list_Level == null || list_Level.size() <= 0) {
            toast("获取数据出错");
            return;
        }
        vipLevelAdapter = new VipLevelAdapter(aty, list_Level);
        viewPager.setAdapter(vipLevelAdapter);
        vipLevelAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(currentLevel);
        setPrivilegeData(currentLevel);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPrivilegeData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPrivilegeData(int position) {
        String str_Privilege = list_Level.get(position).get("PrivilegeList");
        List<ConcurrentHashMap<String, String>> list = DataAnalyze.strToArrayList(str_Privilege);
        list_Privilege.clear();
        list_Privilege.addAll(list);
        mPrivilegeListyAdapter.notifyDataSetChanged();
        if (list_Privilege.size() > 0) {
            textNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            textNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }
}

