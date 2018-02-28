package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 任务失败的显示页
 * Created by lusha on 2017/04/15.
 */
@ContentView(R.layout.activity_task_fail)
public class TaskFailActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;

    @BindView(id = R.id.textMsg)
    private TextView textMsg;

    @BindView(id = R.id.btnUpload, click = true)
    private TextView btnUpload;

    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textCause)
    private TextView textCause;

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
        textTitle.setText("审核未通过");
        textTitle.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            textMsg.setText(bundle.getString("Reason"));
        }
        String title = bundle.getString("Title");
        if (!StringUtils.isEmpty(title)) {
            textTitle.setText(title);
            textCause.setText("可能原因如下：");
            btnUpload.setText("返回任务中心");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnUpload:
                setResult(RESULT_OK);
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
