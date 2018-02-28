package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 我的特权列表数据适配器
 * Created by dengjiang on 2017/11/8.
 */

public class MPrivilegeListyAdapter extends RecyclerView.Adapter<MPrivilegeListyAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;

    public MPrivilegeListyAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_privilegelist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textPrivilegeName.setText(map.get("PrivilegeName"));
        holder.textPrivilegeInfo.setText(map.get("PrivilegeInfo"));
        int id;
        switch (map.get("PrivilegeType")) {
            case "1":
                id = R.drawable.pri_returncash;
                break;
            default:
                id = R.drawable.pri_returncash;
                break;
        }
        holder.imagePic.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textPrivilegeName;
        private TextView textPrivilegeInfo;

        public ViewHolder(View convertView) {
            super(convertView);
            textPrivilegeName = (TextView) convertView.findViewById(R.id.textPrivilegeName);
            textPrivilegeInfo = (TextView) convertView.findViewById(R.id.textPrivilegeInfo);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
        }
    }
}
