package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索Adapter
 * Created by Jocerly on 2017/5/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if(position == listData.size()) {
            //最后一项，清空搜索历史
            viewHolder.textMsg.setText("清空搜索历史");
            viewHolder.imagePic.setImageResource(R.drawable.delete_history);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.cleanHistory();
                }
            });
        } else {
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            viewHolder.textMsg.setText(entity.get("SearchMsg"));
            viewHolder.imagePic.setImageResource(R.drawable.history_record);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("SearchMsg"));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count = listData.size()>0?listData.size()+1:0;
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMsg;
        private ImageView imagePic;

        public ViewHolder(View convertView) {
            super(convertView);
            textMsg = (TextView) convertView.findViewById(R.id.textMsg);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
        }
    }

    public interface OnItemClickListener {
        /**
         * 选择每项
         * @param msg
         */
        public void itemClick(String msg);

        /**
         * 清空历史
         */
        public void cleanHistory();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
