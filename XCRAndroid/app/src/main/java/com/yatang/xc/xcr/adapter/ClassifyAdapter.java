package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.DensityUtils;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类三级Adapter
 * Created by Jocerly on 2017/5/25.
 */

public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ViewHolder> {
    private final int width;
    private LinearLayout.LayoutParams layoutParams;
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnClassifyClistener onClassifyClistener;
    private String classifyFirstId;

    public ClassifyAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
        width = (DensityUtils.getScreenW(context) - context.getResources().getDimensionPixelSize(R.dimen.pad30));
        layoutParams = new LinearLayout.LayoutParams(width/4, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void setClassifyFirstId(String classifyFirstId) {
        this.classifyFirstId = classifyFirstId;
    }

    public void setListData(List<ConcurrentHashMap<String, String>> listData) {
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_classify, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final ConcurrentHashMap<String, String> map = listData.get(position);
        viewHolder.textClassifyName.setText(listData.get(position).get("ClassifyName"));
        Picasso.with(context)
                .load(StringUtils.isEmpty(map.get("ClassifyPic")) ? "0" : map.get("ClassifyPic"))
                .resizeDimen(R.dimen.pad53, R.dimen.pad53)
                .centerCrop()
                .error(R.drawable.defualt_classify)
                .placeholder(R.drawable.defualt_classify)
                .into(viewHolder.imageClassifyPic);
        viewHolder.llClassify.setLayoutParams(layoutParams);
        viewHolder.llClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClassifyClistener.onClassifyItem(map.get("ClassifyId"),  map.get("ClassifyName"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textClassifyName;
        private ImageView imageClassifyPic;
        private LinearLayout llClassify;

        public ViewHolder(View convertView) {
            super(convertView);
            textClassifyName = (TextView) convertView.findViewById(R.id.textClassifyName);
            imageClassifyPic = (ImageView) convertView.findViewById(R.id.imageClassifyPic);
            llClassify = (LinearLayout) convertView.findViewById(R.id.llClassify);
        }
    }

    public interface OnClassifyClistener {
        public void onClassifyItem(String classifyId, String classifyName);
    }

    public void setOnClassifyClistener(OnClassifyClistener onClassifyClistener) {
        this.onClassifyClistener = onClassifyClistener;
    }
}
