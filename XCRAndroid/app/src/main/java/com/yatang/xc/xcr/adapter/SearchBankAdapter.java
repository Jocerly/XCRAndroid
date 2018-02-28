package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.SearchBankActivity;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/8/4.
 */

public class SearchBankAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private String type;

    /**
     * @param context
     * @param listData the datas to attach the adapter
     */
    public SearchBankAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) baseViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            if (SearchBankActivity.SEARCH_BANK.equals(type)) {
                //搜索银行
                viewHolder.text_bank.setText(entity.get("BankCardName"));
            } else if (SearchBankActivity.SEARCH_BRANCH.equals(type)) {
                //搜索支行
                viewHolder.text_bank.setText(entity.get("BankCardBranch"));
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SearchBankActivity.SEARCH_BANK.equals(type)) {
                        //搜索银行
                        onItemClickListener.itemClick(entity.get("BankCardName"), entity.get("BankCardId"));
                    } else if (SearchBankActivity.SEARCH_BRANCH.equals(type)) {
                        //搜索支行
                        onItemClickListener.itemClick(entity.get("BankCardBranch"), entity.get("BankCardBranchId"));
                    }

                }
            });
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_banklist, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView text_bank;

        public ViewHolder(View itemView) {
            super(itemView);
            text_bank = (TextView) itemView.findViewById(R.id.text_bank);
        }
    }

    public interface OnItemClickListener {
        public void itemClick(String bankName, String bankId);

        public void OnFooterClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
