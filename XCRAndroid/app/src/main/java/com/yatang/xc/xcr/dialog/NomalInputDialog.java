package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 通用输入框
 * Created by Jocerly on 2017/8/8.
 */

public class NomalInputDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textTitle;
    private EditText editMsg;
    private Button btnOk;
    private Button btnCancle;
    private OnNomalInputClickListener onNomalInputClickListener;

    private String nullToast;

    public NomalInputDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nomal_input);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        textTitle = (TextView) findViewById(R.id.textTitle);
        editMsg = (EditText) findViewById(R.id.editMsg);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancle = (Button) findViewById(R.id.btnCancle);
        btnOk.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 显示弹框
     * @param title
     * @param hint
     * @param maxLength
     */
    public void show(String title, String hint, String nullToast, int maxLength) {
        super.show();
        textTitle.setText(title);
        editMsg.setHint(hint);
        editMsg.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)}); //最大输入长度
        this.nullToast = nullToast;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancle:
                break;
            case R.id.btnOk:
                String msg = editMsg.getText().toString().trim();
                if (StringUtils.isEmpty(msg)) {
                    ViewInject.toast(context, nullToast);
                    return;
                }
                onNomalInputClickListener.onOkClick(msg);
                break;
        }
        dismiss();
    }

    /**
     * 回调接口
     *
     * @author Jocerly
     */
    public interface OnNomalInputClickListener {
        public void onOkClick(String msg);
    }

    public void setOnNomalInputClickListener(OnNomalInputClickListener onNomalInputClickListener) {
        this.onNomalInputClickListener = onNomalInputClickListener;
    }
}
