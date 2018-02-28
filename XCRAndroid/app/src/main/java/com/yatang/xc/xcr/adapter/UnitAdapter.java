package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**常用单位Adapter
 * Created by lusha on 2017/09/06.
 */

public class UnitAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;


    public UnitAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        super(context, listData);
        this.context = context;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) baseViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            viewHolder.textUnitName.setText(entity.get("UnitName"));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("UnitName"));
                }
            });

        }


    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unit, parent,false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder{
        private TextView textUnitName;

        public ViewHolder(View itemView) {
            super(itemView);
            textUnitName = (TextView) itemView.findViewById(R.id.textUnitName);
        }
    }
    public interface OnItemClickListener{
        public void itemClick(String unitName);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
