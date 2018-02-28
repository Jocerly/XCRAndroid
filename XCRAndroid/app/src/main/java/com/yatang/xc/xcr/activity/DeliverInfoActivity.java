package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 配送信息页面
 * Created by DengJiang on 2017/8/14.
 */
@ContentView(R.layout.activity_deliver_info)
public class DeliverInfoActivity extends BaseActivity {
    public static final String NAME = "Name";
    public static final String PHONE = "Phone";
    public static final String SENDTIME = "SendTime";
    public static final String REACHTIME = "ReachTime";
    public static final String ADDRESS = "Address";

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textName)
    private TextView textName;
    @BindView(id = R.id.textPhone)
    private TextView textPhone;
    @BindView(id = R.id.textSendTime)
    private TextView textSendTime;
    @BindView(id = R.id.textReachTime)
    private TextView textReachTime;
    @BindView(id = R.id.btnReturn, click = true)
    private TextView btnReturn;

    @Override
    public void initWidget() {
        textTitle.setText("配送信息");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            textName.setText(bundle.getString(NAME, ""));
            textPhone.setText(bundle.getString(PHONE, ""));
            String temp = bundle.getString(SENDTIME);
            if (!StringUtils.isEmpty(temp)) {
                textSendTime.setText(Common.stampToDate(temp, "yyyy.MM.dd hh:mm:ss"));
            }
            temp = bundle.getString(REACHTIME);
            if (!StringUtils.isEmpty(temp)) {
                textReachTime.setText(Common.stampToDate(temp, "yyyy.MM.dd hh:mm:ss"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
            case R.id.btnReturn:
                onBackPressed();
        }
    }
}
