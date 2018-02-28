package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.yatang.xc.xcr.R;

import java.util.List;


/**
 * 百度地图定位
 * Created by lusha on 2017/06/13.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private Context context;
    private OnItemClickLinster onItemClickLinster;
    private List<PoiInfo> poiInfos;
    private int selectPosition;
    private boolean isFirstLoc = false;


    public LocationAdapter(Context context, List<PoiInfo> poiInfos) {
        this.context = context;
        this.poiInfos = poiInfos;
    }

    public void setFirstLoc(boolean firstLoc) {
        isFirstLoc = firstLoc;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (isFirstLoc && position == 0) {
            holder.textName.setText(poiInfos.get(position).address);
            holder.textAddress.setText("");
        } else {
            holder.textName.setText(poiInfos.get(position).name);
            holder.textAddress.setText(poiInfos.get(position).address);
        }
        if (position == selectPosition) {
            holder.imageSelect.setVisibility(View.VISIBLE);
        } else {
            holder.imageSelect.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new MyItemClickListenner(position));
    }

    private class MyItemClickListenner implements View.OnClickListener {
        private int position;

        public MyItemClickListenner(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            onItemClickLinster.itemClickLinster(poiInfos.get(position).address, poiInfos.get(position).location);
            selectPosition = position;
            notifyDataSetChanged();
        }
    }

    public void setSelection(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public int getItemCount() {
        return poiInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textAddress;
        private ImageView imageSelect;

        public ViewHolder(View convertView) {
            super(convertView);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textAddress = (TextView) convertView.findViewById(R.id.textAddress);
            imageSelect = (ImageView) convertView.findViewById(R.id.imageSelect);
        }
    }

    public interface OnItemClickLinster {
        public void itemClickLinster(String address, LatLng location);
    }

    public void setOnItemClickLinster(OnItemClickLinster onItemClickLinster) {
        this.onItemClickLinster = onItemClickLinster;
    }

}

