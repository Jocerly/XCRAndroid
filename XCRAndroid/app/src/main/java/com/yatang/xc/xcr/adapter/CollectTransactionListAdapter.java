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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**收钱码流水Adapter
 * Created by lusha on 2017/10/11.
 */

public class CollectTransactionListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");
    private Context context;
    private OnItemClickLinster onItemClickLinster;

    public CollectTransactionListAdapter(Context context, List<ConcurrentHashMap<String, String>> list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            if (!StringUtils.isEmpty(entity.get("Time"))) {
                String data = Common.stampToDate(simpleDateFormat, entity.get("Time"));
                viewHolder.textData.setText(Common.isToday(data) ? "今日" : data);
                viewHolder.textWeekDay.setText(Common.getWeekBDayyDateStr(data));
            }
            String tmp = entity.get("CollectTransactionDaily");
            if (!StringUtils.isEmpty(tmp)) {
                viewHolder.textCollectTransactionDaily.setText("￥" + Common.formatTosepara(tmp, 3, 2));
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLinster.OnItemClick(entity);
                }
            });
        } else if (holder instanceof FooterHolder){
            final FooterHolder footerHolder = (FooterHolder) holder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE){
                        onItemClickLinster.OnFooterClick();
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_transaction_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder{
        private TextView textWeekDay;
        private TextView textData;
        private TextView textCollectTransactionDaily;

        public ViewHolder(View itemView) {
            super(itemView);
            textWeekDay = (TextView) itemView.findViewById(R.id.textWeekDay);
            textData = (TextView) itemView.findViewById(R.id.textData);
            textCollectTransactionDaily = (TextView) itemView.findViewById(R.id.textCollectTransactionDaily);
        }
    }
    public interface OnItemClickLinster{
        public void OnItemClick(ConcurrentHashMap<String, String> map);
        /**
         * 点击底部区域
         */
        public void OnFooterClick();
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }
}
