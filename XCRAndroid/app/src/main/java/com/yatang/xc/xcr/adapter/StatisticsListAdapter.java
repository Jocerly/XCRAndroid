package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计列表适配器
 * Created by dengjiang on 2017/7/1.
 */

public class StatisticsListAdapter extends RecyclerView.Adapter<StatisticsListAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private int type; //1表示销量排行,2标识利润排行

    public StatisticsListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData, int type) {
        this.context = context;
        this.listData = listData;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_statistics_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        TextPaint tp = holder.textNum.getPaint();
        tp.setFakeBoldText(true);
        switch (position) {
            case 0:
                holder.textNum.setBackground(context.getResources().getDrawable(R.drawable.no1));
                break;
            case 1:
                holder.textNum.setBackground(context.getResources().getDrawable(R.drawable.no2));
                break;
            case 2:
                holder.textNum.setBackground(context.getResources().getDrawable(R.drawable.no3));
                break;
            default:
                holder.textNum.setBackground(context.getResources().getDrawable(R.drawable.no4));
                break;
        }
        if(position == listData.size()-1) {
            holder.viewLine.setVisibility(View.GONE);
        }else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
        holder.textNum.setText(position + 1 + "");
        holder.textGoodsName.setText(map.get("GoodsName"));
        holder.textGoodsCode.setText(map.get("GoodsCode"));
        if (type == 1) {
            holder.textValue.setText(map.get("GoodsSaleVaule"));
        } else if (type == 2) {
            holder.textValue.setText(map.get("GoodsProfitsVaule"));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textNum;
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textValue;
        private View viewLine;


        public ViewHolder(View convertView) {
            super(convertView);
            textNum = (TextView) convertView.findViewById(R.id.textNum);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
            viewLine = convertView.findViewById(R.id.viewLine);
        }
    }

    /**
     * 设置数据list
     */
    public void setListData(List<ConcurrentHashMap<String, String>> listData) {
        this.listData = listData;
    }
}
