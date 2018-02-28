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
 * Created by dengjiang on 2017/10/19.
 */

public class DiscountGoodsAdapter extends RecyclerView.Adapter<DiscountGoodsAdapter.ViewHolder> {
    private List<ConcurrentHashMap<String, String>> listData;
    private Context context;
    private DiscountGoodsAdapter.OnItemDeleteListener onDeletelistener;

    public DiscountGoodsAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setOnDeletelistener(DiscountGoodsAdapter.OnItemDeleteListener onDeletelistener) {
        this.onDeletelistener = onDeletelistener;
    }

    @Override
    public DiscountGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discountgood, parent, false);
        return new DiscountGoodsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        holder.textGoodName.setText(map.get("GoodsName"));
        holder.textCode.setText(map.get("GoodsCode"));
        holder.textPrice.setText(map.get("SpecialPrice") + "元/" + map.get("UnitName"));
        holder.textOldPrice.setText(Common.formatFloat(map.get("Price")));
        holder.textStock.setText(map.get("TotalCount") + map.get("UnitName"));
        holder.textRestrictionsNO.setText(map.get("LimitCount") + map.get("UnitName"));
        Picasso.with(context)
                .load(StringUtils.isEmpty(map.get("GoodsPic")) ? "0" : map.get("GoodsPic"))
                .resizeDimen(R.dimen.pad50, R.dimen.pad50)
                .centerCrop()
                .error(R.drawable.orderdefault)
                .placeholder(R.drawable.orderdefault)
                .into(holder.imgGoodsPic);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeletelistener != null) {
                    onDeletelistener.itemDelete(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface OnItemDeleteListener {
        public void itemDelete(int position);

    }

    public class ViewHolder extends BaseViewHolder {

        private TextView textGoodName;
        private TextView textCode;
        private TextView textPrice;
        private TextView textOldPrice;
        private TextView textStock;
        private TextView textRestrictionsNO;
        private ImageView imgDelete;
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
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);
            imgGoodsPic = (ImageView) itemView.findViewById(R.id.imgGoodsPic);
        }
    }
}
