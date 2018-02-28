package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫码确认页adapter
 * Created by Jocerly on 2017/3/13.
 */

public class ScanCodeConfirmAdapter extends RecyclerView.Adapter<ScanCodeConfirmAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public ScanCodeConfirmAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_code_confirm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ConcurrentHashMap<String, String> entity = listData.get(position);
        if (StringUtils.isEmpty(entity.get("GoodsId"))) {//不存在此商品
            viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textGoodsPrice.setText("新增商品");
            viewHolder.textUnitName.setText("");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick("", entity.get("GoodsCode"), entity.get("GoodsName"),"");
                }
            });
        } else {
            viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textGoodsPrice.setText(entity.get("NewGoodsPrice"));
            viewHolder.textUnitName.setText("元/"+entity.get("UnitName"));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("GoodsId"), entity.get("GoodsCode"), entity.get("GoodsName"),viewHolder.textGoodsPrice.getText().toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textGoodsPrice;
        private TextView textUnitName;

        public ViewHolder(View convertView) {
            super(convertView);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textGoodsPrice = (TextView) convertView.findViewById(R.id.textGoodsPrice);
            textUnitName = (TextView) convertView.findViewById(R.id.textUnitName);
        }
    }

    public interface OnItemClickListener {
        public void itemClick(String goodsId, String goodsCode, String goodsName,String price);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
