package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结算管理Adapter
 * Created by Jocerly on 2017/3/10.
 */
public class SettlementManageDetialAdapter extends RecyclerView.Adapter<SettlementManageDetialAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    public SettlementManageDetialAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_settlement_detial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ConcurrentHashMap<String, String> entity = listData.get(position);
        viewHolder.imagePic.setImageResource("1".equals(entity.get("SettlementDetialType")) ? R.drawable.cash : R.drawable.jiekuan);
        viewHolder.textName.setText("1".equals(entity.get("SettlementDetialType")) ? "电子现金" : "结款");
        viewHolder.textDate.setText(entity.get("Date"));
        String temp = Common.formatTosepara(entity.get("SettlementDetialValue"), 3, 2);
        if (!StringUtils.isEmpty(temp)) {
            viewHolder.textValue.setText(temp);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textDate;
        private TextView textName;
        private TextView textValue;

        public ViewHolder(View convertView) {
            super(convertView);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textValue = (TextView) convertView.findViewById(R.id.textValue);
        }
    }
}
