package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class PaymentDetailAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private OnItemClickLinster onItemClickLinster;

    public PaymentDetailAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_detail, parent, false);
        return new PaymentDetailAdapter.ViewHolder(view);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, final int position) {
        if (myViewHolder instanceof PaymentDetailAdapter.ViewHolder) {
            //常规item
            PaymentDetailAdapter.ViewHolder viewHolder = (PaymentDetailAdapter.ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            viewHolder.textNO.setText(entity.get("TicketNo"));
            viewHolder.textTime.setText(entity.get("Time"));
            String temp = Common.formatTosepara(entity.get("TicketAccount"), 3, 2);
            if (!StringUtils.isEmpty(temp)) {
                viewHolder.textValue.setText("￥" + temp);
            }
            showStatue(viewHolder.textType, entity.get("TicketType"));
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
     * 显示小票类型
     *
     * @param textStatue
     * @param TicketType 1：售，2：退
     */
    private void showStatue(TextView textStatue, String TicketType) {
        switch (TicketType) {
            case "1":
                textStatue.setText("售");
                textStatue.setBackgroundResource(R.drawable.icon_sale);
                break;
            case "2":
                textStatue.setText("退");
                textStatue.setBackgroundResource(R.drawable.icon_return);
                break;
        }
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textType;
        private TextView textNO;
        private TextView textTime;
        private TextView textValue;

        public ViewHolder(View convertView) {
            super(convertView);
            textType = (TextView) convertView.findViewById(R.id.textType);
            textNO = (TextView) convertView.findViewById(R.id.textNO);
            textTime = (TextView) convertView.findViewById(R.id.textTime);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
        }
    }

    public interface OnItemClickLinster {
        public void itemClickLinster(String id, String statue);

        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }
}
