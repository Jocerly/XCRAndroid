package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 我的特权 赚积分 数据适配器
 * Created by dengjiang on 2017/11/05.
 */
public class VipCenterAdapter extends BaseAdapter {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData;
    private int type;//0:我的特权 1:赚积分

    public VipCenterAdapter(Context context, List<ConcurrentHashMap<String, String>> listdata, int type) {
        this.context = context;
        this.listData = listdata;
        this.type = type;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vipcenter, null);
            holder = new ViewHolder();
            holder.imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            holder.textName = (TextView) convertView.findViewById(R.id.textName);
            holder.textInfo = (TextView) convertView.findViewById(R.id.textInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        ConcurrentHashMap<String, String> map = listData.get(position);
        if (0 == type) {
            //我的特权
            setPrivilegeList(holder, map);
        } else if (1 == type) {
            //赚积分
            setIntegralList(holder, map);
        }
        return convertView;
    }

    /**
     * 设置特权列表数据
     */
    private void setPrivilegeList(ViewHolder holder, ConcurrentHashMap<String, String> map) {
        int imgId;
        switch (map.get("PrivilegeType")) {
            case "-1":
                imgId = R.drawable.pri_more;
                break;
            case "0":
                imgId = R.drawable.pri_returncash;
                break;
//            case "1":
//                imgId = R.drawable.pri_default;
//                break;
//            case "2":
//                imgId = R.drawable.v2;
//                break;
//            case "3":
//                imgId = R.drawable.v3;
//                break;
//            case "4":
//                imgId = R.drawable.v4;
//                break;
//            case "5":
//                imgId = R.drawable.v5;
//                break;
//            case "6":
//                imgId = R.drawable.v6;
//                break;
//            case "7":
//                imgId = R.drawable.v7;
//                break;
//            case "8":
//                imgId = R.drawable.v8;
//                break;
//            case "9":
//                imgId = R.drawable.v9;
//                break;
//            case "10":
//                imgId = R.drawable.v10;
//                break;
            default:
                imgId = R.drawable.pri_returncash;
                break;
        }
        holder.imagePic.setImageResource(imgId);
        holder.textName.setText(map.get("PrivilegeName"));
        holder.textInfo.setText(map.get("PrivilegeInfo"));
    }

    /**
     * 设置积分列表数据
     */
    private void setIntegralList(ViewHolder holder, ConcurrentHashMap<String, String> map) {
        int imgId = 0;
        switch (map.get("Type")) {
            case "0":
                imgId = R.drawable.vip_sign;
                break;
            case "1":
                imgId = R.drawable.vip_learn;
                break;
            case "2":
                imgId = R.drawable.vip_pos;
                break;
            case "3":
                imgId = R.drawable.vip_buy;
                break;
            case "4":
                imgId = R.drawable.vip_deposit;
                break;
//            case "5":
//                imgId = R.drawable.v10;
//                break;
//            case "6":
//                imgId = R.drawable.v10;
//                break;
//            case "7":
//                imgId = R.drawable.v10;
//                break;
            default:
                break;
        }
        holder.imagePic.setImageResource(imgId);
        holder.textName.setText(map.get("IntegralName"));
        holder.textInfo.setText(map.get("IntegralInfo"));
    }

    class ViewHolder {
        private ImageView imagePic;
        private TextView textName;
        private TextView textInfo;
    }
}
