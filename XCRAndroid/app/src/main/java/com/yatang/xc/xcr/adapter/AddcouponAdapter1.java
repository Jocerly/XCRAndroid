package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/24.
 */

public class AddcouponAdapter1 extends BaseAdapter {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private OnDeletelistener onDeletelistener;

    public AddcouponAdapter1(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setOnDeletelistener(OnDeletelistener onDeletelistener) {
        this.onDeletelistener = onDeletelistener;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_addcoup, null);
            holder = new ViewHolder();
            holder.CouponBalance = (TextView) convertView.findViewById(R.id.CouponBalance);
            holder.UseCondition = (TextView) convertView.findViewById(R.id.UseCondition);
            holder.Duration = (TextView) convertView.findViewById(R.id.Duration);
            holder.btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
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
        return convertView;
    }

    class ViewHolder {
        private TextView CouponBalance;
        private TextView UseCondition;
        private TextView Duration;
        private ImageView btnDelete;
    }

    public interface OnDeletelistener {
        void onDelete(int position);
    }
}
