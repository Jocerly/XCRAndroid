package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 入库记录Adapter
 * Created by Jocerly on 2017/5/23.
 */

public class AddToStockListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemclickLister onItemclickLister;
    private String key;

    public AddToStockListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            String temp = Common.formatTosepara(entity.get("GoodsCostPrice"), 3, 2);
            if (!StringUtils.isEmpty(temp)) {
                viewHolder.textGoodsPrice.setText(temp);
            }
            if (StringUtils.isEmpty(key)) {
                viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            } else {
                viewHolder.textGoodsName.setText(getSpannableString(key, entity.get("GoodsName")));
            }
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textGoodsUnit.setText(" 元/" + entity.get("UnitName"));
            viewHolder.textTime.setText(entity.get("Time"));
            viewHolder.textNum.setText(entity.get("Num"));
            viewHolder.textStockType.setText("0".equals(entity.get("Type")) ? "入库数量：" : "出库数量：");
        } else if (myViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemclickLister.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_to_stock_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textGoodsPrice;
        private TextView textGoodsUnit;
        private TextView textTime;
        private TextView textNum;
        private TextView textStockType;

        public ViewHolder(View convertView) {
            super(convertView);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textGoodsPrice = (TextView) convertView.findViewById(R.id.textGoodsPrice);
            textGoodsUnit = (TextView) convertView.findViewById(R.id.textGoodsUnit);
            textTime = (TextView) convertView.findViewById(R.id.textTime);
            textNum = (TextView) convertView.findViewById(R.id.textNum);
            textStockType = (TextView) convertView.findViewById(R.id.textStockType);
        }
    }

    public interface OnItemclickLister {
        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemclickLister(OnItemclickLister onItemclickLister) {
        this.onItemclickLister = onItemclickLister;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private SpannableString getSpannableString(String key, String str) {
        SpannableString s = new SpannableString(str);
        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
}
