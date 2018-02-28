package com.yatang.xc.xcr.activity;

import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;

/**
 * 关于雅堂小超
 * Created by Chanson on 2017/3/22.
 */
@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("关于雅堂小超");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("关于雅堂小超");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText(getResources().getString(R.string.about_yt_super));
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLeft:
                finish();
                break;
        }
    }
}
