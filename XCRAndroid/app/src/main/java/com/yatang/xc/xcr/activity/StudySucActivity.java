package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;

/**
 * 学习完成页面类
 * @author dengjiang
 * 2017/3/30.
 */
@ContentView(R.layout.activity_study_suc)
public class StudySucActivity extends BaseActivity{

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;

    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.btnBackTask, click = true)
    private TextView btnBackTask;

    @BindView(id = R.id.btnBackMain, click = true)
    private TextView btnBackMain;

    @BindView(id = R.id.textToShow)
    private TextView textToShow;

    @Override
    public void initWidget() {
        textTitle.setText(getResources().getString(R.string.txt_title_finished));
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            btnBackMain.setText(bundle.getString("ReturnText","小超课堂"));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                finish();
                break;
            case R.id.btnBackTask:
                finish();
                break;
            case R.id.btnBackMain:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}