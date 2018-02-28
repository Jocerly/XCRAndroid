package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/6/13.
 */

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    public OrderDetailsAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderdetails, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConcurrentHashMap<String, String> map = listData.get(position);
        String url = StringUtils.isEmpty(map.get("PicUrl")) ? "0" : map.get("PicUrl");
        Picasso.with(context)
                .load(url)
                .resizeDimen(R.dimen.pad50, R.dimen.pad50)
                .centerCrop()
                .error(R.drawable.orderdefault)
                .placeholder(R.drawable.orderdefault)
                .into(holder.imagePic);
        holder.textGoodName.setText(map.get("GoodName"));
        holder.textUnitPrice.setText("￥" + Common.formatFloat(map.get("GoodUnitValue")));
        holder.textCount.setText("x" + map.get("GoodNum"));
        holder.textPrice.setText("￥" + Common.formatFloat(map.get("GoodAllValue")));
        holder.textPrice.setText("￥" + Common.formatFloat(map.get("GoodAllDisValue")));
        if ("-1".equals(map.get("atyType"))) {
            //无折扣
            holder.textOldPrice.setText("");
        } else if ("1".equals(map.get("atyType"))) {
            //有折扣
            holder.textOldPrice.setText("￥" + Common.formatFloat(map.get("GoodAllValue")));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePic;
        TextView textGoodName;
        TextView textUnitPrice;
        TextView textCount;
        TextView textPrice;
        TextView textOldPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imagePic = (ImageView) itemView.findViewById(R.id.imagePic);
            textGoodName = (TextView) itemView.findViewById(R.id.textGoodName);
            textUnitPrice = (TextView) itemView.findViewById(R.id.textUnitPrice);
            textCount = (TextView) itemView.findViewById(R.id.textCount);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textOldPrice = (TextView) itemView.findViewById(R.id.textOldPrice);
            textOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
