package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;

/**
 * Created by dengjiang on 2017/6/10.
 */

public class GoodsPicAdapter extends
        RecyclerView.Adapter<GoodsPicAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<String> mDatas;
    private Context mContext;
    private OnPicListclickListenner onPicListclickListenner;
    public GoodsPicAdapter(Context context, List<String> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        this.mContext = context;
    }
    public void setOnPicListclickListenner(OnPicListclickListenner onPicListclickListenner) {
        this.onPicListclickListenner = onPicListclickListenner;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTxt;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view
                    .findViewById(R.id.img);
        }

    }

    @Override
    public int getItemCount() {
        if(mDatas.size()>5) {
            return 5;
        }
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_goodpic,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Picasso.with(mContext)
                .load(StringUtils.isEmpty(mDatas.get(i))?"0":mDatas.get(i))
                .resizeDimen(R.dimen.pad50, R.dimen.pad50)
                .centerCrop()
                .error(R.drawable.orderdefault)
                .placeholder(R.drawable.orderdefault)
                .into(viewHolder.mImg);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(onPicListclickListenner!=null) {
//                    onPicListclickListenner.onPicClick();
//                }
//            }
//        });
    }
    public interface OnPicListclickListenner {
        public void onPicClick();
    }
}