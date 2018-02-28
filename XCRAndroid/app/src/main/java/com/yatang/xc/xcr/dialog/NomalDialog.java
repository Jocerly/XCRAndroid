package com.yatang.xc.xcr.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * 普通提示框
 *
 * @author Jocerly
 */
public class NomalDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textMsg;

    private LinearLayout llNomanl;
    private Button btnOk;
    private Button btnCancle;

    private Button btnClose;
    private TextView diaTitle;
    private OnNoamlLickListener onNoamlLickListener;

    public NomalDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nomal);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        textMsg = (TextView) findViewById(R.id.textMsg);
        btnOk = (Button) findViewById(R.id.btnOk);
        llNomanl = (LinearLayout) findViewById(R.id.llNomanl);
        btnCancle = (Button) findViewById(R.id.btnCancle);
        btnClose = (Button) findViewById(R.id.btnClose);
        diaTitle = (TextView) findViewById(R.id.diaTitle);
        btnOk.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancle:
                break;
            case R.id.btnClose:
            case R.id.btnOk:
                if (onNoamlLickListener != null) {
                    onNoamlLickListener.onOkClick();
                }
                break;
        }
        dismiss();
    }

    public void show(String msg) {
        super.show();
        textMsg.setText(msg);
        btnOk.setText(R.string.ok);
        btnCancle.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
    }

    public void show(String msg, String leftStr) {
        super.show();
        textMsg.setText(msg);
        btnOk.setText(R.string.ok);
        btnCancle.setText(leftStr);
        btnCancle.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
    }

    public void show(String msg, String leftStr, String rightStr) {
        super.show();
        textMsg.setText(msg);
        btnCancle.setText(leftStr);
        btnOk.setText(rightStr);
        btnCancle.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
    }

    public void showTitle(String title, String msg,String close) {
        super.show();
        diaTitle.setText(title);
        diaTitle.setVisibility(View.VISIBLE);
        textMsg.setText(msg);
        textMsg.setTextColor(Color.GRAY);
        llNomanl.setVisibility(View.GONE);
        btnClose.setText(close);
        btnClose.setVisibility(View.VISIBLE);
    }
    public void showTitle(String title, int msg, String close){
        super.show();
        diaTitle.setText(title);
        diaTitle.setVisibility(View.VISIBLE);
        textMsg.setText(msg);
        textMsg.setTextColor(Color.GRAY);
        llNomanl.setVisibility(View.GONE);
        btnClose.setText(close);
        btnClose.setVisibility(View.VISIBLE);
        textMsg.setGravity(Gravity.LEFT);
    }
    /**
     * 显示关闭弹框
     *
     * @param msg
     */
    public void showClose(String msg) {
        super.show();
        textMsg.setText(msg);
        llNomanl.setVisibility(View.GONE);
        btnClose.setVisibility(View.VISIBLE);
    }

    public void showClose(String msg, String close){
        super.show();
        textMsg.setText(msg);
        llNomanl.setVisibility(View.GONE);
        btnClose.setText(close);
        btnClose.setVisibility(View.VISIBLE);
    }
    /**
     * 回调接口
     *
     * @author Jocerly
     */
    public interface OnNoamlLickListener {
        public void onOkClick();
    }

    public void setOnNoamlLickListener(OnNoamlLickListener onNoamlLickListener) {
        this.onNoamlLickListener = onNoamlLickListener;
    }
}
