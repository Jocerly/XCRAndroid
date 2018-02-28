package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息adapter
 * Created by Jocerly on 2017/3/13.
 */

public class StoreMsgAdapter extends RecyclerView.Adapter<StoreMsgAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private String defaultStoreNo;
    private OnStoreDefaultClistener OnStoreDefaultClistener;

    public StoreMsgAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ConcurrentHashMap<String, String> map = listData.get(position);
        final String storeSerialNo = map.get("StoreSerialNo");
        if (storeSerialNo.equals(defaultStoreNo)) {
            viewHolder.textDefualt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.select, 0, 0, 0);
        } else {
            viewHolder.textDefualt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.select_not, 0, 0, 0);
        }

        viewHolder.textStoreName.setText(StringUtils.isEmpty(map.get("StoreName")) ? "" : map.get("StoreName").toString());
        viewHolder.textStoreSerialNo.setText(StringUtils.isEmpty(map.get("StoreNo")) ? "" : map.get("StoreNo").toString());
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnStoreDefaultClistener.onItemNext(map);
            }
        });

        viewHolder.textDefualt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!storeSerialNo.equals(defaultStoreNo)) {
                    defaultStoreNo = storeSerialNo;
                    notifyDataSetChanged();
                }
                OnStoreDefaultClistener.onDefaultBack(defaultStoreNo, map);
            }
        });
    }

    /**
     * 初始化默认地址
     *
     * @param defaultStoreNo
     */
    public void setDefaultStoreNo(String defaultStoreNo) {
        this.defaultStoreNo = defaultStoreNo;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llItem;
        private TextView textStoreName;
        private TextView textStoreSerialNo;
        private TextView textDefualt;

        public ViewHolder(View convertView) {
            super(convertView);
            textStoreName = (TextView) convertView.findViewById(R.id.textStoreName);
            textStoreSerialNo = (TextView) convertView.findViewById(R.id.textStoreSerialNo);
            llItem = (LinearLayout) convertView.findViewById(R.id.llItem);
            textDefualt = (TextView) convertView.findViewById(R.id.textDefualt);
        }
    }

    public interface OnStoreDefaultClistener {
        public void onItemNext(ConcurrentHashMap<String, String> map);

        public void onDefaultBack(String defaultStoreNo, ConcurrentHashMap<String, String> map);
    }

    public void setOnStoreDefaultClistener(OnStoreDefaultClistener OnStoreDefaultClistener) {
        this.OnStoreDefaultClistener = OnStoreDefaultClistener;
    }
}
