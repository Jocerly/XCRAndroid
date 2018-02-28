package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lusha on 2017/06/07.小票支付方式列表
 */

public class PayTypeAdapter extends RecyclerView.Adapter<PayTypeAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    public PayTypeAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_paytype,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ConcurrentHashMap<String, String> entity = listData.get(position);
       getPayment(viewHolder.textPayType,entity.get("PayType"));
        viewHolder.textEqualMny.setText("￥" + Common.formatTosepara(entity.get("EqualMny"),3,2));

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    /**
     * 支付方式
     * @param paymentType 1：现金、3：电子券、2203：微信、2210：支付宝
     * @return
     */
    private void getPayment(TextView textStatue ,String paymentType) {
        switch (Integer.parseInt(paymentType)) {
            case 1:
                textStatue.setText("现金");
                break;
            case 3:
                textStatue.setText("电子券");
                break;
            case 2203:
                textStatue.setText("微信");
                break;
            case 2210:
                textStatue.setText("支付宝");
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView textPayType;
        private  TextView textEqualMny;

        public ViewHolder(View convertView) {
            super(convertView);
            textPayType = (TextView) convertView.findViewById(R.id.textPayType);
            textEqualMny = (TextView) convertView.findViewById(R.id.textEqualMny);
        }
    }
}
