package com.yatang.xc.xcr.config;

import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BaseActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.activity.MsgActivity;
import com.yatang.xc.xcr.views.PressTextView;

import static com.yatang.xc.xcr.R.id.btnRight;
import static com.yatang.xc.xcr.uitls.Common.getAppInfo;

/**
 * ActionBar管理器
 *
 * @author zxw
 * @version 1.0
 */
public class ActionBarManager {

    /**
     * 设置居中标题
     *
     * @param mContext 上下文
     * @param title    主标题
     */
    public static Toolbar initTitleToolbar(final BaseActivity mContext, String title) {

        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        mContext.setSupportActionBar(toolbar);
        return toolbar;
    }

    /**
     * 订制一个返回+标题的标题栏
     *
     * @param mContext
     * @param title
     * @param color
     */
    public static Toolbar initBackToolbar(final BaseActivity mContext, String title, int color) {
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(color);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        mContext.setSupportActionBar(toolbar);

        mContext.findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });

//        toolbar.setNavigationIcon(R.drawable.back);//设置Navigation 图标
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.finish();
//            }
//        });

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = mContext.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);

                //底部导航栏
                //window.setNavigationBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toolbar;
    }

    /**
     * 订制一个返回+标题的标题栏+右按键
     *
     * @param mContext
     * @param title
     * @param right
     * @param color
     * @return 标题+右侧按钮
     */
    public static TextView[] initBackToolbar(final BaseActivity mContext, String title, String right, int color) {
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(color);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        mContext.setSupportActionBar(toolbar);

        mContext.findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });

        TextView tvRight = (TextView) mContext.findViewById(R.id.btnRight);
        tvRight.setVisibility(right.isEmpty() ? View.GONE : View.VISIBLE);
        tvRight.setText(right);

//        toolbar.setNavigationIcon(R.drawable.back);//设置Navigation 图标
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.finish();
//            }
//        });
        TextView[]  textViews= new TextView[]{tvTitle,tvRight};
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = mContext.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);

                //底部导航栏
                //window.setNavigationBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textViews;
    }


    enum TOOLBARBTN {
        TIJIAO, SEARCH, FAV, SETTINGS, SHARE, SUBMIT, ADD, EDIT, DEAL, MENDIAN, WAISONG
    }

    /**
     * 初始化【提交】右侧按钮菜单
     *
     * @param mOptionsMenu
     */
    public static void initActionBarSubmitButton(Menu mOptionsMenu, TOOLBARBTN type) {
        final MenuItem aboutItem = mOptionsMenu.findItem(R.id.action_about);
        if (null != aboutItem) {
            aboutItem.setVisible(false);
        }

        final MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        if (null != searchItem) {
            searchItem.setVisible(false);
        }

        final MenuItem favItem = mOptionsMenu.findItem(R.id.action_fav);
        if (null != favItem) {
            favItem.setVisible(false);
        }

        final MenuItem settingItem = mOptionsMenu.findItem(R.id.action_settings);
        if (null != settingItem) {
            settingItem.setVisible(false);
        }

        final MenuItem shareItem = mOptionsMenu.findItem(R.id.action_share);
        if (null != shareItem) {
            shareItem.setVisible(false);
        }

        final MenuItem submitItem = mOptionsMenu.findItem(R.id.action_submit);
        if (null != submitItem) {
            submitItem.setVisible(false);
        }

        final MenuItem addItem = mOptionsMenu.findItem(R.id.action_add);
        if (null != addItem) {
            addItem.setVisible(false);
        }
        final MenuItem editItem = mOptionsMenu.findItem(R.id.action_edit);
        if (null != editItem) {
            editItem.setVisible(false);
        }
        final MenuItem mendItem = mOptionsMenu.findItem(R.id.action_mendian);
        if (null != mendItem) {
            mendItem.setVisible(false);
        }
        final MenuItem waiItem = mOptionsMenu.findItem(R.id.action_waisong);
        if (null != waiItem) {
            waiItem.setVisible(false);
        }

        switch (type) {
            case TIJIAO:
                if (null != aboutItem) {
                    aboutItem.setVisible(true);
                }
                break;
            case SEARCH:
                if (null != searchItem) {
                    searchItem.setVisible(true);
                }
                break;
            case FAV:
                if (null != favItem) {
                    favItem.setVisible(true);
                }
                break;
            case SETTINGS:
                if (null != settingItem) {
                    settingItem.setVisible(true);
                }
                break;
            case SHARE:
                if (null != shareItem) {
                    shareItem.setVisible(true);
                }
                break;
            case SUBMIT:
                if (null != submitItem) {
                    submitItem.setVisible(true);
                }
                break;
            case ADD:
                if (null != addItem) {
                    addItem.setVisible(true);
                }
                break;
            case EDIT:
                if (null != editItem) {
                    editItem.setVisible(true);
                }
                break;
            case DEAL:
                break;
            case MENDIAN:
                if (null != mendItem) {
                    mendItem.setVisible(true);
                }
                break;
            case WAISONG:
                if (null != waiItem) {
                    waiItem.setVisible(true);
                }
                break;
        }
    }
}
