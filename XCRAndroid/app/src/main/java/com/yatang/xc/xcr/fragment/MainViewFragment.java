package com.yatang.xc.xcr.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.ClassesActivity;
import com.yatang.xc.xcr.activity.CouponActivity;
import com.yatang.xc.xcr.activity.DataStatisticsActivity;
import com.yatang.xc.xcr.activity.GoodsListActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.activity.MsgActivity;
import com.yatang.xc.xcr.activity.NewRevenueActivity;
import com.yatang.xc.xcr.activity.OrderManagementActivity;
import com.yatang.xc.xcr.activity.OutGoodsListActivity;
import com.yatang.xc.xcr.activity.SettlementManageActivity;
import com.yatang.xc.xcr.activity.StartActivity;
import com.yatang.xc.xcr.activity.StockListActivity;
import com.yatang.xc.xcr.activity.TaskActivity;
import com.yatang.xc.xcr.activity.TransactionActivity;
import com.yatang.xc.xcr.activity.WebViewActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.entity.MainViewEntity;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.uitls.VersionInfoHelper;
import com.yatang.xc.xcr.views.CustomGridView;
import com.yatang.xc.xcr.views.DragDeleteTextView;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.List;

/**
 * 首页图标View
 * Created by zengxiaowen on 2017/7/25.
 */

@SuppressLint("ValidFragment")
public class MainViewFragment extends BaseFragment {
    private JCSwipeRefreshLayout mSwipeLayout;
    private List<MainViewEntity> viewEntities;
    private GridAdapter gridAdapter;

    public MainViewFragment(JCSwipeRefreshLayout mSwipeLayout, List<MainViewEntity> viewEntities) {
        this.mSwipeLayout = mSwipeLayout;
        this.viewEntities = viewEntities;
    }

    public MainViewFragment() {
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        JCLoger.debug("重绘1：" + viewEntities.toString());

        CustomGridView gridView = new CustomGridView(getActivity());
        gridView.setNumColumns(3);
        gridView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        gridAdapter = new GridAdapter(getActivity(), viewEntities);
        gridView.setAdapter(gridAdapter);
        return gridView;
    }

    public void setMsgNum() {
        gridAdapter.notifyDataSetChanged();
    }

    private class GridAdapter extends BaseAdapter {
        private List<MainViewEntity> data;
        private Context context;

        public GridAdapter(Context context, List<MainViewEntity> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HView hView;
            if (convertView == null) {
                hView = new HView();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_main_view, null);
                hView.textView = (PressTextView) convertView.findViewById(R.id.icon_main);
                hView.numView = (DragDeleteTextView) convertView.findViewById(R.id.num_main);
                hView.textClassesNum = (TextView) convertView.findViewById(R.id.textClassesNum);
                convertView.setTag(hView);
            } else {
                hView = (HView) convertView.getTag();
            }
            MainViewEntity entity = data.get(position);
            if (entity == null) return convertView;
            hView.textView.setText(entity.getTitle());
            hView.textView.setCompoundDrawablesWithIntrinsicBounds(0, entity.getDrawable(), 0, 0);

            if ("0".equals(entity.getTxtNum()) || StringUtils.isEmpty(entity.getTxtNum())) {
                hView.numView.setVisibility(View.GONE);
            } else {
                hView.numView.setVisibility(View.VISIBLE);
                hView.numView.setText(entity.getTxtNum());
                hView.numView.setRefreshLayout(mSwipeLayout);
            }

            if ("小超课堂".equals(entity.getTitle())) {
                String temp = Common.getAppInfo(context, Constants.Preference.MaxClassTime, "");
                if (StringUtils.isEmpty(temp)) {
                    Common.setAppInfo(context, Constants.Preference.MaxClassTime, entity.getMaxClassTime());
                } else {
                    if (Long.parseLong(entity.getMaxClassTime()) > Long.parseLong(temp)) {
                        hView.textClassesNum.setVisibility(View.VISIBLE);
                    } else {
                        hView.textClassesNum.setVisibility(View.GONE);
                    }
                }
            }

            hView.textView.setOnClickListener(new onClick(context, entity.getIndex(), entity.getMaxClassTime()));
            return convertView;
        }
    }

    private class HView {
        PressTextView textView;
        DragDeleteTextView numView;
        TextView textClassesNum;
    }

    private class onClick implements View.OnClickListener {
        int index;
        Context context;
        String max;

        public onClick(Context context, int index, String maxTime) {
            this.context = context;
            this.index = index;
            this.max = maxTime;
        }

        @Override
        public void onClick(View v) {
            switch (index) {
                case 1: //门店收入
                    MobclickAgent.onEvent(context, "Home_Revenue");
                    if (max.equals("0")) {
                        skipActivity(context, NewRevenueActivity.class);
                    } else {
                        Common.setAppInfo(context, Constants.Preference.MaxClassTime, max);
                        skipActivity(context, ClassesActivity.class);
                    }
                    break;
                case 2: //交易流水
                    MobclickAgent.onEvent(context, "Home_Transaction");
                    skipActivity(context, TransactionActivity.class);
                    break;
                case 3: //消息
                    MobclickAgent.onEvent(context, "Home_Msg");
                    skipActivity(context, MsgActivity.class);
                    break;
                case 5://店铺活动
                    MobclickAgent.onEvent(context, "Home_Event");
                    skipActivity(context, CouponActivity.class);
                    break;
                case 9://商品列表
                    MobclickAgent.onEvent(context, "Home_Goods");
                    skipActivity(context, GoodsListActivity.class);
                    break;
                case 6://我要进货
                    MobclickAgent.onEvent(context, "Home_Supply");
                    skipActivity(aty, VersionInfoHelper.checkWebFileExists() ? CordovaPageActivity.class : StartActivity.class);
                    break;
                case 11://结算管理
                    MobclickAgent.onEvent(context, "Home_Settle");
                    skipActivity(context, SettlementManageActivity.class);
                    break;
                case 4://订单管理
                    MobclickAgent.onEvent(context, "Home_OutOder");
                    skipActivity(context, OrderManagementActivity.class);
                    break;
                case 7://外送商品
                    MobclickAgent.onEvent(context, "Home_OutGoods");
                    skipActivity(context, OutGoodsListActivity.class);
                    break;
                case 8://库存列表
                    MobclickAgent.onEvent(context, "Home_Stock");
                    skipActivity(context, StockListActivity.class);
                    break;
                case 10://数据统计
                    MobclickAgent.onEvent(context, "Home_DataStatistics");
                    skipActivity(context, DataStatisticsActivity.class);
                    break;
                case 12://我的任务
                    MobclickAgent.onEvent(context, "Home_Task");
                    skipActivity(context, TaskActivity.class);
                    break;
                case 13://小超课堂
                    MobclickAgent.onEvent(context, "Home_Classes");
                    Common.setAppInfo(context, Constants.Preference.MaxClassTime, max);
                    skipActivity(context, ClassesActivity.class);
                    break;
                case 14://在线客服
                    MobclickAgent.onEvent(context, "Home_Call");
                    if (!MyApplication.instance.isX5Over) {
                        toast(R.string.x5_toast);
                        return;
                    }
                    getPurchaseList();
                    break;
            }
        }
    }

    /**
     * 获取雅堂采购Url
     */
    private void getPurchaseList(final Context act) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData((Activity) act, "User/PurchaseList", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ClassUrl", resultParam.mapData.get("Url"));
                    bundle.putString("ClassName", "我要进货");
                    skipActivity(act, WebViewActivity.class, bundle);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast("帐号过期，请重新登录");
                    ((MainActivity) act).doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 在线客服
     */
    private void getPurchaseList() {
        params.clear();
        params.put("UserName", MyApplication.instance.UserName);
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("UserName", MyApplication.instance.StoreSerialNameDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/ConnectCustomerSer", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ClassUrl", resultParam.mapData.get("CCSUrl"));
                    bundle.putString("ClassName", "联系客服");
                    skipActivity(aty, WebViewActivity.class, bundle);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    ((MainActivity) getActivity()).doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }
}
