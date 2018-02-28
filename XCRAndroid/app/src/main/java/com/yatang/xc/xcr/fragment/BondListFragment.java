package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.ContractSettledActivity;
import com.yatang.xc.xcr.adapter.BondListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保证金列表
 * Created by Jocerly on 2017/11/1.
 */
public class BondListFragment extends BaseFragment {
    @BindView(id = R.id.listViewBond)
    private ListView listViewBond;

    private BondListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;

    private NomalDialog nomalDialog;
    private String bondCode;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_bond_list, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
        listData = new ArrayList<>();
        adapter = new BondListAdapter(aty, listData);

        listViewBond.setAdapter(adapter);
        listViewBond.setOnItemClickListener(onItemClickListener);
        getBondList();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bondCode = listData.get(position).get("BondCode");
            nomalDialog.show(String.format(getResources().getString(R.string.bond_value), listData.get(position).get("BondValue")));
        }
    };
    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            doChoiceBond();
        }
    };

    /**
     * 保证金列表
     */
    private void getBondList() {
        params.clear();
        params.put("MarketNo", ((ContractSettledActivity) getActivity()).getMarketNo());
        httpRequestService.doRequestData(aty, "System/BondList", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    listData.clear();
                    listData.addAll(resultParam.listData);

                    adapter.notifyDataSetChanged();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 选择保证金
     */
    private void doChoiceBond() {
        params.clear();
        params.put("MarketNo", ((ContractSettledActivity) getActivity()).getMarketNo());
        params.put("BondCode", bondCode);
        httpRequestService.doRequestData(aty, "System/ChoiceBond", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    ((ContractSettledActivity) getActivity()).setContractUrl(resultParam.mapData.get("ContractUrl"));
                    ((ContractSettledActivity) getActivity()).setStep(2);
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    ((ContractSettledActivity) getActivity()).showTinmeOutDialog();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }
}
