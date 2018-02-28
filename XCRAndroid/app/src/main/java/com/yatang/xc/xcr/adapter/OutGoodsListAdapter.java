package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.AddCouponActivity;
import com.yatang.xc.xcr.activity.AddDiscountActivity;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外送商品Adapter
 * Created by lusha on 2017/07/17.
 */

public class OutGoodsListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemclickLister onItemclickLister;
    private List<String> list_GoodsCode;

    private List<String> listChoice = new ArrayList<>();
    private int type;//0:查看详情、1：可选择

    public OutGoodsListAdapter(Context context, List<ConcurrentHashMap<String, String>> list) {
        super(context, list);
        this.context = context;
    }

    public void setList_GoodsCode(List<String> list_GoodsCode) {
        this.list_GoodsCode = list_GoodsCode;
    }

    public void clearListChoice() {
        this.listChoice.clear();
    }

    public List<String> getListChoice() {
        return listChoice;
    }

    /**
     * 0:查看详情、1：可选择 ,3:选择商品
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, final int position) {
        if (myViewHolder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            String temp = Common.formatTosepara(entity.get("GoodsPrice"), 3, 2);
            if (!StringUtils.isEmpty(temp)) {
                viewHolder.textGoodsPrice.setText(temp);
            }
            if (1 == type) {
                viewHolder.imageNext.setVisibility(View.GONE);
                viewHolder.imgSelect.setVisibility(View.VISIBLE);
            } else if (0 == type) {
                viewHolder.imageNext.setVisibility(View.VISIBLE);
                viewHolder.imgSelect.setVisibility(View.GONE);
            } else if (3 == type) {
                viewHolder.imageNext.setVisibility(View.GONE);
                viewHolder.imgSelect.setVisibility(View.GONE);
            }
            viewHolder.textGoodsName.setText(entity.get("GoodsName"));
            viewHolder.textGoodsCode.setText(entity.get("GoodsCode"));
            if (StringUtils.isEmpty(entity.get("UnitName"))) {
                viewHolder.textUnitName.setText("元");
            } else {
                viewHolder.textUnitName.setText("元/" + entity.get("UnitName").replaceAll(" ", ""));
            }
            Picasso.with(context)
                    .load(StringUtils.isEmpty(entity.get("GoodsPic")) ? "0" : entity.get("GoodsPic"))
                    .resizeDimen(R.dimen.pad50, R.dimen.pad50)
                    .centerCrop()
                    .error(R.drawable.orderdefault)
                    .placeholder(R.drawable.orderdefault)
                    .into(viewHolder.imageGoodsPic);
            if (listChoice.contains(entity.get("GoodsCode"))) {//选中了的
                viewHolder.imgSelect.setBackgroundResource(R.drawable.select);
            } else {
                viewHolder.imgSelect.setBackgroundResource(R.drawable.select_not);
            }
            if (3 == type && list_GoodsCode.contains(entity.get("GoodsCode"))) {
                viewHolder.imgSelect.setBackgroundResource(R.drawable.select);
                viewHolder.imgSelect.setVisibility(View.VISIBLE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) {
                        if (listChoice.contains(entity.get("GoodsCode"))) {//选中了的
                            listChoice.remove(entity.get("GoodsCode"));
                        } else {
                            listChoice.add(entity.get("GoodsCode"));
                        }
                        notifyDataSetChanged();
                    }
                    onItemclickLister.OnItemClick(entity, type, listChoice.size());
                }
            });
        } else if (myViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) myViewHolder;
            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemclickLister.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_outside_order, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textGoodsName;
        private TextView textGoodsCode;
        private TextView textGoodsPrice;
        private TextView textUnitName;
        private ImageView imageGoodsPic;
        private ImageView imageNext;
        private ImageView imgSelect;

        public ViewHolder(View convertView) {
            super(convertView);
            textGoodsName = (TextView) convertView.findViewById(R.id.textGoodsName);
            textGoodsCode = (TextView) convertView.findViewById(R.id.textGoodsCode);
            textGoodsPrice = (TextView) convertView.findViewById(R.id.textGoodsPrice);
            textUnitName = (TextView) convertView.findViewById(R.id.textUnitName);
            imageGoodsPic = (ImageView) convertView.findViewById(R.id.imageGoodsPic);
            imageNext = (ImageView) convertView.findViewById(R.id.imageNext);
            imgSelect = (ImageView) convertView.findViewById(R.id.imgSelect);
        }
    }

    public interface OnItemclickLister {
        /**
         * 点击底部区域
         */
        public void OnFooterClick();

        /**
         * 0:查看详情、1：可选择
         *
         * @param map
         * @param type
         */
        public void OnItemClick(ConcurrentHashMap<String, String> map, int type, int num);
    }

    public void setOnItemclickLister(OnItemclickLister onItemclickLister) {
        this.onItemclickLister = onItemclickLister;
    }
}
