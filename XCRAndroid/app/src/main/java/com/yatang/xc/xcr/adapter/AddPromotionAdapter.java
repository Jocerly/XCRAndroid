package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.entity.PromotionEntity;

import java.util.List;

/**
 * 新增活动列表适配器
 * Created by dengjiang on 2017/10/11.
 */

public class AddPromotionAdapter extends RecyclerView.Adapter<AddPromotionAdapter.ViewHolder> {
    private List<PromotionEntity> listData;
    private Context context;
    private OnAddPromotionClickListenner onAddPromotionClickListenner;

    public AddPromotionAdapter(Context context, List<PromotionEntity> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setOnAddPromotionClickListenner(AddPromotionAdapter.OnAddPromotionClickListenner onAddPromotionClickListenner) {
        this.onAddPromotionClickListenner = onAddPromotionClickListenner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promotion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PromotionEntity entity = listData.get(position);
        setIcon(holder.textType, entity.getType());
        holder.textName.setText(entity.getName());
        holder.textDescribe.setText(entity.getDescribe());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAddPromotionClickListenner != null) {
                    onAddPromotionClickListenner.onAddPromotion(entity.getType());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private void setIcon(TextView textView, String type) {
        switch (type) {
            case "0":
                textView.setBackgroundResource(R.drawable.btn_circle_coupon);
                textView.setText("券");
                break;
            case "1":
                textView.setBackgroundResource(R.drawable.btn_circle_discount);
                textView.setText("折");
                break;
            default:
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textType;
        private TextView textName;
        private TextView textDescribe;

        public ViewHolder(View convertView) {
            super(convertView);
            textType = (TextView) convertView.findViewById(R.id.textType);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textDescribe = (TextView) convertView.findViewById(R.id.textDescribe);
        }
    }

    public interface OnAddPromotionClickListenner {
        void onAddPromotion(String type);
    }
}
