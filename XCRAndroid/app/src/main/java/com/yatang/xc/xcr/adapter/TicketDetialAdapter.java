package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小票详情Adapter
 * Created by Jocerly on 2017/8/11.
 */

public class TicketDetialAdapter extends BaseAdapter {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private ScanCodeConfirmAdapter.OnItemClickListener onItemClickListener;

    public TicketDetialAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ticket_detial, null);
            holder = new ViewHolder();
            holder.textGoodname = (TextView) convertView.findViewById(R.id.textGoodname);
            holder.textGoodNum = (TextView) convertView.findViewById(R.id.textGoodNum);
            holder.textGoodUnitValue = (TextView) convertView.findViewById(R.id.textGoodUnitValue);
            holder.textGoodAllValue = (TextView) convertView.findViewById(R.id.textGoodAllValue);
            convertView.setTag(holder); //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
        final ConcurrentHashMap<String, String> entity = listData.get(position);
        holder.textGoodname.setText(entity.get("GoodName"));
        holder.textGoodNum.setText("x" + entity.get("GoodNum"));
       holder.textGoodUnitValue.setText(Common.formatTosepara(entity.get("GoodUnitValue"), 3, 2));
        holder.textGoodAllValue.setText(Common.formatTosepara(entity.get("GoodAllValue"), 3, 2));
        return convertView;
    }

    class ViewHolder {
        TextView textGoodname;
        TextView textGoodNum;
        TextView textGoodUnitValue;
        TextView textGoodAllValue;
    }
}
