package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.fragment.BondListFragment;
import com.yatang.xc.xcr.fragment.ContractSignFragment;
import com.yatang.xc.xcr.fragment.PayToBondFragment;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 签约入驻
 * Created by Jocerly on 2017/11/1.
 */
@ContentView(value = R.layout.activity_contract_settled)
public class ContractSettledActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.text1)
    private TextView text1;
    @BindView(id = R.id.text2)
    private TextView text2;
    @BindView(id = R.id.text3)
    private TextView text3;
    @BindView(id = R.id.textStep1)
    private TextView textStep1;
    @BindView(id = R.id.textStep2)
    private TextView textStep2;
    @BindView(id = R.id.textStep3)
    private TextView textStep3;

    private BondListFragment bondListFragment;
    private ContractSignFragment contractSignFragment;
    private PayToBondFragment payToBondFragment;

    private NomalDialog nomalDialog;

    /**
     * 0：选择保证金，1：合同未签订，2：支付保证金
     */
    private String joinStatue;
    private String marketNo;
    private String phone;

    private String contractUrl;//需要签订的合同

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("签约入驻");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("签约入驻");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("签约入驻");
        btnRight.setVisibility(View.GONE);

        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);

        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            joinStatue = bundle.getString("joinStatue");
            marketNo = bundle.getString("marketNo");
            phone = bundle.getString("phone");
            contractUrl = bundle.getString("contractUrl");

            if (!StringUtils.isEmpty(joinStatue)) {
                //0：选择保证金，1：合同未签订，2：支付保证金
                switch (Integer.parseInt(joinStatue)) {
                    case 0:
                        setStep(1);
                        break;
                    case 1:
                        setStep(2);
                        break;
                    case 2:
                        setStep(3);
                        break;
                }
            }
        }
    }

    /**
     * 显示步骤View
     *
     * @param step
     */
    private void showStepView(int step) {
        switch (step) {
            case 1:
                text1.setBackgroundResource(R.drawable.step_on);
                text2.setBackgroundResource(R.drawable.step_no);
                text3.setBackgroundResource(R.drawable.step_no);

                textStep1.setTextColor(getResources().getColor(R.color.text_dark));
                textStep2.setTextColor(getResources().getColor(R.color.text_light));
                textStep3.setTextColor(getResources().getColor(R.color.text_light));
                break;
            case 2:
                text1.setBackgroundResource(R.drawable.step_down);
                text2.setBackgroundResource(R.drawable.step_on);
                text3.setBackgroundResource(R.drawable.step_no);

                textStep1.setTextColor(getResources().getColor(R.color.text_light));
                textStep2.setTextColor(getResources().getColor(R.color.text_dark));
                textStep3.setTextColor(getResources().getColor(R.color.text_light));
                break;
            case 3:
                text1.setBackgroundResource(R.drawable.step_down);
                text2.setBackgroundResource(R.drawable.step_down);
                text3.setBackgroundResource(R.drawable.step_on);

                textStep1.setTextColor(getResources().getColor(R.color.text_light));
                textStep2.setTextColor(getResources().getColor(R.color.text_light));
                textStep3.setTextColor(getResources().getColor(R.color.text_dark));
                break;
        }
    }

    /**
     * 获取小超编号
     *
     * @return
     */
    public String getMarketNo() {
        return marketNo;
    }

    /**
     * 得到合同链接
     *
     * @return
     */
    public String getContractUrl() {
        return contractUrl;
    }

    /**
     * 设置签到合同链接
     *
     * @param contractUrl
     */
    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setStep(int step) {
        showStepView(step);
        switch (step) {
            case 1:
                if (bondListFragment == null) {
                    bondListFragment = new BondListFragment();
                }
                changeFragment(R.id.llFragment, bondListFragment);
                break;
            case 2:
                if (contractSignFragment == null) {
                    contractSignFragment = new ContractSignFragment();
                }
                changeFragment(R.id.llFragment, contractSignFragment);
                break;
            case 3:
                if (payToBondFragment == null) {
                    payToBondFragment = new PayToBondFragment();
                }
                changeFragment(R.id.llFragment, payToBondFragment);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                nomalDialog.show("您还未完成签约入驻，确认关闭吗？");
                break;
        }
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                nomalDialog.show("您还未完成签约入驻，确认关闭吗？");
                break;
        }
        return true;
    }

    /**
     * 显示超时弹框
     */
    public void showTinmeOutDialog() {
        nomalDialog.showClose(getResources().getString(R.string.bond_time_out), "我知道了");
    }
}
