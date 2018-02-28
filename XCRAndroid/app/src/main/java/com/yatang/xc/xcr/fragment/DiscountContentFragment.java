package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.DiscountCotentAdapter;
import com.yatang.xc.xcr.uitls.ResultParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/23.
 */

public class DiscountContentFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private ResultParam resultParam;
    private TextView eventName;
    private TextView eventTime;
    private DiscountCotentAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_discountcontent, null);
        initView(view);
        return view;
    }

    /**
     * 初始化
     *
     * @param view
     */
    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        eventName = (TextView) view.findViewById(R.id.eventName);
        eventTime = (TextView) view.findViewById(R.id.eventTime);
        listData = new ArrayList<>();
        adapter = new DiscountCotentAdapter(getActivity(), listData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setData(ResultParam resultParam) {
        if (resultParam != null) {
            eventName.setText(resultParam.mapData.get("EventName"));
            eventTime.setText("活动时间:" + resultParam.mapData.get("StartDate") + "-" + resultParam.mapData.get("EndDate"));

            if (resultParam.listData != null) {
                listData.addAll(resultParam.listData);
            } else {
                listData.clear();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
