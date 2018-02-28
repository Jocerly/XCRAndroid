package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.views.WheelStyle;
import com.yatang.xc.xcr.views.WheelView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期选择对话框
 */
public class SelectDateDialog extends Dialog {
    private Context context;
    private WheelView yearWheel;
    private WheelView monthWheel;
    private WheelView dayWheel;
    private Date maxdate;
    private Date earliestDate;
    private int tempDays = 0;
    int selectYear;
    int selectMonth;
    int selectDay;

    private int monthIndext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    private OnClickListener onClickListener;

    /**
     * 创建一个日期选择对话框
     *
     * @param context
     */
    public SelectDateDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    public SelectDateDialog(Context context, Date earliestDate) {
        this(context);
        this.earliestDate = earliestDate;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wheel_select_date);
        yearWheel = (WheelView) findViewById(R.id.select_date_wheel_year_wheel);
        monthWheel = (WheelView) findViewById(R.id.select_date_month_wheel);
        dayWheel = (WheelView) findViewById(R.id.select_date_day_wheel);

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
                if (monthIndext - indext > 1 && indext == 0) {
                    //从12月变成了1月分 加一年
                    yearWheel.setCurrentItem(yearWheel.getCurrentItem() + WheelStyle.MINYEAR - WheelStyle.MINYEAR + 1);
                } else if (monthIndext - indext < -1 && indext == 11) {
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
                long setTime = calendar.getTimeInMillis();

                if (onClickListener != null) {
                    if (!onClickListener.onSure(year, month, day, setTime)) {
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
        Calendar calendar = Calendar.getInstance();
        maxdate = new Date(new Date().getTime());
        calendar.setTime(maxdate);
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
        boolean onSure(int year, int month, int day, long time);

        boolean onCancel();
    }

    private void onDateChanged() {
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
        long setTime = calendar.getTimeInMillis();
        //如果有设置最早时间，增加一个最小时间的判断
        if (earliestDate != null) {
            if (!isCurrentDate(dateFormat.format(earliestDate.getTime()), dateFormat.format(setTime))) {
                calendar.setTime(earliestDate);
                int nyear = calendar.get(Calendar.YEAR);
                int nmonth = calendar.get(Calendar.MONTH);
                int nday = calendar.get(Calendar.DAY_OF_MONTH);

                dayWheel.setWheelItemList(WheelStyle.createDayString(nyear - WheelStyle.MINYEAR, month + 1));
                yearWheel.setCurrentItem(nyear - WheelStyle.MINYEAR);
                monthWheel.setCurrentItem(nmonth);
                monthIndext = month;
                dayWheel.setCurrentItem(nday - 1);
            }
            return;
        }

        //开始时间必须早于结束时间的判断
        if (!isCurrentDate(dateFormat.format(setTime), dateFormat.format(maxdate.getTime()))) {
            calendar.setTime(maxdate);
            int nyear = calendar.get(Calendar.YEAR);
            int nmonth = calendar.get(Calendar.MONTH);
            int nday = calendar.get(Calendar.DAY_OF_MONTH);

            dayWheel.setWheelItemList(WheelStyle.createDayString(nyear - WheelStyle.MINYEAR, month + 1));
            yearWheel.setCurrentItem(nyear - WheelStyle.MINYEAR);
            monthWheel.setCurrentItem(nmonth);
            monthIndext = month;
            dayWheel.setCurrentItem(nday - 1);
        }

        //时间不能提前指定天数的判断
        if (!isTempDaysOK(dateFormat.format(setTime))) {
            Date newDate = new Date(maxdate.getTime() - (long) (tempDays) * 24 * 60 * 60 * 1000);
            calendar.setTime(newDate);
            int nyear = calendar.get(Calendar.YEAR);
            int nmonth = calendar.get(Calendar.MONTH);
            int nday = calendar.get(Calendar.DAY_OF_MONTH);

            dayWheel.setWheelItemList(WheelStyle.createDayString(nyear - WheelStyle.MINYEAR, month + 1));
            yearWheel.setCurrentItem(nyear - WheelStyle.MINYEAR);
            monthWheel.setCurrentItem(nmonth);
            monthIndext = month;
            dayWheel.setCurrentItem(nday - 1);
        }
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

}
