package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调价记录
 * Created by Jocerly on 2017/3/22.
 */

public class ScanCodeAdapter extends RecyclerView.Adapter<ScanCodeAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public ScanCodeAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_code, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final ConcurrentHashMap<String, String> entity =listData.get(position);
        if(StringUtils.isEmpty(entity.get("GoodsId"))) {
            viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textGoodsPrice.setText(entity.get(""));
            viewHolder.textUnitName.setText(entity.get(""));
           viewHolder.textName.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textGoodsPrice.setText(entity.get("NewGoodsPrice"));
            viewHolder.textUnitName.setText("元/"+entity.get("UnitName"));
            viewHolder.textName.setVisibility(View.GONE);
        }
        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.delete(position, entity.get("GoodsCode"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private  TextView textGoodsName;
        private ImageView imageDelete;
        private TextView textGoodsCode;
        private TextView textGoodsPrice;
        private TextView textUnitName;

        public ViewHolder(View convertView) {
            super(convertView);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textGoodsPrice = (TextView) convertView.findViewById(R.id.textGoodsPrice);
            textUnitName = (TextView) convertView.findViewById(R.id.textUnitName);
            imageDelete = (ImageView) convertView.findViewById(R.id.imageDelete);
            textGoodsName= (TextView) convertView.findViewById(R.id.textGoodsName);
        }
    }

    public interface OnItemClickListener {
        public void delete(int position, String code);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
