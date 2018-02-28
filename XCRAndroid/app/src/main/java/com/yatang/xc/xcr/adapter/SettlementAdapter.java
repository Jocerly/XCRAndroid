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
 * 结算管理Adapter
 * Created by Jocerly on 2017/3/10.
 */

public class SettlementAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemClickLinster onItemClickLinster;
    private int type;

    public SettlementAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    public void setType(int type) {// 1:订单结算，2:优惠券结算
        this.type = type;
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_settlement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, final int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            viewHolder.textDate.setText(Common.isToday(entity.get("Date")) ? "今日" : entity.get("Date"));
            if (StringUtils.isEmpty(entity.get("Date"))) {
                viewHolder.textWeekDay.setText(" ");
            } else {
                viewHolder.textWeekDay.setText(Common.getWeekBDayyDateStr(entity.get("Date")));
            }

            String temp = Common.formatTosepara(entity.get("SettlementValue"),3,2);
            if(!StringUtils.isEmpty(temp)){
                viewHolder.textValue.setText("￥"+temp);
            }
            showStatue(viewHolder.textStatue, viewHolder.textWaitSure, entity.get("SettlementStatue"), entity.get("IsFinished"));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLinster.itemClickLinster(entity, type);
                }
            });
            }else if (myViewHolder instanceof FooterHolder){
            final FooterHolder footerHolder = (FooterHolder)myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE){
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
     * @param textStatue
     * @param settlementStatue 0：未结算，2：已结算，3：结算失败
     * @param isFinished 1:是，0：否
     */
    private void showStatue(TextView textStatue, TextView textWaitSure, String settlementStatue, String isFinished) {
        switch (Integer.parseInt(settlementStatue)) {
            case 0:
                switch (type){
                    case 1:
                        switch (Integer.parseInt(isFinished)){
                            case 0:
                                textStatue.setText("未确认");
                                textStatue.setBackgroundResource(R.drawable.unconfirmed);
                                textStatue.setTextColor(context.getResources().getColor(R.color.orange_light));
                                textWaitSure.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                textStatue.setText("未结算");
                                textStatue.setBackgroundResource(R.drawable.settlement_statue_no);
                                textStatue.setTextColor(context.getResources().getColor(R.color.orange));
                                textWaitSure.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case 2:
                        textStatue.setText("未结算");
                        textStatue.setBackgroundResource(R.drawable.settlement_statue_no);
                        textStatue.setTextColor(context.getResources().getColor(R.color.orange));
                        textWaitSure.setVisibility(View.GONE);
                        break;
                }
                break;
            case 2:
                textStatue.setText("已结算");
                textStatue.setBackgroundResource(R.drawable.settlement_statue_yes);
                textStatue.setTextColor(context.getResources().getColor(R.color.blue));
                textWaitSure.setVisibility(View.GONE);
                break;
            case 3:
                textStatue.setText("结算失败");
                textStatue.setBackgroundResource(R.drawable.settlement_statue_error);
                textStatue.setTextColor(context.getResources().getColor(R.color.red));
                textWaitSure.setVisibility(View.GONE);
                break;
        }
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textWeekDay;
        private TextView textDate;
        private TextView textStatue;
        private TextView textValue;
        private TextView textWaitSure;

        public ViewHolder(View convertView) {
            super(convertView);
            textWeekDay = (TextView) convertView.findViewById(R.id.textWeekDay);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textStatue = (TextView) convertView.findViewById(R.id.textStatue);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
            textWaitSure=(TextView) convertView.findViewById(R.id.textWaitSure);
        }
    }

    public interface OnItemClickLinster {
       public void itemClickLinster(ConcurrentHashMap<String, String> mapData, int type);
        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }
}
