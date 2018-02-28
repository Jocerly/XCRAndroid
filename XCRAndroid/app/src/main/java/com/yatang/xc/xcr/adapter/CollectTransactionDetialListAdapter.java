package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;
import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**日收钱码流水Adapter
 * Created by lusha on 2017/10/30.
 */

public class CollectTransactionDetialListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private OnItemClickLinster onItemClickLinster;
    private Context context;


    public CollectTransactionDetialListAdapter(Context context, List<ConcurrentHashMap<String, String>> list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            viewHolder.imgTicketType.setImageResource("1".equals(entity.get("PayType")) ? R.drawable.ticket_weixin : R.drawable.ticket_zhifubao);
            viewHolder.textTip.setVisibility(View.GONE);
            viewHolder.textName.setText(entity.get("Time"));
            viewHolder.textElectronic.setVisibility(View.GONE);
            viewHolder.textValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (!StringUtils.isEmpty(entity.get("CollectTransactionDaily"))) {
                viewHolder.textValue.setText("￥" + Common.formatTosepara(entity.get("CollectTransactionDaily"), 3, 2));
            }

        } else if (holder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) holder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }

    class ViewHolder extends BaseViewHolder {
        private ImageView imgTicketType;
        private TextView textName;
        private TextView textTip;
        private TextView textElectronic;
        private TextView textValue;

        public ViewHolder(View itemView) {
            super(itemView);
            imgTicketType = (ImageView) itemView.findViewById(R.id.imgTicketType);
            textName = (TextView) itemView.findViewById(R.id.textName);
            textTip = (TextView) itemView.findViewById(R.id.textTip);
            textElectronic = (TextView) itemView.findViewById(R.id.textElectronic);
            textValue = (TextView) itemView.findViewById(R.id.textValue);
        }
    }

    public interface OnItemClickLinster{
        /**
         * 点击底部区域
         */
         public void OnFooterClick();
    }

}