package com.yatang.xc.xcr.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.AboutActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.activity.StoreListActivity;
import com.yatang.xc.xcr.activity.StoreManageActivity;
import com.yatang.xc.xcr.activity.VipCenterActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.uitls.AppAutoUpdate;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;
import org.jocerly.jcannotation.widget.CircleImageView;

/**
 * 侧滑Fragment
 *
 * @author Jocerly
 */
public class LeftFragment extends BaseFragment {
    @BindView(id = R.id.imagePic)
    private CircleImageView imagePic;
    @BindView(id = R.id.textStoreNo)
    private TextView textStoreNo;

    @BindView(id = R.id.llVipCenter, click = true)
    private LinearLayout llVipCenter;
    @BindView(id = R.id.textStoreMsg, click = true)
    private TextView textStoreMsg;
    @BindView(id = R.id.textStoreManage, click = true)
    private TextView textStoreManage;
    @BindView(id = R.id.textAboutYtSuper, click = true)
    private TextView textAboutYtSuper;
    @BindView(id = R.id.textCall, click = true)
    private TextView textCall;
    @BindView(id = R.id.textUpdate, click = true)
    private TextView textUpdate;
    @BindView(id = R.id.textLoginOut, click = true)
    private TextView textLoginOut;
    @BindView(id = R.id.textVersion)
    private TextView textVersion;
    @BindView(id = R.id.textVipCenter)
    private TextView textVipCenter;

    private NomalDialog dialog;
    private AppAutoUpdate appAutoUpdate;
    private String callNo;

    @Override
    protected void initData() {
        super.initData();
        appAutoUpdate = new AppAutoUpdate(aty);
        appAutoUpdate.setOnAppUpdateClickLister(onAppUpdateClickLister);

        textVersion.setText(SystemTool.getAppVersionName(aty));
        textStoreNo.setText(MyApplication.instance.StoreNo);
        try {
            textVipCenter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.left_user, 0, getLevelPic(), 0);
        } catch (Exception e) {
        }
        Picasso.with(aty)
                .load(StringUtils.isEmpty(MyApplication.instance.StoreSerialPicDefault) ? "0" : MyApplication.instance.StoreSerialPicDefault)
                .error(R.drawable.defaulthead)
                .placeholder(R.drawable.defaulthead)
                .into(imagePic);
        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(onNoamlLickListene);
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            ((MainActivity) getActivity()).doEmpLoginOut();
        }
    };

    AppAutoUpdate.OnAppUpdateClickLister onAppUpdateClickLister = new AppAutoUpdate.OnAppUpdateClickLister() {

        @Override
        public void OnCancleClickLister() {
            //不做任何事情
        }
    };

    Handler handler = new Handler() {
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llVipCenter:
                showActivity(aty, VipCenterActivity.class);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).slidingMenuToggle();
                    }
                }, 500);
                break;
            case R.id.textStoreMsg:
                showActivityForResult(aty, StoreListActivity.class, Constants.ForResult.CHOICE_STORE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).slidingMenuToggle();
                    }
                }, 500);
                break;
            case R.id.textAboutYtSuper:
                showActivity(aty, AboutActivity.class);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).slidingMenuToggle();
                    }
                }, 500);
                break;
            case R.id.textCall:
                MobclickAgent.onEvent(aty, "Set_Call");
                callNo = textCall.getText().toString().replace("-", "");
                if (SystemTool.checkSelfPermission(aty, Manifest.permission.CALL_PHONE)) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.Permission.CALL_PHONE);
                } else {
                    doCall();
                }
                break;
            case R.id.textUpdate:
                if (SystemTool.checkSelfPermission(aty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.Permission.EXTERNAL_STORAGE);
                } else {
                    appAutoUpdate.checkVersion(true);
                }
                break;
            case R.id.textLoginOut:
                dialog.show("退出登录？");
                break;
            case R.id.textStoreManage:
                showActivity(aty, StoreManageActivity.class);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).slidingMenuToggle();
                    }
                }, 500);
                break;
        }
    }

    /**
     * 打电话
     */
    private void doCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + callNo);
            intent.setData(data);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.layout_menu, null);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.CHOICE_STORE:
                if (resultCode == Activity.RESULT_OK && data != null) {//切换门店，通知刷新数据
                    MyApplication.instance.StoreSerialNoDefault = data.getExtras().getString("StoreDefualtNo");
                    MyApplication.instance.StoreSerialNameDefault = data.getExtras().getString("StoreName");
                    MyApplication.instance.StoreAbbreName = data.getExtras().getString("StoreAbbreName");
                    MyApplication.instance.StoreNo = data.getExtras().getString("StoreNo");
                    textStoreNo.setText(MyApplication.instance.StoreNo);
                    Picasso.with(aty)
                            .load(StringUtils.isEmpty(MyApplication.instance.StoreSerialPicDefault) ? "0" : MyApplication.instance.StoreSerialPicDefault)
                            .error(R.drawable.defaulthead)
                            .placeholder(R.drawable.defaulthead)
                            .into(imagePic);
                    MyApplication.initCordova();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.Permission.CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doCall();
                } else {
                    toast("需要此权限才能拨打电话，请到设置里面打开");
                }
                break;
            case Constants.Permission.EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appAutoUpdate.checkVersion(true);
                } else {
                    toast("需要此权限才能更新版本，请到设置里面打开");
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public int getLevelPic() {
        int levelPic = 0;
        switch (Integer.parseInt(MyApplication.instance.vipIdentify)) {
            case 0:
                levelPic = R.drawable.lv0;
                break;
            case 1:
                levelPic = R.drawable.lv1;
                break;
            case 2:
                levelPic = R.drawable.lv2;
                break;
            case 3:
                levelPic = R.drawable.lv3;
                break;
            case 4:
                levelPic = R.drawable.lv4;
                break;
            case 5:
                levelPic = R.drawable.lv5;
                break;
            case 6:
                levelPic = R.drawable.lv6;
                break;
            case 7:
                levelPic = R.drawable.lv7;
                break;
            case 8:
                levelPic = R.drawable.lv8;
                break;
            case 9:
                levelPic = R.drawable.lv9;
                break;
            case 10:
                levelPic = R.drawable.lv10;
                break;
        }
        return levelPic;
    }
}
