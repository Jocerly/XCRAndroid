package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.db.ADEntity;
import com.yatang.xc.xcr.db.ADEntityBase;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页供应链广告适配器
 * Created by dengjiang on 2017/9/5.
 */

public class ADAdapter extends BaseAdapter {
    private Context context;
    private List<ADEntity> listData;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局
    private int width_1, height_1;//位置1图片高宽
    private int width_2_3, height_2_3;//位置2 3 图片高宽
    private int width_4_5_6, height_4_5_6;//位置4 5 6 图片高宽
    private int width_7, height_7;//位置7图片高宽
    private int dividerHeight = 6;//分割线宽度

    /**
     * 构造方法
     *
     * @param listData 需要显示的广告图片数据
     */
    public ADAdapter(Context context, List<ADEntity> listData) {
        this.context = context;
        this.listData = listData;
        this.mInflater = LayoutInflater.from(context);
        dividerHeight = Common.dip2px(context, 6);
    }

    /**
     * 初始化各个图片的尺寸
     *
     * @param width  屏幕宽度，位置7图片宽度
     * @param height 位置7图片高度
     */
    public void initSize(int width, int height) {
        this.width_7 = width;
        this.height_7 = height;
        width_1 = (width_7 - dividerHeight) * 575 / (575 + 486);
        height_1 = width_1;

        width_2_3 = width_7 - dividerHeight - width_1;
        height_2_3 = (height_1 - dividerHeight) / 2;

        width_4_5_6 = (width_7 - dividerHeight * 2) / 3;
        height_4_5_6 = width_4_5_6 * 50 / 87;
    }
//    @Override
//    public ADAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false);
//        return new ADAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ADAdapter.ViewHolder viewHolder, int position) {
//        ADEntity adEntity = listData.get(position);
//        for (ADEntityBase adEntityBase : adEntity.getShortcutSecondList()) {
//            switch (adEntityBase.getAdId()) {
//                case "1":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_1);
//                    viewHolder.img_1.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "2":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_2);
//                    viewHolder.img_2.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "3":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_3);
//                    viewHolder.img_3.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "4":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_4);
//                    viewHolder.img_4.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "5":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_5);
//                    viewHolder.img_5.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "6":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_6);
//                    viewHolder.img_6.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                case "7":
//                    Picasso.with(context)
//                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
//                            .error(R.drawable.task_other)
//                            .placeholder(R.drawable.task_other)
//                            .into(viewHolder.img_7);
//                    viewHolder.img_7.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        JCLoger.debug("MY2==SIZE="+listData.size());
//        return listData.size();
//    }

    /**
     * 设置广告数据
     */
    public void setListData(List<ADEntity> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_ad, null);
            holder = new ViewHolder();
            holder.img_1 = (ImageView) convertView.findViewById(R.id.img_1);
            holder.img_2 = (ImageView) convertView.findViewById(R.id.img_2);
            holder.img_3 = (ImageView) convertView.findViewById(R.id.img_3);
            holder.img_4 = (ImageView) convertView.findViewById(R.id.img_4);
            holder.img_5 = (ImageView) convertView.findViewById(R.id.img_5);
            holder.img_6 = (ImageView) convertView.findViewById(R.id.img_6);
            holder.img_7 = (ImageView) convertView.findViewById(R.id.img_7);
            convertView.setTag(holder); //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
        ADEntity adEntity = listData.get(position);
        for (ADEntityBase adEntityBase : adEntity.getShortcutSecondList()) {
            switch (adEntityBase.getAdId()) {
                case "1":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_1, height_1)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_1);
                    holder.img_1.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "2":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_2_3, height_2_3)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_2);
                    holder.img_2.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "3":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_2_3, height_2_3)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_3);
                    holder.img_3.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "4":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_4_5_6, height_4_5_6)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_4);
                    holder.img_4.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "5":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_4_5_6, height_4_5_6)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_5);
                    holder.img_5.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "6":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_4_5_6, height_4_5_6)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_6);
                    holder.img_6.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                case "7":
                    Picasso.with(context)
                            .load(StringUtils.isEmpty(adEntityBase.AdPic) ? "" : adEntityBase.AdPic)
                            .error(R.drawable.defualt_img)
                            .resize(width_7, height_7)
                            .centerCrop()
                            .placeholder(R.drawable.defualt_img)
                            .into(holder.img_7);
                    holder.img_7.setOnClickListener(new ADClickListenner(adEntityBase.AdJump));
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView img_1, img_2, img_3, img_4, img_5, img_6, img_7;

//        public ViewHolder(View convertView) {
//            super(convertView);
//            img_1 = (ImageView) convertView.findViewById(R.id.img_1);
//            img_2 = (ImageView) convertView.findViewById(R.id.img_2);
//            img_3 = (ImageView) convertView.findViewById(R.id.img_3);
//            img_4 = (ImageView) convertView.findViewById(R.id.img_4);
//            img_5 = (ImageView) convertView.findViewById(R.id.img_5);
//            img_6 = (ImageView) convertView.findViewById(R.id.img_6);
//            img_7 = (ImageView) convertView.findViewById(R.id.img_7);
//        }
    }

    private class ADClickListenner implements View.OnClickListener {
        private String AdJump;

        public ADClickListenner(String AdJump) {
            this.AdJump = AdJump;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.itemClick(AdJump.trim());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void itemClick(String AdUrl);
    }
}
