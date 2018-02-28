package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调价记录Adapter
 * Created by Jocerly on 2017/3/13.
 */

public class ScanCodeListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemClickLinster onItemClickLinster;

    public ScanCodeListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_addgoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, final int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            ConcurrentHashMap<String, String> map = listData.get(position);
            viewHolder.textGoodsName.setText(map.get("GoodsName"));
            viewHolder.textGoodsPrice.setText(map.get("GoodsPrice"));
            viewHolder.textUnitName.setText("元/"+map.get("UnitName"));
            viewHolder.textGoodsStatue.setText(map.get("Date"));
            viewHolder.textGoodsCode.setText(map.get("GoodsCode"));
        } else if (myViewHolder instanceof FooterHolder){
            final FooterHolder footerHolder = (FooterHolder)myViewHolder;

            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE){
                        onItemClickLinster.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    public interface OnItemClickLinster {
        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textGoodsName;
        private TextView textGoodsPrice;
        private TextView textUnitName;
        private TextView textGoodsStatue;
        private TextView textGoodsCode;

        public ViewHolder(View convertView) {
            super(convertView);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsPrice = (TextView) convertView.findViewById(R.id.textGoodsPrice);
            textUnitName = (TextView) convertView.findViewById(R.id.textUnitName);
            textGoodsStatue = (TextView) convertView.findViewById(R.id.textGoodsStatue);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
        }
    }
}