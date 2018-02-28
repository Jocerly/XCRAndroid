package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/24.
 */

public class OrderDiscountAdapter extends RecyclerView.Adapter<OrderDiscountAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局

    public OrderDiscountAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderdiscount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textDiscountName.setText((map.get("DiscountName")));
        holder.textDiscount.setText("￥"+Common.formatFloat(map.get("Discount")));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textDiscountName;
        private TextView textDiscount;

        public ViewHolder(View itemView) {
            super(itemView);
            textDiscountName = (TextView) itemView.findViewById(R.id.textDiscountName);
            textDiscount = (TextView) itemView.findViewById(R.id.textDiscount);
        }
    }
}
