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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/20.
 */

public class CouponStatisticAdapter extends RecyclerView.Adapter<CouponStatisticAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;

    public CouponStatisticAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }


    @Override
    public CouponStatisticAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_couponstatistic, parent, false);
        return new CouponStatisticAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CouponStatisticAdapter.ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textCoupDetails.setText(map.get("CouponBalance") + "元券(满" + map.get("UseCondition") + "元可用)");
        float useCount = Float.parseFloat(map.get("UsedCount"));
        float receivedCount = Float.parseFloat(map.get("ReceivedCount"));
        holder.textCoupNumber.setText((int) useCount + "/" + (int) receivedCount);
        int progress = (int) (useCount / receivedCount * 100);
        JCLoger.debug("proCoupNumber=" + progress);
        holder.proCoupNumber.setProgress(progress);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends BaseViewHolder {

        private TextView textCoupDetails;
        private TextView textCoupNumber;
        private ProgressBar proCoupNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            textCoupDetails = (TextView) itemView.findViewById(R.id.textCoupDetails);
            textCoupNumber = (TextView) itemView.findViewById(R.id.textCoupNumber);
            proCoupNumber = (ProgressBar) itemView.findViewById(R.id.proCoupNumber);
        }
    }
}