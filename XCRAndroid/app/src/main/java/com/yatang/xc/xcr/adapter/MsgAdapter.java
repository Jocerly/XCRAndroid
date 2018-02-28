package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.DensityUtils;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息adapter
 * Created by Jocerly on 2017/3/13.
 */

public class MsgAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int width;
    private int height;

    public MsgAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
        setImageHeight();
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 图片高度设置
     */
    private void setImageHeight() {
        width = (DensityUtils.getScreenW(context) - context.getResources().getDimensionPixelSize(R.dimen.pad50));
        height = width * 9 / 20;
        JCLoger.debug(width + "-----" + height);
    }

    @Override
    public void bindDataToItemView(BaseViewHolder myViewHolder, int position) {
        if (myViewHolder instanceof ViewHolder) {
            ViewHolder baseViewHolder = (ViewHolder) myViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            baseViewHolder.textTitle.setText(entity.get("MsgName"));
            String date = Common.stampToDate(simpleDateFormat, entity.get("MsgTime"));
            baseViewHolder.textDate.setText(Common.isToday(date) ? "今天" : date);
            baseViewHolder.textTime.setText(Common.stampToDate(simpleDateFormat2, entity.get("MsgTime")));
            Picasso.with(context)
                    .load(StringUtils.isEmpty(entity.get("MsgPic")) ? "0" : entity.get("MsgPic"))
                    .resize(width, height)
                    .centerCrop()
                    .error(R.drawable.defualt_img2)
                    .placeholder(R.drawable.defualt_img2)
                    .into(baseViewHolder.imagePic);
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("MsgUrl"), entity.get("MsgName"));
                }
            });
        }else if (myViewHolder instanceof FooterHolder){
            final FooterHolder footerHolder = (FooterHolder)myViewHolder;

            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE){
                        onItemClickListener.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    class ViewHolder extends BaseViewHolder {
        private TextView textTitle;
        private ImageView imagePic;
        private TextView textDate;
        private TextView textTime;

        public ViewHolder(View convertView) {
            super(convertView);
            textTitle = (TextView) convertView.findViewById(R.id.textTitle);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textDate = (TextView) convertView.findViewById(R.id.textDate);
            textTime = (TextView) convertView.findViewById(R.id.textTime);
        }
    }

    public interface OnItemClickListener {
        public void itemClick(String url, String title);
        public void OnFooterClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
