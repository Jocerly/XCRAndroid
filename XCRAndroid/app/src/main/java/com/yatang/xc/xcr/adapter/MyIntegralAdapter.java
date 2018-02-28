package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 我的特权数据适配器
 * Created by dengjiang on 2017/11/6.
 */

public class MyIntegralAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MyIntegralAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_integral, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder baseViewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
//            Picasso.with(context)
//                    .load(StringUtils.isEmpty(entity.get("Pic")) ? "0" : entity.get("Pic"))
//                    .resizeDimen(R.dimen.pad32, R.dimen.pad32)
//                    .centerCrop()
//                    .error(R.drawable.task_other)
//                    .placeholder(R.drawable.task_other)
//                    .into(baseViewHolder.imagePic);
            baseViewHolder.textName.setText(entity.get("IntegralName"));
            baseViewHolder.textDate.setText(entity.get("Date"));
            baseViewHolder.textIntegralValue.setText("+" + entity.get("IntegralValue"));
        } else if (myViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) myViewHolder;

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

    class ViewHolder extends BaseViewHolder {
        //        private ImageView imagePic;
        private TextView textName;
        private TextView textDate;
        private TextView textIntegralValue;

        public ViewHolder(View convertView) {
            super(convertView);
            textName = (TextView) convertView.findViewById(R.id.textName);
//            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textIntegralValue = (TextView) convertView.findViewById(R.id.textIntegralValue);
        }
    }

    public interface OnItemClickListener {
        public void itemClick(String url, String title);

        public void OnFooterClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}