package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/23.
 */

public class DiscountStatisticAdapter extends RecyclerView.Adapter<DiscountStatisticAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private String salesNum;

    public DiscountStatisticAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setSalesNum(String salesNum) {
        this.salesNum = salesNum;
    }

    @Override
    public DiscountStatisticAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discountstatistics, parent, false);
        return new DiscountStatisticAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiscountStatisticAdapter.ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textGoodName.setText(map.get("GoodsName"));
        holder.textNumber.setText(map.get("UsedCount"));
        JCLoger.debug("GoodsName=" + map.get("GoodsName") + "  goodsSalesNum=" + map.get("GoodsSalesNum"));
        float goodsSalesNum = Float.parseFloat(StringUtils.isEmpty(map.get("GoodsSalesNum")) ? "0" : map.get("GoodsSalesNum"));
        float SalesNum = Float.parseFloat(salesNum);
        holder.textNumber.setText((int) goodsSalesNum + "/" + (int) SalesNum);
        int progress = (int) (goodsSalesNum / SalesNum * 100);
        holder.progressBar.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        JCLoger.debug("size=" + listData.size());
        return listData.size();
    }

    public class ViewHolder extends BaseViewHolder {

        private TextView textGoodName;
        private TextView textNumber;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            textGoodName = (TextView) itemView.findViewById(R.id.textGoodName);
            textNumber = (TextView) itemView.findViewById(R.id.textNumber);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}