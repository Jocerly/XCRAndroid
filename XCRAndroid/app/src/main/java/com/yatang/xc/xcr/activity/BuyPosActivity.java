package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 购买收银机任务Activity
 * Created by dengjiang on 2017/3/28.
 */
@ContentView(R.layout.activity_buy_pos)
public class BuyPosActivity extends BaseActivity {

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;

    @BindView(id = R.id.editOrder)
    private EditText editOrder;

    @BindView(id = R.id.btnCommit, click = true)
    private TextView btnCommit;

    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textGetOrderNumber)
    private TextView textGetOrderNumber;

    @BindView(id = R.id.line_picture_content)
    private LinearLayout line_picture_content;

    String taskId;
    String orderNumber;
    private NomalDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("购买收银机");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("购买收银机");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText(getResources().getString(R.string.title_buy_pos));
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskId = bundle.getString("TskId");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btnCommit:
                orderNumber = editOrder.getText().toString().trim();
                if (StringUtils.isEmpty(orderNumber)) {
                    toast(getResources().getString(R.string.toast_input_order_number));
                } else {
                    commitOrderNumber();
                }
                break;
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        orderNumber = editOrder.getText().toString().trim();
        if (StringUtils.isEmpty(orderNumber)) {
            super.onBackPressed();
        } else {
            showExitDialog();
        }
    }

    /**
     * 退出提示对话框
     */
    private void showExitDialog() {
        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
            @Override
            public void onOkClick() {
                BuyPosActivity.super.onBackPressed();
            }
        });
        dialog.show(getResources().getString(R.string.exit_dialog_title));

    }

    /**
     * 提交订单号
     */
    private void commitOrderNumber() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("StoreName",MyApplication.instance.StoreSerialNameDefault);
        params.put("TaskId", taskId);
        params.put("OrderNo", orderNumber);
        httpRequestService.doRequestData(aty, "User/InputOrderNo", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    toast(getResources().getString(R.string.toast_commit_success));
                    Bundle bundle = new Bundle();
                    bundle.putString(UpdateSucActivity.KEY_TXT, aty.getResources().getString(R.string.txt_task_finished));
                    skipActivityForResult(aty, UpdateSucActivity.class, bundle, 1);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(getResources().getString(R.string.account_overdue));
                    doEmpLoginOut();
                } else if(Constants.M06.equals(resultParam.resultId)) {
                    //任务下架
                    NomalDialog dialog = new NomalDialog(aty);
                    dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                    dialog.showClose("很遗憾任务已下架！","我知道了");

                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("Reason", resultParam.mapData.get("Reason"));
                    bundle.putString("Title", "很遗憾，您未能完成任务！");
                    StringBuilder builder = new StringBuilder();
                    builder.append("1.订单号填写错误；\n");
                    builder.append("2.订单还未确认收货；\n");
                    builder.append("3.订单号已提交至您的其它门店；\n");
                    builder.append("请检查是否有以上原因，再去重新提交。\n");
                    bundle.putString("Reason", builder.toString());
                    skipActivityForResult(aty, TaskFailActivity.class, bundle, 0);
//                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
