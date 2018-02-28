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

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 库存列表Adapter
 * Created by Jocerly on 2017/5/22.
 */

public class StockListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemclickLister onItemclickLister;
    private String key;

    public StockListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            if (StringUtils.isEmpty(key)) {
                viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            } else {
                viewHolder.textGoodsName.setText(getSpannableString(key, entity.get("GoodsName")));
            }
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            viewHolder.textNum.setText(entity.get("GoodsStock") +entity.get("UnitName"));
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_stock_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textNum;

        public ViewHolder(View convertView) {
            super(convertView);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textNum = (TextView) convertView.findViewById(R.id.textNum);
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
