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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小超课堂Adapter
 * Created by Jocerly on 2017/3/13.
 */

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnClassesClistener OnClassesClistener;

    public ClassesAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ConcurrentHashMap<String, String> map = listData.get(position);
        String url = StringUtils.isEmpty(map.get("Pic")) ? "0" : map.get("Pic");
        Picasso.with(context)
                .load(url)
                .resizeDimen(R.dimen.pad32, R.dimen.pad32)
                .centerCrop()
                .error(R.drawable.task_other)
                .placeholder(R.drawable.task_other)
                .into(viewHolder.imagePic);
        String type = map.get("ClassType");
        String isFinished = map.get("IsFinished");
        try {
            if (type.equals("1")) {
                //任务型 课程
                viewHolder.textTaskName.setCompoundDrawablesWithIntrinsicBounds(0, 0, isFinished.equals("0") ? R.drawable.task_class : 0, 0);
                if(isFinished .equals("1")){
                    viewHolder.btnTask.setBackgroundResource(R.drawable.task_award);
                    viewHolder.btnTask.setText("再次学习");
                }else {
                    viewHolder.btnTask.setBackgroundResource(R.drawable.task_take);
                    viewHolder.btnTask.setText("立即学习");
                }
            } else {
                viewHolder.textTaskName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                viewHolder.btnTask.setBackgroundResource(R.drawable.task_take);
                viewHolder.btnTask.setText("立即学习");
                isFinished = "1";
            }
        } catch (Exception e) {

        }
        final boolean finish = isFinished.equals("1");
        viewHolder.textTaskName.setText(map.get("ClassName"));
        viewHolder.btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClassesClistener.onClasses(map.get("ClassName"), map.get("ClassTimes"), map.get("ClassUrl"), map.get("ClassId"), finish);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textTaskName;
        private TextView btnTask;

        public ViewHolder(View convertView) {
            super(convertView);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textTaskName = (TextView) convertView.findViewById(R.id.textTaskName);
            btnTask = (TextView) convertView.findViewById(R.id.btnTask);
        }
    }

    public interface OnClassesClistener {
        public void onClasses(String className, String classTimes, String classUrl, String taskId, boolean isFinished);
    }

    public void setOnClassesClistener(OnClassesClistener OnClassesClistener) {
        this.OnClassesClistener = OnClassesClistener;
    }
}
