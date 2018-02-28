package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.views.WheelStyle;
import com.yatang.xc.xcr.views.WheelView;

import java.util.Calendar;
import java.util.Date;


/**
 * 时间选择对话框
 */
public class SelectTimeDialog extends Dialog {
    private Context context;
    private WheelView leftWheel;
    private WheelView rightWheel;

    private OnClickListener onClickListener;

    boolean cancelable = true;

    /**
     * 创建一个时间选择对话框
     *
     * @param context
     */
    public SelectTimeDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wheel_select_time);
        leftWheel = (WheelView) findViewById(R.id.select_time_wheel_left);
        rightWheel = (WheelView) findViewById(R.id.select_time_wheel_right);
        leftWheel.setWheelStyle(WheelStyle.STYLE_HOUR);
        rightWheel.setWheelStyle(WheelStyle.STYLE_MINUTE);

        setCancelable(cancelable);

        Button cancelBtn = (Button) findViewById(R.id.select_date_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    if (!onClickListener.onCancel()) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
        Button sureBtn = (Button) findViewById(R.id.select_date_sure);
        sureBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, leftWheel.getCurrentItem());
                calendar.set(Calendar.MINUTE, rightWheel.getCurrentItem());
                long setTime = calendar.getTimeInMillis();

                if (onClickListener != null) {
                    if (!onClickListener.onSure(leftWheel.getCurrentItem(), rightWheel.getCurrentItem(), setTime)) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
    }

    /**
     * 显示选择时间对话框
     *
     * @param mHour   默认显示的小时
     * @param mMinute 默认小时的分钟
     */
    public void show(int mHour, int mMinute) {
        super.show();
        leftWheel.setCurrentItem(mHour);
        rightWheel.setCurrentItem(mMinute);
    }

    public void show() {
        super.show();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        leftWheel.setCurrentItem(hour);
        rightWheel.setCurrentItem(minute);
    }

    /**
     * 选择时间对话框回调
     *
     * @param listener
     */
    public void setOnUpdateTimeListener(OnClickListener listener) {
        onClickListener = listener;
    }

    /**
     * 选择时间对话框回调接口，调用者实现
     */
    public interface OnClickListener {
        boolean onSure(int hour, int minute, long time);

        boolean onCancel();
    }
}
