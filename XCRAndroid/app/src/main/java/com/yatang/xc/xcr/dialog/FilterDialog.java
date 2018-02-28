package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;

import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据统计，门店收入筛选对话框
 * Created by dengjiang on 2017/6/30.
 */

public class FilterDialog extends Dialog implements View.OnClickListener {
    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    private final Context context;
    private TextView btnLeft;
    private TextView textTitle;
    private RadioButton rbYesterday;
    private RadioButton rbDay;
    private RadioButton rbMonth;
    private RadioButton rbWeek;
    private RadioButton rbThirty;
    private RadioButton rbCustom;
    private RelativeLayout rlStartTime;
    private RelativeLayout rlEndTime;
    private View line_date;
    private View line_date1;
    private TextView textStartTime;
    private TextView textEndTime;
    private TextView btnScreen;
    private OnFilterDialogClickLinster onFilterDialogClickLinster;
    private SelectDateDialog dateDialog;
    private String screenStatue;
    private int flag = 1;

    public FilterDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText("筛选");
        btnLeft = (TextView) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);
        textStartTime = (TextView) findViewById(R.id.textStartTime);
        textEndTime = (TextView) findViewById(R.id.textEndTime);
        btnScreen = (TextView) findViewById(R.id.btnScreen);
        rlStartTime = (RelativeLayout) findViewById(R.id.rlStartTime);
        line_date = findViewById(R.id.line_date);
        line_date1 = findViewById(R.id.line_date1);
        rlEndTime = (RelativeLayout) findViewById(R.id.rlEndTime);
        rbDay = (RadioButton) findViewById(R.id.rbDay);
        rbYesterday= (RadioButton) findViewById(R.id.rbYesterday);
        rbMonth = (RadioButton) findViewById(R.id.rbMonth);
        rbWeek = (RadioButton) findViewById(R.id.rbWeek);
        rbThirty = (RadioButton) findViewById(R.id.rbThirty);
        rbCustom = (RadioButton) findViewById(R.id.rbCustom);
        rlStartTime.setOnClickListener(this);
        rlEndTime.setOnClickListener(this);
        rbDay.setOnClickListener(this);
        rbYesterday.setOnClickListener(this);
        rbMonth.setOnClickListener(this);
        rbWeek.setOnClickListener(this);
        rbThirty.setOnClickListener(this);
        rbCustom.setOnClickListener(this);
        btnScreen.setOnClickListener(this);

        //初始化日期选择对话框
        dateDialog = new SelectDateDialog(context);
        dateDialog.setOnClickListener(onDateClickListener);
    }

    public void show(String screenStatue) {
        show(screenStatue, "", "");
    }

    /**
     * 获取数据
     *
     * @param screenStatue 默认选择的筛选项
     * @param StartDate    开始日期
     * @param EndDate      结束日期
     */
    public void show(String screenStatue, String StartDate, String EndDate) {
        super.show();
        rlStartTime.setVisibility(View.GONE);
        rlEndTime.setVisibility(View.GONE);
        line_date.setVisibility(View.GONE);
        line_date1.setVisibility(View.GONE);
        textStartTime.setText(StringUtils.isEmpty(StartDate) ? "请选择" : StartDate);
        textEndTime.setText(StringUtils.isEmpty(EndDate) ? "请选择" : EndDate);
        switch (screenStatue) {
            case "0":
                rbYesterday.setChecked(true);
                break;
            case "1":
                rbDay.setChecked(true);
                break;
            case "2":
                rbMonth.setChecked(true);
                break;
            case "3":
                rbWeek.setChecked(true);
                break;
            case "4":
                rbThirty.setChecked(true);
                break;
            case "5":
                rbCustom.setChecked(true);
                rlStartTime.setVisibility(View.VISIBLE);
                rlEndTime.setVisibility(View.VISIBLE);
                line_date.setVisibility(View.VISIBLE);
                line_date1.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeft:
                dismiss();
                break;
            case R.id.rbDay:
            case R.id.rbYesterday:
            case R.id.rbMonth:
            case R.id.rbWeek:
            case R.id.rbThirty:
                rlStartTime.setVisibility(View.GONE);
                rlEndTime.setVisibility(View.GONE);
                line_date.setVisibility(View.GONE);
                line_date1.setVisibility(View.GONE);
                break;
            case R.id.rbCustom:
                rlStartTime.setVisibility(View.VISIBLE);
                rlEndTime.setVisibility(View.VISIBLE);
                line_date.setVisibility(View.VISIBLE);
                line_date1.setVisibility(View.VISIBLE);
                break;
            case R.id.rlStartTime:
                flag = 1;
                String time = textStartTime.getText().toString().trim();
                if (!"请选择".equals(time) && !StringUtils.isEmpty(time)) {
                    String[] timeArray = time.split("-");
                    dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]), 180);
                } else {
                    dateDialog.show(180);
                }
                break;
            case R.id.rlEndTime:
                flag = 2;
                String time2 = textEndTime.getText().toString().trim();
                if (!"请选择".equals(time2) && !StringUtils.isEmpty(time2)) {
                    String[] timeArray = time2.split("-");
                    dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]), 180);
                } else {
                    dateDialog.show(180);
                }
                break;
            case R.id.btnScreen:
                if(rbYesterday.isChecked()) {
                    screenStatue = "0";
                } else if (rbDay.isChecked()) {
                    screenStatue = "1";
                } else if (rbMonth.isChecked()) {
                    screenStatue = "2";
                } else if (rbWeek.isChecked()) {
                    screenStatue = "3";
                } else if (rbThirty.isChecked()) {
                    screenStatue = "4";
                } else if (rbCustom.isChecked()) {
                    screenStatue = "5";
                }
                if (StringUtils.isEmpty(screenStatue)) {
                    ViewInject.toast(context, "请选择筛选条件");
                    return;
                }
                if ("5".equals(screenStatue)) {
                    String startDate = textStartTime.getText().toString().trim();
                    String endDate = textEndTime.getText().toString().trim();
                    if ("请选择".equals(startDate)) {
                        startDate = "";
                        ViewInject.toast(context, "请选择开始时间");
                        return;
                    }
                    if ("请选择".equals(endDate)) {
                        ViewInject.toast(context, "请选择结束时间");
                        endDate = "";
                        return;
                    }
                    if (!isCurrentDate(startDate, endDate)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    if (onFilterDialogClickLinster != null) {
                        onFilterDialogClickLinster.OK(screenStatue, startDate, endDate);
                    }
                } else {
                    if (onFilterDialogClickLinster != null) {
                        onFilterDialogClickLinster.OK(screenStatue, "", "");
                    }
                }
                dismiss();
                break;

        }

    }

    /**
     * 对话框回调监听器
     * screenStatue 默认选择的筛选项
     * StartDate 开始日期
     * EndDate 结束日期
     */
    public interface OnFilterDialogClickLinster {
        public void OK(String screenStatue, String StartDate, String EndDate);
    }

    public void setFilterDialogClickLinster(OnFilterDialogClickLinster onFilterDialogClickLinster) {
        this.onFilterDialogClickLinster = onFilterDialogClickLinster;
    }

    /**
     * 日期回调
     */
    SelectDateDialog.OnClickListener onDateClickListener = new SelectDateDialog.OnClickListener() {
        @Override
        public boolean onSure(int year, int month, int day, long time) {
            switch (flag) {
                case 1:
                    textStartTime.setText(dateFormat.format(time));
                    break;
                case 2:
                    textEndTime.setText(dateFormat.format(time));
                    break;
            }
            return false;
        }

        @Override
        public boolean onCancel() {
            return false;
        }
    };

    /**
     * 时间对比
     *
     * @param start
     * @param end
     * @return
     */
    private boolean isCurrentDate(String start, String end) {
        if (StringUtils.isEmpty(start)) {
            return true;
        }
        boolean current = false;
        try {
            Date dt1 = dateFormat.parse(start);
            Date dt2 = dateFormat.parse(end);
            if (dt1.getTime() > dt2.getTime()) {
                current = false;
            } else {
                current = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return current;
    }
}
