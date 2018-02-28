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
 * 交易流水Adapter
 * Created by Jocerly on 2017/3/10.
 */

public class TransactionAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemclickLister onItemclickLister;

    public TransactionAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            String temp = Common.formatTosepara(entity.get("TransactionDaily"),3,2);
            if(!StringUtils.isEmpty(temp)){
                viewHolder.textValue.setText("￥"+temp);
            }
            viewHolder.textDate.setText(Common.isToday(entity.get("Date")) ? "今日" : entity.get("Date"));
            viewHolder.textWeekDay.setText(Common.getWeekBDayyDateStr(entity.get("Date")));
            if (StringUtils.isEmpty(entity.get("TicketNum")) || "0".equals(entity.get("TicketNum"))) {
                viewHolder.textTicketNum.setVisibility(View.GONE);
            } else {
                viewHolder.textTicketNum.setVisibility(View.VISIBLE);
                viewHolder.textTicketNum.setText(entity.get("TicketNum") + "笔");
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemclickLister.OnItemClick(entity.get("Date"));
                }
            });
        } else if (myViewHolder instanceof FooterHolder){
            final FooterHolder footerHolder = (FooterHolder)myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE){
                        onItemclickLister.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textWeekDay;
        private TextView textDate;
        private TextView textValue;
        private TextView textTicketNum;

        public ViewHolder(View convertView) {
            super(convertView);
            textWeekDay = (TextView) convertView.findViewById(R.id.textWeekDay);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
            textTicketNum = (TextView) convertView.findViewById(R.id.textTicketNum);
        }
    }

    public interface OnItemclickLister {
        /**
         * 点击底部区域
         */
        public void OnFooterClick();
        public void OnItemClick(String Date);
    }

    public void setOnItemclickLister(OnItemclickLister onItemclickLister) {
        this.onItemclickLister = onItemclickLister;
    }
}
