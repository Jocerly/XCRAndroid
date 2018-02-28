package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务提示对话框
 * Created by dengjiang on 2017/7/12.
 */

public class TaskExplainDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textTitle;
    private TextView textTip;
    private TextView textTaskExplain;

    private LinearLayout llNomanl;
    private Button btnDoTask;
    private Button btnCancle;
    private ConcurrentHashMap<String, String> mapsData;
    private OnDoTaskClickListenner onDoTaskClickListenner;

    private Button btnClose;

    public TaskExplainDialog(@NonNull Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_task);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        textTitle = (TextView) findViewById(R.id.text_Title);
        textTip = (TextView) findViewById(R.id.text_Tip);
        textTaskExplain = (TextView) findViewById(R.id.text_TaskExplain);

        llNomanl = (LinearLayout) findViewById(R.id.llNomanl);
        btnCancle = (Button) findViewById(R.id.btnCancle);
        btnDoTask = (Button) findViewById(R.id.btnDoTask);
        btnClose = (Button) findViewById(R.id.btnClose);

        btnCancle.setOnClickListener(this);
        btnDoTask.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    /**
     * 设置对话框内容
     */
    public void setContent() {
        textTitle.setText(mapsData.get("TaskName"));
        textTip.setText(mapsData.get("TaskRewardMsg"));
        textTaskExplain.setText(mapsData.get("TaskExplain"));
    }

    /**
     * 显示 有做任务按钮的对话框
     */
    public void show(String leftBtn, String rightBtn, ConcurrentHashMap<String, String> mapsData) {
        super.show();
        this.mapsData = mapsData;
        btnCancle.setText(leftBtn);
        btnDoTask.setText(rightBtn);
        llNomanl.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.GONE);
        setContent();
    }

    /**
     * 显示 只有关闭按钮的对话框
     */
    public void show(String closeMsg, ConcurrentHashMap<String, String> mapsData) {
        super.show();
        this.mapsData = mapsData;
        btnClose.setText(closeMsg);
        llNomanl.setVisibility(View.GONE);
        btnClose.setVisibility(View.VISIBLE);
        setContent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDoTask:
                if (onDoTaskClickListenner != null) {
                    onDoTaskClickListenner.onDoTask(mapsData);
                }
                dismiss();
                break;
            case R.id.btnClose:
            case R.id.btnCancle:
                dismiss();
        }
    }

    /**
     * 回调接口
     *
     * @author Jocerly
     */
    public interface OnDoTaskClickListenner {
        public void onDoTask(ConcurrentHashMap<String, String> mapsData);
    }

    public void setOnDoTaskClickListenner(OnDoTaskClickListenner onDoTaskClickListenner) {
        this.onDoTaskClickListenner = onDoTaskClickListenner;
    }
}
