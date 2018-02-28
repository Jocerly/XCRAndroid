package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.AddCoupAdapter;
import com.yatang.xc.xcr.adapter.CouponStatisticAdapter;
import com.yatang.xc.xcr.uitls.ResultParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 优惠券详情统计页面
 * Created by dengjiang on 2017/10/20.
 */

public class CouponStatisticsFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private TextView textCustomNumber;
    private TextView textCoupNumber;
    private ProgressBar proCoupNumber;
    private ProgressBar proCustomNumber;
    private ResultParam resultParam;
    private List<ConcurrentHashMap<String, String>> listData;
    private CouponStatisticAdapter adapter;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_couponstatistics, null);
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
        textCustomNumber = (TextView) view.findViewById(R.id.textCustomNumber);
        textCoupNumber = (TextView) view.findViewById(R.id.textCoupNumber);
        proCoupNumber = (ProgressBar) view.findViewById(R.id.proCoupNumber);
        proCustomNumber = (ProgressBar) view.findViewById(R.id.proCustomNumber);
        listData = new ArrayList<>();
        adapter = new CouponStatisticAdapter(getActivity(), listData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setData(ResultParam resultParam) {
        if (resultParam != null) {
            float reCount = Float.parseFloat(resultParam.mapData.get("ReceivedCount"));
            float totalCount = Float.parseFloat(resultParam.mapData.get("TotalCount"));
            textCoupNumber.setText((int) reCount + "/" + (int) totalCount);
            int progress = (int) (reCount / totalCount * 100);
            JCLoger.debug("proCoupNumber=" + progress);
            proCoupNumber.setProgress(progress);

            float usedCount = Float.parseFloat(resultParam.mapData.get("UsedCouponCount"));
            float receivedCount = Float.parseFloat(resultParam.mapData.get("ReceivedCouponCount"));
            textCustomNumber.setText((int) usedCount + "/" + (int) receivedCount);
            progress = (int) (usedCount / receivedCount * 100);
            JCLoger.debug("proCoupNumber=" + progress);
            proCustomNumber.setProgress(progress);
            if (resultParam.listData != null) {
                listData.addAll(resultParam.listData);
            } else {
                listData.clear();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
