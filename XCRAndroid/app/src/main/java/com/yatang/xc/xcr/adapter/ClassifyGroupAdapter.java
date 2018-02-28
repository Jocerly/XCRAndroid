package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类一级Adapter
 * Created by Jocerly on 2017/5/25.
 */

public class ClassifyGroupAdapter extends RecyclerView.Adapter<ClassifyGroupAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, Object>> listGroupData = new ArrayList<>();
    private OnClassifyGroupClistener onClassifyGroupClistener;

    public ClassifyGroupAdapter(Context context, List<ConcurrentHashMap<String, Object>> listGroupData) {
        this.context = context;
        this.listGroupData = listGroupData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_classify, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ConcurrentHashMap<String, Object> map = listGroupData.get(position);
        viewHolder.textClassifyName.setText(map.get("ClassifyFirstName").toString());
        viewHolder.setClassifyFirstId(map.get("ClassifyFirstId").toString());
        viewHolder.setListData((List<ConcurrentHashMap<String, String>>) map.get("SecondList"));
        JCLoger.debug(map.get("SecondList").toString());
    }

    @Override
    public int getItemCount() {
        return listGroupData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textClassifyName;
        private RecyclerView mRecyclerView;
        private ClassifyAdapter adapter;
        private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

        public ViewHolder(View convertView) {
            super(convertView);
            textClassifyName = (TextView) convertView.findViewById(R.id.textClassifyName);
            mRecyclerView = (RecyclerView) convertView.findViewById(R.id.mRecyclerView);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST,
                    (int) context.getResources().getDimension(R.dimen.pad15), context.getResources().getColor(R.color.white)));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST,
                    (int) context.getResources().getDimension(R.dimen.pad5), context.getResources().getColor(R.color.white)));
            adapter = new ClassifyAdapter(context, listData);
            adapter.setOnClassifyClistener(new ClassifyAdapter.OnClassifyClistener() {
                @Override
                public void onClassifyItem(String classifyId, String classifyName) {
                    onClassifyGroupClistener.onClassifyGroupItem(classifyId, classifyName);
                }
            });
            mRecyclerView.setAdapter(adapter);
        }

        public void setListData(List<ConcurrentHashMap<String,String>> listData) {
            this.listData.clear();
            this.listData.addAll(listData);
            adapter.notifyDataSetChanged();
        }

        public void setClassifyFirstId(String classifyFirstId) {
            adapter.setClassifyFirstId(classifyFirstId);
        }
    }

    public interface OnClassifyGroupClistener {
        public void onClassifyGroupItem(String classifyId, String classifyName);
    }

    public void setOnClassifyGroupClistener(OnClassifyGroupClistener onClassifyGroupClistener) {
        this.onClassifyGroupClistener = onClassifyGroupClistener;
    }
}
