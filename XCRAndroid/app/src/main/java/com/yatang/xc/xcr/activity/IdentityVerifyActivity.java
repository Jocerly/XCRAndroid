package com.yatang.xc.xcr.activity;

import android.os.Bundle;
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
import org.jocerly.jcannotation.utils.MatcherUtils;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 身份验证
 * Created by Jocerly on 2017/10/30.
 */
@ContentView(value = R.layout.activity_identity_verify)
public class IdentityVerifyActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.editMarketNo)
    private EditText editMarketNo;
    @BindView(id = R.id.editPhone)
    private EditText editPhone;

    @BindView(id = R.id.btnNext, click = true)
    private TextView btnNext;
    private NomalDialog nomalDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("身份验证");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("身份验证");
        MobclickAgent.onPause(aty);
    }


    @Override
    public void initWidget() {
        textTitle.setText("身份验证");
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnNext:
                String marketNo = editMarketNo.getText().toString().trim();
                String phone = editPhone.getText().toString().trim();
                if (StringUtils.isEmpty(marketNo)) {
                    toast("请输入您的小超编号");
                    return;
                }
                if (StringUtils.isEmpty(phone)) {
                    toast("请输入加盟手机号码");
                    return;
                }
                if (marketNo.length() < 6) {
                    toast("请输入正确的小超编号");
                    return;
                }
                if (!MatcherUtils.checkMobilePhoneNum(phone)) {
                    toast("请输入正确的手机号");
                    return;
                }
                doIdentityMarketNo(marketNo, phone);
                break;
        }
    }

    /**
     * 门店编号校验
     */
    private void doIdentityMarketNo(final String marketNo, final String phone) {
        params.clear();
        params.put("MarketNo", marketNo);
        params.put("Phone", phone);
        httpRequestService.doRequestData(aty, "System/IdentityMarketNo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("countDown", resultParam.mapData.get("CountDown"));
                    bundle.putString("marketNo", marketNo);
                    bundle.putString("phone", phone);
                    skipActivity(aty, SendPhoneCodeActivity.class, bundle);
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    if (nomalDialog == null) {
                        nomalDialog = new NomalDialog(aty);
                        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
                    }
                    nomalDialog.showClose(getResources().getString(R.string.bond_time_out), "我知道了");
                } else if (Constants.M14.equals(resultParam.resultId)) {//未实名认证
                    if (nomalDialog == null) {
                        nomalDialog = new NomalDialog(aty);
                        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
                    }
                    nomalDialog.showClose(getResources().getString(R.string.has_not_realname), "我知道了");
                } else if (Constants.M15.equals(resultParam.resultId)) {//已加盟
                    toast("您的店铺已经加盟");
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            finish();
        }
    };

}
