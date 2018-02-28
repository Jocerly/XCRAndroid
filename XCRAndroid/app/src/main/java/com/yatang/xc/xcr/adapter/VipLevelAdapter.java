package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会员列表数据适配器
 * Created by dengjiang on 2017/11/8.
 */

public class VipLevelAdapter extends PagerAdapter {
    private List<ConcurrentHashMap<String, String>> listData;
    private List<View> views;
    private Context context;
    private int mChildCount = 0;

    public VipLevelAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
        views = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            views.add(null);
        }
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (views.get(position) == null) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_viplevel, container, false);
            bind(position, view);
            views.set(position, view);
        }
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    private void bind(final int position, View view) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        LinearLayout lineroot = (LinearLayout) view.findViewById(R.id.lineroot);
        ImageView imgVip = (ImageView) view.findViewById(R.id.imgVip);
        TextView textAccumulatedIntegral = (TextView) view.findViewById(R.id.textAccumulatedIntegral);
        setBack(lineroot, map.get("VipIdentify"));
        setVipIcon(imgVip, map.get("VipIdentify"));
        textAccumulatedIntegral.setText(map.get("NeedIntegral"));
    }

    /**
     * 设置会员等级背景
     */
    private void setBack(LinearLayout lin_LevelBack, String currentLevel) {
        int backId;
        switch (currentLevel) {
            case "0":
                backId = R.drawable.v0_bg;
                break;
            case "1":
            case "2":
            case "3":
                backId = R.drawable.v1_v2_v3_bg;
                break;
            case "4":
            case "5":
                backId = R.drawable.v4_v5_bg;
                break;
            case "6":
            case "7":
                backId = R.drawable.v6_v7_bg;
                break;
            case "8":
            case "9":
                backId = R.drawable.v8_v9_bg;
                break;
            case "10":
                backId = R.drawable.v10_bg;
                break;
            default:
                backId = R.drawable.v10_bg;
                break;
        }
        lin_LevelBack.setBackgroundResource(backId);
    }

    private void setVipIcon(ImageView img, String currentLevel) {
        int imgId;
        switch (currentLevel) {
            case "0":
                imgId = R.drawable.v0;
                break;
            case "1":
                imgId = R.drawable.v1;
                break;
            case "2":
                imgId = R.drawable.v2;
                break;
            case "3":
                imgId = R.drawable.v3;
                break;
            case "4":
                imgId = R.drawable.v4;
                break;
            case "5":
                imgId = R.drawable.v5;
                break;
            case "6":
                imgId = R.drawable.v6;
                break;
            case "7":
                imgId = R.drawable.v7;
                break;
            case "8":
                imgId = R.drawable.v8;
                break;
            case "9":
                imgId = R.drawable.v9;
                break;
            case "10":
                imgId = R.drawable.v10;
                break;
            default:
                imgId = R.drawable.v10;
                break;
        }
        img.setImageResource(imgId);
    }
}
