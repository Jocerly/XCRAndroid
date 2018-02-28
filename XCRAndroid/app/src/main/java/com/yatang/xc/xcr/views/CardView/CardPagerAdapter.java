package com.yatang.xc.xcr.views.CardView;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BaseActivity;
import com.yatang.xc.xcr.activity.NewRevenueActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.RevenueBaseEn;
import com.yatang.xc.xcr.db.RevenueEntity;
import com.yatang.xc.xcr.dialog.SelectDayTimeDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zengxiaowen on 2017/7/17.
 */
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<View> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private titOnClickListener listener;
    private SelectDayTimeDialog timeDialog;
    private BaseActivity mContext;

    private int Type = NewRevenueActivity.MEND;  //查询类型

    private TextView titleTextView, contentTextView;

    private String strTime = "自定义日期";

    private int mChildCount = 0;

    public CardPagerAdapter(final BaseActivity context) {
        this.mContext = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        timeDialog = new SelectDayTimeDialog(context);
        timeDialog.setOnClickListener(new SelectDayTimeDialog.OnClickListener() {

            @Override
            public boolean onSure(String startTime, String endTime) {
                if (endTime.equals("结束时间")) {
                    mContext.toast("请选择结束时间");
                }else if (!isCurrentDate(startTime,endTime)){
                    mContext.toast("开始时间不能大于结束时间");
                } else {
//                    contentTextView.setText(startTime + " 至 " + endTime + " (元)");
                    strTime = startTime + " 至 " + endTime + " (元)";
                    getRevenueDetial(startTime, endTime);
                    timeDialog.dismiss();
                }
                return true;
            }

            @Override
            public boolean onCancel() {
                return false;
            }
        });
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public void setCardItem(int i,CardItem item){
        mData.set(i,item);
    }

    public void setType(int type) {
        Type = type;
    }

    public void clear() {
        mViews.clear();
        mData.clear();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }



    @Override
    public View getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public List<View> getCardView() {
        return mViews;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_revenue, container, false);
        container.addView(view);
        bind(position, view);
        View cardView =  view.findViewById(R.id.cardView);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final int position, View view) {
        CardItem item = mData.get(position);
        titleTextView = (TextView) view.findViewById(R.id.textRevenueAllValue);
        contentTextView = (TextView) view.findViewById(R.id.textRevenueMsg);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
        if (position==5) {
            Drawable nav_up=mContext.getResources().getDrawable(R.drawable.green_right);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            contentTextView.setCompoundDrawables(null, null, nav_up, null);
        }
        contentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 5) {
                    timeDialog.show(180);
                }
            }
        });

    }

    /**
     * 时间对比
     *
     * @param start
     * @param end
     * @return
     */
    private boolean isCurrentDate(String start, String end) {
        if (start.equals(end)) {
            return true;
        }

        boolean current = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(start);
            Date dt2 = df.parse(end);
            if (dt1.getTime() > dt2.getTime()) {
                current = false;
            } else {
                current = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return current;
    }

    public void settitOnClickListener(titOnClickListener listener) {
        this.listener = listener;
    }

    public interface titOnClickListener {
        void ontiClick(RevenueEntity entity,String time);
    }

    /**
     * 获取数据
     */
    private void getRevenueDetial(String sTime, String eTime) {
        mContext.params.clear();
        mContext.params.put("UserId", MyApplication.instance.UserId);
        mContext.params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        mContext.params.put("Token", MyApplication.instance.Token);
        mContext.params.put("ScreenStatue", "5");
        mContext.params.put("Type", Type);
        mContext.params.put("StartDate", sTime);
        mContext.params.put("EndDate", eTime);
        mContext.httpRequestService.doRequestData(mContext, "User/RevenueOrOutDetial", mContext.params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {

                if (Constants.M00.equals(resultParam.resultId)) {
                    RevenueBaseEn baseEn = new Gson().fromJson(resultParam.mapData.toString(),RevenueBaseEn.class);
                    RevenueEntity ob = baseEn.getRevenueList().get(0);
                    Message msg = mHandler.obtainMessage();
                    msg.obj = ob;
                    msg.sendToTarget();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    mContext.toast("帐号过期，请重新登录");
                    mContext.doEmpLoginOut();
                } else {
                    mContext.toast(resultParam.message);
                }
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RevenueEntity entity = (RevenueEntity) msg.obj;
            String temp = Common.formatTosepara(entity.getRevenueAllValue()+"", 3, 2);
            titleTextView.setText(temp);
            if (listener != null) listener.ontiClick(entity,strTime);
        }
    };

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
