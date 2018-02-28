package com.yatang.xc.xcr.adapter;

import android.content.Context;
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

/**
 * 统计排行榜 详情 数据适配器
 * Created by dengjiang on 2017/7/3.
 */

public class DataStatisticsDetailsAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private String type; //1表示销量排行,2标识利润排行
    private OnItemClickListener onItemClickListener;

    /**
     * @param context
     * @param list    the datas to attach the adapter
     */
    public DataStatisticsDetailsAdapter(Context context, List<ConcurrentHashMap<String, String>> list) {
        super(context, list);
        this.context = context;
    }

    /**
     * @param context
     * @param list    the datas to attach the adapter
     * @param type    1: 商品销量排行 2:商品利润排行
     */
    public DataStatisticsDetailsAdapter(Context context, List<ConcurrentHashMap<String, String>> list, String type) {
        super(context, list);
        this.context = context;
        this.type = type;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) baseViewHolder;
            ConcurrentHashMap<String, String> map = listData.get(position);
            holder.textNum.setText(position + 1 + "");
            holder.textGoodsName.setText(map.get("GoodsName"));
            holder.textGoodsCode.setText(map.get("GoodsCode"));
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
            holder.textValue.setText(map.get("GoodsVaule"));
            if ("1".equals(type)) {
                holder.textValue.setText(map.get("GoodsVaule")+" "+map.get("GoodsUnit"));
            } else if ("2".equals(type)) {
                String temp = Common.formatTosepara(map.get("GoodsVaule"),3,2);
                if(!StringUtils.isEmpty(temp)) {
                    temp = "￥"+temp;
                }
                holder.textValue.setText(temp);
            }
            if (position == listData.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            } else {
                holder.viewLine.setVisibility(View.VISIBLE);
            }
        } else if (baseViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) baseViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemClickListener.OnFooterClick();
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_statistics_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textNum;
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textValue;
        private View viewLine;

        public ViewHolder(View itemView) {
            super(itemView);
            textNum = (TextView) itemView.findViewById(R.id.textNum);
            textGoodsName = (TextView) itemView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) itemView.findViewById(R.id.textGoodsCode);
            textValue = (TextView) itemView.findViewById(R.id.textValue);
            viewLine = itemView.findViewById(R.id.viewLine);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void OnFooterClick();
    }
}
