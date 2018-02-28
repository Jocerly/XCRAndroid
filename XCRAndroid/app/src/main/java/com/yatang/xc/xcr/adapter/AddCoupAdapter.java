package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/16.
 */

public class AddCoupAdapter extends RecyclerView.Adapter<AddCoupAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private OnDeletelistener onDeletelistener;

    public AddCoupAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setOnDeletelistener(OnDeletelistener onDeletelistener) {
        this.onDeletelistener = onDeletelistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_addcoup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.CouponBalance.setText("￥" + map.get("CouponBalance"));
        holder.UseCondition.setText("满" + map.get("UseCondition") + "元可用");
        holder.Duration.setText("领券后" + map.get("Duration") + "天内有效");
        if (onDeletelistener != null) {
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeletelistener.onDelete(position);
                }
            });
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
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

        public ViewHolder(View convertView) {
            super(convertView);
            CouponBalance = (TextView) convertView.findViewById(R.id.CouponBalance);
            UseCondition = (TextView) convertView.findViewById(R.id.UseCondition);
            Duration = (TextView) convertView.findViewById(R.id.Duration);
            btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeletelistener {
        void onDelete(int position);
    }
}
