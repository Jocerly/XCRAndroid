package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/11/6.
 */

public class OrderDiscountLAdapter extends BaseAdapter {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;

    public OrderDiscountLAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_orderdiscount, null);
            holder = new ViewHolder();
            holder.textDiscountName = (TextView) convertView.findViewById(R.id.textDiscountName);
            holder.textDiscount = (TextView) convertView.findViewById(R.id.textDiscount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        final ConcurrentHashMap<String, String> map = listData.get(position);
        holder = (ViewHolder) convertView.getTag();
        holder.textDiscountName.setText((map.get("DiscountName")));
        JCLoger.debug("DiscountName=" + map.get("DiscountName"));
        holder.textDiscount.setText("￥" + Common.formatFloat(map.get("Discount")));
        return convertView;
    }

    class ViewHolder {
        private TextView textDiscountName;
        private TextView textDiscount;
    }

}
