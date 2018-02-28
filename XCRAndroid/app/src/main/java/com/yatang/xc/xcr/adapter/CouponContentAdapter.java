package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/23.
 */

public class CouponContentAdapter extends RecyclerView.Adapter<CouponContentAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private String eventStatus = "1";

    public CouponContentAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    @Override
    public CouponContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_addcoup, parent, false);
        return new CouponContentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CouponContentAdapter.ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.CouponBalance.setText("￥" + map.get("CouponBalance"));
        holder.UseCondition.setText("满" + map.get("UseCondition") + "元可用");
        holder.Duration.setText("领券后" + map.get("Duration") + "天内有效");
        holder.btnDelete.setVisibility(View.GONE);
        if ("3".equals(eventStatus)) {
            holder.linRoot.setBackgroundResource(R.drawable.coupongray);
        } else {
            holder.linRoot.setBackgroundResource(R.drawable.coupon);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView CouponBalance;
        private TextView UseCondition;
        private TextView Duration;
        private ImageView btnDelete;
        private LinearLayout linRoot;

        public ViewHolder(View convertView) {
            super(convertView);
            CouponBalance = (TextView) convertView.findViewById(R.id.CouponBalance);
            UseCondition = (TextView) convertView.findViewById(R.id.UseCondition);
            Duration = (TextView) convertView.findViewById(R.id.Duration);
            btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            linRoot = (LinearLayout) convertView.findViewById(R.id.linRoot);
        }
    }
}