package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.entity.CouponList;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 活动列表对象
 * Created by dengjiang on 2017/10/12.
 */

public class CouponListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private String status;

    public CouponListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    public CouponListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData, String status) {
        this(context, listData);
        this.status = status;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) baseViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            setIcon(holder.imageType, holder.textType, entity.get("EventType"));
            holder.textName.setText(entity.get("EventName"));
            holder.textTime.setText(entity.get("StartTime") + "-" + entity.get("EndTime"));
            if ("2".equals(status)) {
                holder.textRemainDays.setText("剩余" + entity.get("RemainDays") + "天");
                holder.textRemainDays.setVisibility(View.VISIBLE);
            } else {
                holder.textRemainDays.setVisibility(View.INVISIBLE);
            }
            if ("3".equals(status)) {
                holder.imgNewcustom.setImageResource(R.drawable.newcustomgray);
                holder.imageType.setBackgroundResource(R.drawable.btn_circle_gray);
                holder.textType.setBackgroundResource(R.drawable.grayback);
                holder.textType.setTextColor(context.getResources().getColor(R.color.gray));
            } else {
                holder.imgNewcustom.setImageResource(R.drawable.newcustom);
                holder.textType.setBackgroundResource(R.drawable.redback);
                holder.textType.setTextColor(context.getResources().getColor(R.color.red));
            }
            if ("1".equals(entity.get("IsNewUserCanUse"))) {
                holder.imgNewcustom.setVisibility(View.VISIBLE);
            } else {
                holder.imgNewcustom.setVisibility(View.GONE);
            }
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("EventId"), entity.get("EventType"));
                }
            });
        } else if (baseViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) baseViewHolder;

            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemClickListener.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    /**
     * 设置活动的icon
     */
    private void setIcon(TextView textView, TextView textView2, String type) {
        switch (type) {
            case "1":
                textView.setBackgroundResource(R.drawable.btn_circle_coupon);
                textView.setText("券");
                textView2.setText("优惠券");
                break;
            case "2":
                textView.setBackgroundResource(R.drawable.btn_circle_discount);
                textView.setText("折");
                textView2.setText("商品折扣");
                break;
            default:
                break;
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        public void itemClick(String eventID, String eventType);

        public void OnFooterClick();
    }

    private class ViewHolder extends BaseViewHolder {

        private TextView imageType;
        private TextView textName;
        private TextView textTime;
        private TextView textType;
        private TextView textRemainDays;
        private ImageView imgNewcustom;

        public ViewHolder(View itemView) {
            super(itemView);
            imageType = (TextView) itemView.findViewById(R.id.imageType);
            textName = (TextView) itemView.findViewById(R.id.textName);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            textType = (TextView) itemView.findViewById(R.id.textType);
            textRemainDays = (TextView) itemView.findViewById(R.id.textRemainDays);
            imgNewcustom = (ImageView) itemView.findViewById(R.id.imgNewcustom);
        }
    }
}
