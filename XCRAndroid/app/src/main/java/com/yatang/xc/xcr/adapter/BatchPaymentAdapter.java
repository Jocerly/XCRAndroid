package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/5/22.
 */

public class BatchPaymentAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private OnItemClickLinster onItemClickLinster;

    public BatchPaymentAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_batchpayment, parent, false);
        return new BatchPaymentAdapter.ViewHolder(view);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, final int position) {
        if (myViewHolder instanceof BatchPaymentAdapter.ViewHolder) {
            //常规item
            BatchPaymentAdapter.ViewHolder viewHolder = (BatchPaymentAdapter.ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            String time = entity.get("Time");
            String date = Common.stampToDate(time, "yyyy-MM-dd");//将时间戳转换为日期。
            viewHolder.textDate.setText(Common.isToday(date) ? "今日" : date.replace("-", "."));
            viewHolder.textTime.setText(Common.stampToDate(time, "HH:mm:ss"));//将时间戳转换为时分秒。
            viewHolder.textWeekDay.setText(Common.getWeekBDayyDateStr(date));
            String temp = Common.formatTosepara(entity.get("ForPaymentValue"), 3, 2);
            if (!StringUtils.isEmpty(temp)) {
                viewHolder.textValue.setText("￥" + temp);
            }
            showStatue(viewHolder.textType, entity.get("ForPaymentStatue"));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLinster.itemClickLinster(entity.get("ForPaymentId"), entity.get("ForPaymentStatue"));
                }
            });
        } else if (myViewHolder instanceof FooterHolder) {
            //底部加载更多item
            final FooterHolder footerHolder = (FooterHolder) myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemClickLinster.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    /**
     * 显示结算状态
     *
     * @param textStatue
     * @param settlementStatue 0：未结款，1：已结款，2：支付中
     */
    private void showStatue(TextView textStatue, String settlementStatue) {
        switch (settlementStatue) {
            case "0":
                textStatue.setText("未结款");
                textStatue.setBackgroundResource(R.drawable.settlement_statue_error);
                textStatue.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            case "1":
                textStatue.setText("已结款");
                textStatue.setBackgroundResource(R.drawable.settlement_statue_yes);
                textStatue.setTextColor(mContext.getResources().getColor(R.color.blue));
                break;
            case "2":
                textStatue.setText("支付中");
                textStatue.setBackgroundResource(R.drawable.settlement_statue_no);
                textStatue.setTextColor(mContext.getResources().getColor(R.color.orange));
                break;
        }
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textWeekDay;
        private TextView textDate;
        private TextView textTime;
        private TextView textType;
        private TextView textValue;

        public ViewHolder(View convertView) {
            super(convertView);
            textWeekDay = (TextView) convertView.findViewById(R.id.textWeekDay);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textTime = (TextView) convertView.findViewById(R.id.textTime);
            textType = (TextView) convertView.findViewById(R.id.textType);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
        }
    }

    public interface OnItemClickLinster {
        public void itemClickLinster(String id, String ForPaymentStatue);

        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }
}
