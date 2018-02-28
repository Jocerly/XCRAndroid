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

import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/10/24.
 */

public class DiscountCotentAdapter extends RecyclerView.Adapter<DiscountCotentAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;

    public DiscountCotentAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }


    @Override
    public DiscountCotentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discountcontent, parent, false);
        return new DiscountCotentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiscountCotentAdapter.ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textGoodName.setText(map.get("GoodsName"));
        holder.textCode.setText(map.get("GoodsCode"));
        String price = Common.formatFloat(map.get("Price"));
        holder.textOldPrice.setText(price);
        if ("1".equals(map.get("DiscountType"))) {
            //特价
            holder.textPrice.setText(map.get("SpecialPrice") + "元/" + map.get("UnitName"));
        } else if ("2".equals(map.get("DiscountType"))) {
            //折扣
            double discount = Float.parseFloat(map.get("Discount"));
            double d_price = Float.parseFloat(price);
            double lastPrice = d_price * discount/10;
            holder.textPrice.setText(Common.formatFloat(lastPrice) + "元/" + map.get("UnitName"));
        }
        holder.textStock.setText(map.get("TotalCount") + map.get("UnitName"));
        holder.textRestrictionsNO.setText(map.get("LimitCount") + map.get("UnitName"));
        Picasso.with(context)
                .load(StringUtils.isEmpty(map.get("GoodsPic")) ? "0" : map.get("GoodsPic"))
                .resizeDimen(R.dimen.pad50, R.dimen.pad50)
                .centerCrop()
                .error(R.drawable.orderdefault)
                .placeholder(R.drawable.orderdefault)
                .into(holder.imgGoodsPic);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends BaseViewHolder {

        private TextView textGoodName;
        private TextView textCode;
        private TextView textPrice;
        private TextView textOldPrice;
        private TextView textStock;
        private TextView textRestrictionsNO;
        private ImageView imgGoodsPic;

        public ViewHolder(View itemView) {
            super(itemView);
            textGoodName = (TextView) itemView.findViewById(R.id.textGoodName);
            textCode = (TextView) itemView.findViewById(R.id.textCode);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textOldPrice = (TextView) itemView.findViewById(R.id.textOldPrice);
            textOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            textStock = (TextView) itemView.findViewById(R.id.textStock);
            textRestrictionsNO = (TextView) itemView.findViewById(R.id.textRestrictionsNO);
            imgGoodsPic = (ImageView) itemView.findViewById(R.id.imgGoodsPic);
        }
    }
}
