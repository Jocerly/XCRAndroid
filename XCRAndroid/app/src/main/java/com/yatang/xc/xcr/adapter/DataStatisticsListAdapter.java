package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计列表适配器
 * Created by dengjiang on 2017/7/4.
 */

public class DataStatisticsListAdapter extends BaseAdapter {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private int type; //1表示销量排行,2标识利润排行
    private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局

    public DataStatisticsListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData, int type) {
        this.context = context;
        this.listData = listData;
        this.type = type;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_statistics_list, null);
            holder = new ViewHolder();
            holder.textNum = (TextView) convertView.findViewById(R.id.textNum);
            holder.textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            holder.textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            holder.textValue = (TextView) convertView.findViewById(R.id.textValue);
            holder.viewLine = convertView.findViewById(R.id.viewLine);
            convertView.setTag(holder); //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
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
        if (position == listData.size() - 1) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
        holder.textNum.setText(position + 1 + "");
        holder.textGoodsName.setText(map.get("GoodsName"));
        holder.textGoodsCode.setText(map.get("GoodsCode"));
        if (type == 1) {
            holder.textValue.setText(map.get("GoodsSaleVaule")+" "+map.get("GoodsUnit"));
        } else if (type == 2) {
            String temp = Common.formatTosepara(map.get("GoodsProfitsVaule"),3,2);
            if(!StringUtils.isEmpty(temp)) {
                temp = "￥"+temp;
            }
            holder.textValue.setText(temp);
        }
        return convertView;
    }

    /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        private TextView textNum;
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textValue;
        private View viewLine;
    }

    /**
     * 设置数据list
     */
    public void setListData(List<ConcurrentHashMap<String, String>> listData) {
        this.listData = listData;
    }
}
