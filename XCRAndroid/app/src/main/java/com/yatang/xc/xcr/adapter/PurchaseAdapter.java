package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.DensityUtils;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 雅堂采购Adapter
 * Created by Jocerly on 2017/3/13.
 */

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnPurchaseClistener OnPurchaseClistener;

    public PurchaseAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ConcurrentHashMap<String, String> entity = listData.get(position);
        viewHolder.textName.setText(entity.get("GoodsName"));
        viewHolder.textPrice.setText("¥"+entity.get("GoodsPrice"));
        setImageHeight(viewHolder.imagePic);
        try {
            Picasso.with(context)
                    .load(StringUtils.isEmpty(entity.get("GoodsPic")) ? "0" : entity.get("GoodsPic"))
                    .error(R.drawable.defualt_img)
                    .placeholder(R.drawable.defualt_img)
                    .into(viewHolder.imagePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnPurchaseClistener.onItem(entity.get("GoodsUrl"), entity.get("GoodsName"));
            }
        });
    }

    /**
     * 图片高度设置
     */
    private void setImageHeight(ImageView imageView) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int width = (DensityUtils.getScreenW(context) - context.getResources().getDimensionPixelSize(R.dimen.pad60)) / 2;
        int height = width * 3 / 4;
        JCLoger.debug(width + "-----" + height);
        linearParams.height = height;
        imageView.setLayoutParams(linearParams);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textName;
        private TextView textPrice;
        private View rootView;

        public ViewHolder(View convertView) {
            super(convertView);
            this.rootView = convertView;
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textPrice = (TextView) convertView.findViewById(R.id.textPrice);
        }
    }

    public interface OnPurchaseClistener {
        public void onItem(String url, String purchaseName);
    }

    public void setOnPurchaseClistener(OnPurchaseClistener OnPurchaseClistener) {
        this.OnPurchaseClistener = OnPurchaseClistener;
    }
}
