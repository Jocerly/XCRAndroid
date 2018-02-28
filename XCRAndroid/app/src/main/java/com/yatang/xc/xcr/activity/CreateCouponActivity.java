package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 创建优惠券页面
 * Created by dengjiang on 2017/10/13.
 */
@ContentView(R.layout.activity_createcoupon)
public class CreateCouponActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.CouponBalance)
    private EditText CouponBalance;
    @BindView(id = R.id.UseCondition)
    private EditText UseCondition;
    @BindView(id = R.id.Duration)
    private EditText Duration;
    @BindView(id = R.id.btnConfirm, click = true)
    private TextView btnConfirm;

    @Override
    public void initWidget() {
        textTitle.setText("添加优惠券");
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("添加优惠券");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("添加优惠券");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnConfirm:
                createCoupon();
                break;
            default:
                break;
        }
    }

    /**
     * 构建单个优惠券信息
     */
    private void createCoupon() {
        String balance, condition, duration;
        balance = CouponBalance.getText().toString().trim();
        condition = UseCondition.getText().toString().trim();
        duration = Duration.getText().toString().trim();
        if (StringUtils.isEmpty(balance)) {
            toast("请输入优惠券金额");
            return;
        } else if (Integer.parseInt(balance) <= 0) {
            toast("优惠券金额必须大于0");
            return;
        } else if (Integer.parseInt(balance) > 10000) {
            toast("优惠券金额不得大于1万");
            return;
        } else if (StringUtils.isEmpty(condition)) {
            toast("请输入订单金额");
            return;
        } else if (Integer.parseInt(condition) <= Integer.parseInt(balance)) {
            toast("优惠券金额必须小于订单金额");
            return;
        } else if (Integer.parseInt(condition) > 100000) {
            toast("订单金额不得大于10万");
            return;
        } else if (StringUtils.isEmpty(duration)) {
            toast("请输入有效天数");
            return;
        } else if (Integer.parseInt(duration) <= 0) {
            toast("券有效天数必须大于0");
            return;
        } else if (Integer.parseInt(duration) > 365) {
            toast("券有效天数不得大于365");
            return;
        } else {
            Intent intent = new Intent();
            intent.putExtra("CouponBalance", balance);
            intent.putExtra("UseCondition", condition);
            intent.putExtra("Duration", duration);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
