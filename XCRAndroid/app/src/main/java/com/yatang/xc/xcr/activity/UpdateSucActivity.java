package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;

/**
 * 提交成功页面
 * Created by Jocerly on 2017/3/28.
 */
@ContentView(R.layout.activity_upload_suc)
public class UpdateSucActivity extends BaseActivity{
    public static final String KEY_TXT = "key_txt";

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;

    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.btnBackTask, click = true)
    private TextView btnBackTask;

    @BindView(id = R.id.textToShow)
    private TextView textToShow;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("任务结果");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("任务结果");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(UpdateSucActivity.KEY_TXT) != null) {
            textToShow.setText(bundle.getString(KEY_TXT));
        }else {
            textToShow.setText(getResources().getString(R.string.txt_wait_verify));
        }
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
            case R.id.btnBackTask:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
