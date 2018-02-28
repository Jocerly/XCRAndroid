package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/7/7.
 */

public class ImageViewHolder implements Holder<ConcurrentHashMap<String, String>> {

    private ImageView imageView;
    private int width = 0, height = 0;
    LayoutInflater inflater;

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public View createView(Context context) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        View view = inflater.inflate(R.layout.item_banner, null);
        imageView = (ImageView) view.findViewById(R.id.img);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, ConcurrentHashMap<String, String> data) {
        Picasso.with(context)
                .load(StringUtils.isEmpty(data.get("AdvertisPic")) ? "0" : data.get("AdvertisPic"))
                .resize(width, height)
                .centerCrop()
                .error(R.drawable.defualt_img2)
                .placeholder(R.drawable.defualt_img2)
                .into(imageView);
    }
}
