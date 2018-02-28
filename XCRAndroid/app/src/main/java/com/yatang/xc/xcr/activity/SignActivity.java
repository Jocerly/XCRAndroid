package com.yatang.xc.xcr.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.SignAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.entity.SignEntity;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.SignView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 签到
 * Created by lusha on 2017/07/08.
 */
@ContentView(R.layout.activity_sign)
public class SignActivity extends BaseActivity {
    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.textYear)
    private TextView textYear;
    @BindView(id = R.id.textMonth)
    private TextView textMonth;
    @BindView(id = R.id.textContinueSign)
    private TextView textContinueSign;
    @BindView(id = R.id.textSignReward)
    private TextView textSignReward;
    @BindView(id = R.id.btnSign, click = true)
    private TextView btnSign;
    @BindView(id = R.id.textRewardUnit)
    private TextView textRewardUnit;
    @BindView(id = R.id.signView)
    private SignView signView;
    @BindView(id = R.id.textSignMsg)
    private TextView textSignMsg;

    private SignAdapter signAdapter;
    private List<SignEntity> data;
    private String IsCurrentDateSign;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("每日签到");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("每日签到");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.green_press);
        rlTitle.setBackgroundColor(color);
        textTitle.setText("每日签到");

        setWindowColor(color);
    }

    @Override
    public void initData() {
        data = new ArrayList<>();
        if (signView != null) {
            signView.setOnTodayClickListener(onTodayClickListener);
        }
        getSignHistoryMsg();
    }

    private SignView.OnTodayClickListener onTodayClickListener = new SignView.OnTodayClickListener() {
        @Override
        public void onTodayClick() {
        }
    };

    /**
     * 签到
     */
    public void getSign() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/Sign", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Firm_Store_Sign");
                    toast(resultParam.mapData.get("SignResultMsg"));
                    getSignHistoryMsg();
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
     * 获取历史签到的数据
     */
    public void getSignHistoryMsg() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/SignHistoryMsg", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    showData(resultParam.mapData);
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
     * 显示数据
     *
     * @param mapData
     */
    private void showData(ConcurrentHashMap<String, String> mapData) {
        textContinueSign.setText(mapData.get("ContinueSignDays"));
        textSignReward.setText(mapData.get("SignReward"));
        textRewardUnit.setText(mapData.get("RewardUnit"));
        textYear.setText(Common.getData(mapData.get("CurrentDate"), 0) + "年");
        textMonth.setText(Common.getData(mapData.get("CurrentDate"), 1) + "月");
        textSignMsg.setText(mapData.get("SignMsg"));

        IsCurrentDateSign = mapData.get("IsCurrentDateSign");

        if ("1".equals(mapData.get("IsCurrentDateSign"))) {
            btnSign.setText("今日已签到");
            btnSign.setBackgroundResource(R.drawable.sign_finish);
        } else {
            btnSign.setText("今日签到");
            btnSign.setBackgroundResource(R.drawable.sign);
        }
        List<String> listdatas = getContinueSignArrayDays(mapData.get("ContinueSignArrayDays"));
        data.clear();
        int dayOfMonthToday = Integer.valueOf(Common.getData(mapData.get("CurrentDate"), 2));
        for (int i = 1; i < 32; i++) {
            SignEntity signEntity = new SignEntity();
            if (dayOfMonthToday == i) {
                if ("1".equals(mapData.get("IsCurrentDateSign"))) {
                    signEntity.setDayType(3);
                } else {
                    signEntity.setDayType(2);
                }
            } else {
                if (listdatas.contains(String.valueOf(i))) {
                    signEntity.setDayType(0);
                } else {
                    signEntity.setDayType(1);
                }
            }
            data.add(signEntity);
        }
        signAdapter = new SignAdapter(data);
        Calendar calendar = Common.getCalendarByDate(mapData.get("CurrentDate"));
        if (calendar != null) {
            signView.updateCurrentDate(calendar);
        } else {
            signView.setDayOfMonthToday(dayOfMonthToday);
        }
        signView.setAdapter(signAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnSign:
                if ("1".equals(IsCurrentDateSign)) {
                    btnSign.setEnabled(false);
                } else {
                    getSign();
                }
                break;
        }

    }

    /**
     * 获取当月签到日期
     *
     * @param str
     * @return
     */
    public List<String> getContinueSignArrayDays(String str) {
        String datas = str.replace("[", "");
        String ContinueSignArrayDays = datas.replace("]", "");
        String[] data = ContinueSignArrayDays.split(",");
        List<String> list = new ArrayList<>();
        for (String s : data) {
            list.add(s);
        }
        return list;
    }
}
