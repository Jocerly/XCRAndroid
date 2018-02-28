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
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务Adapter
 * Created by Jocerly on 2017/3/13.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnTaskClistener onTaskClistener;
    private OnItemClickListener onItemClickListener;

    public TaskAdapter(Context context, List<ConcurrentHashMap<String, String>> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
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
            viewHolder.textTaskTip.setVisibility(View.GONE);
        } else {
            viewHolder.textTaskTip.setText(map.get("TaskRewardMsg"));
            viewHolder.textTaskTip.setVisibility(View.VISIBLE);
        }

        setBtnTaskEnble(viewHolder.btnTask, map.get("TaskClassfy"), map.get("TaskType"), map.get("TaskStatue"), map.get("TaskUrl"), map.get("IsTakeReward"));
        viewHolder.btnTask.setBackgroundResource(getBtnTaskBg(map.get("TaskClassfy"), map.get("TaskType"), map.get("TaskStatue"), map.get("TaskUrl"), map.get("IsTakeReward")));
        viewHolder.btnTask.setText(getBtnTaskText(map.get("TaskClassfy"), map.get("TaskType"), map.get("TaskStatue"), map.get("TaskUrl"), map.get("IsTakeReward")));
        viewHolder.btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTaskClistener.onTask(listData.get(position));
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.itemClick(listData.get(position),getBtnTaskText(listData.get(position).get("TaskClassfy"), listData.get(position).get("TaskType"), listData.get(position).get("TaskStatue"), listData.get(position).get("TaskUrl"), listData.get(position).get("IsTakeReward")));
            }
        });
    }

    /**
     * 设置按钮是否可点击
     */
    private void setBtnTaskEnble(PressTextView btnTask, String taskClassfy, String taskType, String taskStatue, String taskUrl, String takeReward) {
        switch (Integer.parseInt(taskStatue)) {
            case 0:
                if (StringUtils.isEmpty(taskUrl)) {
                    if ("3".equals(taskClassfy) || !"3".equals(taskType)) {
                        btnTask.setEnabled(true);
                    } else {
                        btnTask.setEnabled(false);
                    }
                } else {
                    btnTask.setEnabled(true);
                }
                break;
            case 1:
                btnTask.setEnabled(false);
                break;
            case 2:
                if ("0".equals(takeReward)) {
                    btnTask.setEnabled(false);
                } else {
                    btnTask.setEnabled(true);
                }
                break;
            case 3:
                btnTask.setEnabled(true);
        }
    }

    /**
     * 获取操作按钮文字
     *
     * @param taskType   1：本地任务拍门头照、2：本地跳转（根据url页面）、3：后台自动处理、4：本地输入单号，5：申请租金补贴，6：签到
     * @param taskStatue 0：未完成、1：任务审核中，2：已完成，3：任务审核失败
     * @param taskUrl
     * @return
     */
    private String getBtnTaskText(String taskClassfy, String taskType, String taskStatue, String taskUrl, String takeReward) {
        String text = "待完成";
        switch (Integer.parseInt(taskStatue)) {
            case 0:
                if (StringUtils.isEmpty(taskUrl)) {
                    if ("3".equals(taskClassfy) || !"3".equals(taskType)) {
                        text = "做任务";
                    } else {
                        text = "待完成";
                    }
                } else {
                    text = "做任务";
                }
                break;
            case 1:
                text = "审核中";
                break;
            case 2:
                if ("0".equals(takeReward)) {
                    text = "已完成";
                } else {
                    text = "领奖励";
                }
                break;
            case 3:
                text = "继续做";
        }
        return text;
    }

    /**
     * 获取操作按钮背景图
     *
     * @param taskType   1：本地任务拍门头照、2：本地跳转（根据url页面）、3：后台自动处理、4：本地输入单号，5：申请租金补贴，6：签到
     * @param taskStatue 0：未完成、1：任务审核中，2：已完成
     * @param taskUrl
     * @return
     */
    private int getBtnTaskBg(String taskClassfy, String taskType, String taskStatue, String taskUrl, String takeReward) {
        int rid = R.drawable.task_pending;

        switch (Integer.parseInt(taskStatue)) {
            case 0:
                if (StringUtils.isEmpty(taskUrl)) {
                    if ("3".equals(taskClassfy) || !"3".equals(taskType)) {
                        rid = R.drawable.task_take;
                    } else {
                        rid = R.drawable.task_pending;
                    }
                } else {
                    rid = R.drawable.task_take;
                }
                break;
            case 1:
                rid = R.drawable.task_pending;
                break;
            case 2:
                if ("0".equals(takeReward)) {
                    rid = R.drawable.task_finish;
                } else {
                    rid = R.drawable.task_award;
                }
                break;
            case 3:
                rid = R.drawable.task_retake;
                break;
        }
        return rid;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePic;
        private TextView textTaskName;
        private PressTextView btnTask;
        private TextView textTaskTip;

        public ViewHolder(View convertView) {
            super(convertView);
            imagePic = (ImageView) convertView.findViewById(R.id.imagePic);
            textTaskName = (TextView) convertView.findViewById(R.id.textTaskName);
            btnTask = (PressTextView) convertView.findViewById(R.id.btnTask);
            textTaskTip = (TextView) convertView.findViewById(R.id.textTaskTip);
        }
    }

    public interface OnTaskClistener {
        public void onTask(ConcurrentHashMap<String, String> map);
    }

    public void setOnTaskClistener(OnTaskClistener onTaskClistener) {
        this.onTaskClistener = onTaskClistener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener {
        public void itemClick(ConcurrentHashMap<String, String> map,String btn);

    }
}
