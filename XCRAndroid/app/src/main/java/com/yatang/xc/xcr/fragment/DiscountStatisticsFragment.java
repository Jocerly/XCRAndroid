package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.DiscountStatisticAdapter;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/23.
 */

public class DiscountStatisticsFragment extends BaseFragment {
    private TextView textSalesNumber;
    private TextView textSalesMoney;
    private TextView textDiscount;
    private RecyclerView recyclerView;
    private List<ConcurrentHashMap<String, String>> listData;
    private DiscountStatisticAdapter adapter;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_discountstatistics, null);
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
        textSalesNumber = (TextView) view.findViewById(R.id.textSalesNumber);
        textSalesMoney = (TextView) view.findViewById(R.id.textSalesMoney);
        textDiscount = (TextView) view.findViewById(R.id.textDiscount);
        listData = new ArrayList<>();
        adapter = new DiscountStatisticAdapter(getActivity(), listData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setData(ResultParam resultParam) {
        if (resultParam != null) {
            textSalesNumber.setText(resultParam.mapData.get("TotalSalesNum"));
            textSalesMoney.setText(Common.formatFloat(resultParam.mapData.get("SalesBalance")));
            textDiscount.setText(Common.formatFloat(resultParam.mapData.get("PromotionBalance")));
            if (resultParam.listData != null) {
                listData.addAll(resultParam.listData);
            } else {
                listData.clear();
            }
            adapter.setSalesNum(resultParam.mapData.get("TotalSalesNum"));
            adapter.notifyDataSetChanged();
        }
    }
}
