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
 * 任务完成列表Adapter
 * Created by Jocerly on 2017/3/13.
 */

public class TaskFinishAdapter extends RecyclerView.Adapter<TaskFinishAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    public TaskFinishAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_finish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ConcurrentHashMap<String, String> map = listData.get(position);
        Picasso.with(context)
                .load(StringUtils.isEmpty(map.get("Pic")) ? "0" : map.get("Pic"))
                .resizeDimen(R.dimen.pad32, R.dimen.pad32)
                .centerCrop()
                .error(R.drawable.task_other)
                .placeholder(R.drawable.task_other)
                .into(viewHolder.imagePic);
        viewHolder.textTaskName.setText(map.get("TaskName"));
        if (StringUtils.isEmpty(map.get("TaskRewardMsg"))) {
            viewHolder.textTaskRewardMsg.setVisibility(View.GONE);
        } else {
            viewHolder.textTaskRewardMsg.setVisibility(View.VISIBLE);
            viewHolder.textTaskRewardMsg.setText(map.get("TaskRewardMsg"));
        }
        viewHolder.btnTask.setBackgroundResource(getBtnTaskBg(map.get("TakeRewardStatue")));
        viewHolder.btnTask.setText(getBtnTaskText(map.get("IsTakeReward")));
        viewHolder.btnTask.setTextColor(getBtnTaskTextColor(map.get("TakeRewardStatue")));
    }

    /**
     * 获取操作按钮文字
     * @param IsTakeReward 1：奖励审核中，2：领取完成
     * @return
     */
    private String getBtnTaskText(String IsTakeReward) {
        String text = "已完成";
        if ("1".equals(IsTakeReward)) {
            text = "已领取";
        }
        return text;
    }

    /**
     * 获取操作按钮背景图
     * @param takeRewardStatue 1：奖励审核中，2：领取完成
     * @return
     */
    private int getBtnTaskBg(String takeRewardStatue) {
        int rid = R.drawable.task_finish;
        if ("1".equals(takeRewardStatue)) {
            rid = R.drawable.task_pending;
        }
        return rid;
    }

    /**
     * 获取操作按钮文字颜色
     * @param takeRewardStatue 1：奖励审核中，2：领取完成
     * @return
     */
    private int getBtnTaskTextColor(String takeRewardStatue) {
        int color = context.getResources().getColor(R.color.text_light);
        if ("1".equals(takeRewardStatue)) {
            color = context.getResources().getColor(R.color.white);
        }
        return color;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textTaskName;
        private TextView textTaskRewardMsg;
        private TextView btnTask;

        public ViewHolder(View convertView) {
            super(convertView);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textTaskName = (TextView) convertView.findViewById(R.id.textTaskName);
            textTaskRewardMsg = (TextView) convertView.findViewById(R.id.textTaskRewardMsg);
            btnTask = (TextView) convertView.findViewById(R.id.btnTask);
        }
    }
}
