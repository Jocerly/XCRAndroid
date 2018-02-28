package com.yatang.xc.xcr.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.StoreMsgAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.FullyLinearLayoutManager;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 门店信息Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_store_msg)
public class StoreListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textUserNo)
    private TextView textUserNo;
    @BindView(id = R.id.textUserName)
    private TextView textUserName;
    @BindView(id = R.id.textUserPhone)
    private TextView textUserPhone;
    @BindView(id = R.id.textFinancialAccount)
    private TextView textFinancialAccount;
    @BindView(id = R.id.textBankCard)
    private TextView textBankCard;
    @BindView(id = R.id.rlBankCard, click = true)
    private RelativeLayout rlBankCard;
    @BindView(id = R.id.textBankCardName)
    private TextView textBankCardName;

    @BindView(id = R.id.recyclerViewStore)
    private RecyclerView recyclerViewStore;

    private StoreMsgAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private boolean choseDefaultStore;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("门店管理");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("门店管理");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        btnRight.setVisibility(View.GONE);
        textTitle.setText(R.string.store_manage);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        choseDefaultStore = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            choseDefaultStore = bundle.getBoolean("choseDefaultStore", false);
        }
        if (choseDefaultStore) {
            textTitle.setText(R.string.choose_store);
            btnLeft.setVisibility(View.GONE);
        }
        colorGap = getResources().getColor(R.color.base_bg);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器

        adapter = new StoreMsgAdapter(aty, listData);
        adapter.setOnStoreDefaultClistener(onStoreDefaultClistener);
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(aty, LinearLayoutManager.VERTICAL, false);
        recyclerViewStore.setLayoutManager(layoutManager);
        recyclerViewStore.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad15), colorGap));
        recyclerViewStore.setAdapter(adapter);

        textUserNo.setText(MyApplication.instance.UserNo);
        textUserName.setText(MyApplication.instance.UserName);
        textUserPhone.setText(MyApplication.instance.UserPhone);
        textFinancialAccount.setText(MyApplication.instance.FinancialAccount);

        getStoreList(true);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getStoreList(false);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 获取数据
     */
    private void getStoreList(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("Token", MyApplication.instance.Token);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        httpRequestService.doRequestData(aty, "User/StoreList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    if (!StringUtils.isEmpty(resultParam.mapData.get("BankCardName")) && !StringUtils.isEmpty(resultParam.mapData.get("BankCardNum"))) {
                        textBankCard.setVisibility(View.VISIBLE);
                        textBankCardName.setText(resultParam.mapData.get("BankCardName"));
                        textBankCard.setText(bankCard(resultParam.mapData.get("BankCardNum")));
                        textBankCardName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        textBankCard.setVisibility(View.GONE);
                        textBankCardName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.next_bg, 0);
                    }
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    adapter.setDefaultStoreNo(MyApplication.instance.StoreSerialNoDefault);
                    adapter.notifyDataSetChanged();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 设置默认门店
     */
    private void setStoreDefault(final String storeNo, final ConcurrentHashMap<String, String> map) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", storeNo);
        params.put("StoreName", map.get("StoreName"));
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/SetStoreDefault", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Set_DefualtStore");

                    saveStoreInfo(storeNo, map);
                    if (choseDefaultStore) {
                        skipActivity(aty, MainActivity.class);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("StoreDefualtNo", storeNo);
                        intent.putExtra("StoreName", map.get("StoreName"));
                        intent.putExtra("StoreAbbreName", map.get("StoreAbbreName"));
                        intent.putExtra("StoreNo", map.get("StoreNo"));
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 获取银行卡信息
     */
    private void getCheckBankCard() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/CheckBankCard", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!StringUtils.isEmpty(resultParam.mapData.get("Cardholder")) && !StringUtils.isEmpty(resultParam.mapData.get("ID")) && !StringUtils.isEmpty(resultParam.mapData.get("ServiceAgreeUrl"))) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Cardholder", resultParam.mapData.get("Cardholder"));
                        bundle.putString("ID", resultParam.mapData.get("ID"));
                        bundle.putString("ServiceAgreeUrl", resultParam.mapData.get("ServiceAgreeUrl"));
                        showActivityForResult(aty, AddBankCardActivity.class, bundle, 0);
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    getStoreList(true);
                }
                break;
        }
    }

    private void saveStoreInfo(final String storeNo, ConcurrentHashMap<String, String> map) {
        MyApplication.instance.StoreSerialNoDefault = storeNo;
        MyApplication.instance.StoreSerialNameDefault = map.get("StoreName");
        MyApplication.instance.StoreAbbreName = map.get("StoreAbbreName");
        MyApplication.instance.StoreNo = map.get("StoreNo");
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNoDefault, MyApplication.instance.StoreSerialNoDefault);
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNameDefault, MyApplication.instance.StoreSerialNameDefault);
        Common.setAppInfo(aty, Constants.Preference.StoreAbbreName, MyApplication.instance.StoreAbbreName);
        Common.setAppInfo(aty, Constants.Preference.StoreNo, MyApplication.instance.StoreNo);
    }

    StoreMsgAdapter.OnStoreDefaultClistener onStoreDefaultClistener = new StoreMsgAdapter.OnStoreDefaultClistener() {
        @Override
        public void onItemNext(ConcurrentHashMap<String, String> map) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mapData", map);
            skipActivity(aty, StoreDetialActivity.class, bundle);
        }

        @Override
        public void onDefaultBack(String storeNo, ConcurrentHashMap<String, String> map) {
            setStoreDefault(storeNo, map);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.rlBankCard:
                if ("立即添加".equals(textBankCardName.getText().toString().trim())) {
                    rlBankCard.setEnabled(true);
                    getCheckBankCard();
                } else {
                    rlBankCard.setEnabled(false);
                }
                break;
        }
    }

    /**
     * 把银行卡号中间数据替换为*
     *
     * @param bankCardNum 银行卡号
     * @return
     */
    public static String bankCard(String bankCardNum) {
        if (bankCardNum.isEmpty() || bankCardNum == null) {
            return null;
        } else if (bankCardNum.length() > 4) {
            String str = bankCardNum.substring(0, 4) + "********" + bankCardNum.substring(bankCardNum.length() - 4);
            return str.replaceAll("(.{4})", "$1 ");
        } else {
            return bankCardNum;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!choseDefaultStore) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
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
        return false;
    }
}
