package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.views.WheelStyle;
import com.yatang.xc.xcr.views.WheelView;

import org.jocerly.jcannotation.utils.JCLoger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 开始日期+结束日期选择对话框
 * Created by zengxiaowen on 2017/7/19.
 */
public class SelectDayTimeDialog extends Dialog {

    private Context context;
    private WheelView yearWheel;
    private WheelView monthWheel;
    private WheelView dayWheel;
    private Date maxdate;  //最大时间
    private Date mindate; //最小时间
    private int tempDays = 0;
    int selectYear;
    int selectMonth;
    int selectDay;

    private int monthIndext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    private OnClickListener onClickListener;

    private RadioButton startTime;  //开始时间
    private RadioButton endTime;  // 结束时间
    private RadioGroup starGroup;

    private Map<Integer, RadioButton> radioButtonMap;

    /**
     * 创建一个日期选择对话框
     *
     * @param context
     */
    public SelectDayTimeDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wheel_select_start_end_date);
        yearWheel = (WheelView) findViewById(R.id.select_date_wheel_year_wheel);
        monthWheel = (WheelView) findViewById(R.id.select_date_month_wheel);
        dayWheel = (WheelView) findViewById(R.id.select_date_day_wheel);
        startTime = (RadioButton) findViewById(R.id.startTime);
        endTime = (RadioButton) findViewById(R.id.endTime);
        starGroup = (RadioGroup) findViewById(R.id.starGroup);
        radioButtonMap = new HashMap<>();
        radioButtonMap.put(R.id.startTime, startTime);
        radioButtonMap.put(R.id.endTime, endTime);

        for (Integer id : radioButtonMap.keySet()) {
            radioButtonMap.get(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.startTime:
                            JCLoger.debug("设置一次最大值：" + dateFormat.format(new Date(getWheelTime())));
                            maxdate = new Date(getWheelTime());
                            if (mindate == null) {
                                return;
                            }
                            setMaxTime(mindate.getTime());
                            break;
                        case R.id.endTime:
                            JCLoger.debug("设置一次最小值：" + dateFormat.format(new Date(getWheelTime())));
                            if (((RadioButton)v).getText().toString().equals("结束时间")){
                                ((RadioButton)v).setText(dateFormat.format(maxdate));
                            }
                            mindate = new Date(getWheelTime());
                            setMaxTime(maxdate.getTime());
                            break;
                    }
                    JCLoger.debug("最大值：" + dateFormat.format(maxdate) + "---最小值：" + dateFormat.format(mindate));
                }
            });
        }

        yearWheel.setWheelStyle(WheelStyle.STYLE_YEAR);
        yearWheel.setOnSelectListener(new WheelView.onSelectListener() {
            @Override
            public void onSelect(int index, String text) {
                selectYear = index + WheelStyle.MINYEAR;
                dayWheel.setWheelItemList(WheelStyle.createDayString(selectYear, selectMonth));
                onDateChanged();
            }
        });

        monthWheel.setWheelStyle(WheelStyle.STYLE_MONTH);
        monthWheel.setOnSelectListener(new WheelView.onSelectListener() {
            @Override
            public void onSelect(int index, String text) {
                selectMonth = index + 1;
                dayWheel.setWheelItemList(WheelStyle.createDayString(selectYear, selectMonth));
                onDateChanged();
            }
        });
        monthWheel.setOnIndextChangeListenner(new WheelView.onIndextChangedListenner() {
            @Override
            public void onChange(int indext) {
                indext = indext + 1;
                if (monthIndext - indext > 1 && indext == 1) {
                    //从12月变成了1月分 加一年
                    yearWheel.setCurrentItem(yearWheel.getCurrentItem() + WheelStyle.MINYEAR - WheelStyle.MINYEAR + 1);
                } else if (monthIndext - indext < -1 && indext == 12) {
                    //从1月份变成了12月份 减一年
                    yearWheel.setCurrentItem(yearWheel.getCurrentItem() + WheelStyle.MINYEAR - WheelStyle.MINYEAR - 1);
                }
                monthIndext = indext;
            }
        });
        dayWheel.setOnSelectListener(new WheelView.onSelectListener() {
            @Override
            public void onSelect(int index, String text) {
                selectDay = index + 1;
                onDateChanged();
            }
        });
        Button cancelBt = (Button) findViewById(R.id.select_date_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {

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
        Button sureBt = (Button) findViewById(R.id.select_date_sure);
        sureBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (onClickListener != null) {
                    if (!onClickListener.onSure(startTime.getText().toString(),endTime.getText().toString())) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
    }

    /**
     * 显示选择日期对话框
     *
     * @param year  默认显示的年
     * @param month 默认月
     * @param day   默认日
     */
    public void show(int year, int month, int day) {
        super.show();
        maxdate = new Date(new Date().getTime());
        dayWheel.setWheelItemList(WheelStyle.createDayString(year - WheelStyle.MINYEAR, month + 1));
        yearWheel.setCurrentItem(year - WheelStyle.MINYEAR);
        monthWheel.setCurrentItem(month);
        monthIndext = month;
        dayWheel.setCurrentItem(day - 1);
    }


    /**
     * 显示选择日期对话框
     *
     * @param year     默认显示的年
     * @param month    默认月
     * @param day      默认日
     * @param tempDays 从当天开始计算最大时间差
     */
    public void show(int year, int month, int day, int tempDays) {
        this.tempDays = tempDays;
        this.show(year, month, day);
    }

    /**
     * 显示选择日期对话框
     */
    public void show(int tempDays) {
        this.tempDays = tempDays;
        this.show();
    }

    /**
     * 显示选择日期对话框
     */
    public void show() {
        super.show();

        maxdate = new Date();

        setMaxTime(maxdate.getTime());

        setRadioText();
    }


    /**
     * 选择日期对话框回调
     *
     * @param listener
     */
    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    /**
     * 选择日期对话框回调接口，调用者实现
     *
     * @author huangzj
     */
    public interface OnClickListener {
        boolean onSure(String startTime, String endTime);

        boolean onCancel();
    }

    /**
     * 获取当前滚轮选中日期
     *
     * @return
     */
    private long getWheelTime() {
        int year = yearWheel.getCurrentItem() + WheelStyle.MINYEAR;
        int month = monthWheel.getCurrentItem();
        int day = dayWheel.getCurrentItem() + 1;

        int daySize = dayWheel.getItemCount();
        if (day > daySize) {
            day = day - daySize;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        JCLoger.debug("getWheelTime:" + dateFormat.format(calendar.getTimeInMillis()));
        return calendar.getTimeInMillis();
    }

    private void onDateChanged() {
        long setTime = getWheelTime();

        if (startTime.isChecked()) {
            //必须小于最大时间a>max = false.
            if (!isCurrentDate(dateFormat.format(setTime), dateFormat.format(maxdate.getTime()))) {
                setMaxTime(maxdate.getTime());
            }
        }
        if (endTime.isChecked()) {
            //必须大于最小时间
            if (mindate != null && isCurrentDate(dateFormat.format(setTime), dateFormat.format(mindate.getTime()))) {
                setMaxTime(mindate.getTime());
            }
            if (!isCurrentDate(dateFormat.format(setTime), dateFormat.format(new Date()))) {
                setMaxTime(new Date().getTime());
            }
        }

        //时间不能提前指定天数的判断
        if (!isTempDaysOK(dateFormat.format(setTime))) {
            setMaxTime(maxdate.getTime() - (long) (tempDays) * 24 * 60 * 60 * 1000);
        }
        setRadioText();
    }


    /**
     * 设置日期
     *
     * @param time
     */
    private void setMaxTime(long time) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(time);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dayWheel.setWheelItemList(WheelStyle.createDayString(year - WheelStyle.MINYEAR, month + 1));
        yearWheel.setCurrentItem(year - WheelStyle.MINYEAR);
        monthWheel.setCurrentItem(month);
        monthIndext = month;
        dayWheel.setCurrentItem(day - 1);
    }

    /**
     * 时间对比
     *
     * @param date 当前选择的时间
     * @return
     */
    private boolean isTempDaysOK(String date) {
        if (tempDays <= 0) {
            return true;
        }
        Date lastDate = new Date(maxdate.getTime() - (long) tempDays * 24 * 60 * 60 * 1000);

        if (isCurrentDate(dateFormat.format(lastDate.getTime()), date)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 时间对比
     *
     * @param start
     * @param end
     * @return
     */
    private boolean isCurrentDate(String start, String end) {
        if (start.equals(end)) {
            return true;
        }

        boolean current = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(start);
            Date dt2 = df.parse(end);
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

    private void setRadioText() {
        int y = yearWheel.getCurrentItem() + WheelStyle.MINYEAR;
        int m = monthWheel.getCurrentItem() + 1;
        int d = dayWheel.getCurrentItem() + 1;

        radioButtonMap.get(starGroup.getCheckedRadioButtonId()).setText(y + "-" + m + "-" + d);
    }

}
