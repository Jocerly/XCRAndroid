package com.yatang.xc.xcr.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.fragment.LeftFragment;
import com.yatang.xc.xcr.fragment.MainFragment;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.widget.slide.SlidingMenu;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * 主Activity
 *
 * @author asus
 */
@SuppressLint("NewApi")
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @BindView(id = R.id.textTitle)
    private TextView textTitle;//标题栏
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    private MainFragment mainFragment;
    private LeftFragment leftFragment;
    private SlidingMenu slidingMenu = null;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("首页");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("首页");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initData() {
        JPushInterface.resumePush(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initWidget() {
        mainFragment = new MainFragment();
        leftFragment = new LeftFragment();
        //替换主界面内容
        getSupportFragmentManager().beginTransaction().replace(R.id.llFragment, mainFragment).commit();

        //实例化菜单控件
        slidingMenu = new SlidingMenu(aty);
        //设置相关属性
        slidingMenu.setMode(SlidingMenu.LEFT);//菜单靠左
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏支持触摸拖拉
        slidingMenu.setBehindOffset(300);//设置菜单大小
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);//不包含ActionBar
        slidingMenu.setMenu(R.layout.fragment_left);
        slidingMenu.setOffsetFadeDegree(0.4f);
        //替换掉菜单内容
        getSupportFragmentManager().beginTransaction().replace(R.id.leftmenu, leftFragment).commit();
        detachLayout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    /**
     * 控制slidingMenu的打开与关闭
     */
    public void slidingMenuToggle() {
        slidingMenu.toggle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (slidingMenu.isMenuShowing()) {//显示着侧滑栏部分的
                slidingMenu.toggle();
                return true;
            } else {
                Timer tExit = null;
                if (isExit == false) {
                    isExit = true; // 准备
                    toast(getResources().getString(R.string.exit_toast));
                    tExit = new Timer();
                    tExit.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isExit = false; // 取消
                        }
                    }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任
                } else {
                    exit();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void doEmpLoginOut() {
        super.doEmpLoginOut();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        }
    }

    public void refrashMsgNum() {
        mainFragment.setMsgNum();
    }
}
