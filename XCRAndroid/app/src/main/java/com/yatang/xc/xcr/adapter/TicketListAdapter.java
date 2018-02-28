package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小票列表Adapter
 * Created by Jocerly on 2017/5/17.
 */

public class TicketListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemclickLister onItemclickLister;
    private String key;

    public TicketListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            String temp = Common.formatTosepara(entity.get("TicketAccount"), 3, 2);
            if (!StringUtils.isEmpty(entity.get("TicketAccount")) && !StringUtils.isEmpty(temp)) {
                if(Float.parseFloat(entity.get("TicketAccount")) > 0) {
                    viewHolder.textValue.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    viewHolder.textValue.setTextColor(context.getResources().getColor(R.color.deep_green));
                }
            }
            viewHolder.textValue.setText("￥" + temp);
            if (StringUtils.isEmpty(key)) {
                viewHolder.textName.setText(entity.get("TicketNo"));
            } else {
                viewHolder.textName.setText(getSpannableString(key, entity.get("TicketNo")));
            }
            showPayType(viewHolder.imgTicketType, viewHolder.textElectronic, entity.get("PayType"));

            viewHolder.textTip.setText(entity.get("Time"));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemclickLister.OnItemClick(entity.get("TicketNo"), entity.get("TicketId"));
                }
            });
        } else if (myViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemclickLister.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    /**
     * 支付方式显示
     * @param imgTicketType
     * @param textElectronic
     * @param payType
     */
    private void showPayType(ImageView imgTicketType, TextView textElectronic, String payType) {
        if (StringUtils.isEmpty(payType)) {
            imgTicketType.setImageResource(R.drawable.ticket_electronic);
            return;
        }
        switch (Integer.parseInt(payType)) {//1：现金、2203：微信、3：电子券、2210：支付宝、5：电子券+微信、6：电子券+支付宝
            case 1:
                textElectronic.setVisibility(View.GONE);
                imgTicketType.setImageResource(R.drawable.ticket_cash);
                break;
            case 2203:
                textElectronic.setVisibility(View.GONE);
                imgTicketType.setImageResource(R.drawable.ticket_weixin);
                break;
            case 3:
                textElectronic.setVisibility(View.VISIBLE);
                imgTicketType.setImageResource(R.drawable.ticket_electronic);
                break;
            case 2210:
                textElectronic.setVisibility(View.GONE);
                imgTicketType.setImageResource(R.drawable.ticket_zhifubao);
                break;
            case 5:
                textElectronic.setVisibility(View.VISIBLE);
                imgTicketType.setImageResource(R.drawable.ticket_weixin);
                break;
            case 6:
                textElectronic.setVisibility(View.VISIBLE);
                imgTicketType.setImageResource(R.drawable.ticket_zhifubao);
                break;
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private ImageView imgTicketType;
        private TextView textName;
        private TextView textTip;
        private TextView textElectronic;
        private TextView textValue;

        public ViewHolder(View convertView) {
            super(convertView);
            imgTicketType = (ImageView) convertView.findViewById(R.id.imgTicketType);
            textElectronic = (TextView) convertView.findViewById(R.id.textElectronic);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textTip = (TextView) convertView.findViewById(R.id.textTip);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
        }
    }

    public interface OnItemclickLister {
        /**
         * 点击底部区域
         */
        public void OnFooterClick();

        public void OnItemClick(String id, String ticketId);
    }

    public void setOnItemclickLister(OnItemclickLister onItemclickLister) {
        this.onItemclickLister = onItemclickLister;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private SpannableString getSpannableString(String key, String str) {
        SpannableString s = new SpannableString(str);
        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
}
