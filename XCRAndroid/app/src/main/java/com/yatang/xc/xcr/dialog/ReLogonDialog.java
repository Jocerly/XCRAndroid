package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BaseActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 被迫下线
 * Created by dengjiang on 2017/4/15.
 */
public class ReLogonDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textTitle;
    private TextView textMsg;

    private LinearLayout llNomanl;
    private Button btnReLogin;
    private Button btnDoExit;

    private Button btnClose;
    private Context act;
    private String message;

    public ReLogonDialog(Context context, String message) {
        super(context, R.style.ShareDialog);
        this.act = context;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_relogin);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textMsg = (TextView) findViewById(R.id.textMsg);
        btnReLogin = (Button) findViewById(R.id.btnOk);
        llNomanl = (LinearLayout) findViewById(R.id.llNomanl);
        btnDoExit = (Button) findViewById(R.id.btnCancle);
        btnClose = (Button) findViewById(R.id.btnClose);

        textTitle.setText("下线通知");
        textTitle.setVisibility(View.VISIBLE);
        if (!StringUtils.isEmpty(message)) {
            textMsg.setText(message);
        } else {
            textMsg.setText("您已被迫下线，请确认密码是否泄漏！");
        }
        btnReLogin.setOnClickListener(this);
        btnReLogin.setText(R.string.doreLogin);
        btnDoExit.setOnClickListener(this);
        btnDoExit.setText(R.string.doExit);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                ((BaseActivity) act).doEmpLoginOut();
                break;
            case R.id.btnCancle:
                MyApplication.instance.deleteLoginInfo((BaseActivity) act);
                ((BaseActivity) act).exit();
                break;
        }
        dismiss();
    }
}