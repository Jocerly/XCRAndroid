package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保证金列表Adapter
 * Created by Jocerly on 2017/11/1.
 */
public class BondListAdapter extends BaseAdapter {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private OnBondListlistener onBondListlistener;

    public BondListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setOnBondListlistener(OnBondListlistener onBondListlistener) {
        this.onBondListlistener = onBondListlistener;
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
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bond_list, null);
            holder.textBondName = (TextView) convertView.findViewById(R.id.textBondName);
            holder.textBondInfo = (TextView) convertView.findViewById(R.id.textBondInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textBondName.setText(map.get("BondName"));
        holder.textBondInfo.setText(map.get("BondInfo"));
        return convertView;
    }

    class ViewHolder {
        private TextView textBondName;
        private TextView textBondInfo;
    }

    public interface OnBondListlistener {
        void onBondList(int position);
    }
}
