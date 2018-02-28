package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 验证手机号
 * Created by Jocerly on 2017/10/30.
 */
@ContentView(value = R.layout.activity_send_phone_code)
public class SendPhoneCodeActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.editCode)
    private EditText editCode;

    @BindView(id = R.id.btnNext, click = true)
    private TextView btnNext;
    @BindView(id = R.id.btnCode, click = true)
    private TextView btnCode;

    private int countDown = 60;
    private String marketNo;
    private String phone;
    private Timer timer;
    private NomalDialog nomalDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("验证手机号码");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("验证手机号码");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("验证手机号码");
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String count = bundle.getString("countDown");
            marketNo = bundle.getString("marketNo");
            phone = bundle.getString("phone");
            if (!StringUtils.isEmpty(count)) {
                countDown = Integer.parseInt(count);
            }
            showBtnCode(true);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnCode:
                doIdentityMarketNo();
                break;
            case R.id.btnNext:
                String code = editCode.getText().toString().trim();
                if (StringUtils.isEmpty(code)) {
                    toast("请输入验证码");
                    return;
                }
                if (code.length() < 6) {
                    toast("请输入正确的验证码");
                    return;
                }

                doIdentityVerify(code);
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void doIdentityMarketNo() {
        params.clear();
        params.put("MarketNo", marketNo);
        params.put("Phone", phone);
        httpRequestService.doRequestData(aty, "System/IdentityMarketNo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    countDown = Integer.parseInt(resultParam.mapData.get("CountDown"));
                    showBtnCode(true);
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 身份验证
     */
    private void doIdentityVerify(String code) {
        params.clear();
        params.put("MarketNo", marketNo);
        params.put("Phone", phone);
        params.put("Code", code);
        httpRequestService.doRequestData(aty, "System/IdentityVerify", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    Bundle bundle;
                    if ("0".equals(resultParam.mapData.get("IsPayBond"))) {//0：不需要金，1：需要
                        bundle = new Bundle();
                        bundle.putInt("JutmpTo", 3);
                        bundle.putString("marketNo", marketNo);
                        bundle.putString("ClassName", "合同签订");
                        bundle.putString("ClassUrl", resultParam.mapData.get("ContractUrl"));
                        skipActivity(aty, BrowserActivity.class, bundle);
                        finish();
                    } else {//需要
                        bundle = new Bundle();
                        bundle.putString("marketNo", marketNo);
                        bundle.putString("phone", phone);
                        bundle.putString("joinStatue", resultParam.mapData.get("JoinStatue"));
                        bundle.putString("contractUrl", resultParam.mapData.get("ContractUrl"));
                        skipActivity(aty, ContractSettledActivity.class, bundle);
                        finish();
                    }
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    if (nomalDialog == null) {
                        nomalDialog = new NomalDialog(aty);
                        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
                    }
                    nomalDialog.showClose(getResources().getString(R.string.bond_time_out), "我知道了");
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    private void showBtnCode(boolean isTimer) {
        btnCode.setEnabled(!isTimer);
        if (isTimer) {
            btnCode.setText(countDown + "s");
            btnCode.setBackgroundResource(R.drawable.btn_code_no);

            timer = new Timer();
            timer.schedule(new MyTimerTask(), 1000);
        } else {
            btnCode.setBackgroundResource(R.drawable.btn_code_bg);
            btnCode.setText("再次获取");
        }
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
    };

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            countDown--;
            if (countDown == 0) {
                timer.cancel();
                mHandler.sendEmptyMessage(0);
            } else {
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showBtnCode(false);
                    break;
                case 1:
                    timer.schedule(new MyTimerTask(), 1000);
                    btnCode.setText(countDown + "s");
                    break;
            }
        }
    };
}
