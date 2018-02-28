package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;

import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 筛选弹框
 * Created by Jocerly on 2017/3/13.
 */

public class ScreenDialog extends Dialog implements View.OnClickListener {
    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    SimpleDateFormat dateFormat2 = new SimpleDateFormat(Constants.SimpleTimeFormat);
    private final Context context;
    private TextView btnLeft;
    private TextView textTitle;
    private TextView btnRight;

    //时间
    private RelativeLayout rlStartTime;
    private RelativeLayout rlEndTime;
    private TextView textStartTime;
    private TextView textEndTime;
    private TextView txtStatus;

    //支付方式
    private LinearLayout llPayType;
    private CheckBox cbWeixin;
    private CheckBox cbZhiFuBao;
    private CheckBox cbCash;
    private CheckBox cbElectronic;

    //订单销售单和退货单状态
    private LinearLayout llOrderStatue;
    private TextView textOrderStatue;
    private RadioButton cbTypeWait;
    private RadioButton cbTypeDeliver;
    private RadioButton cbTypeDeliverred;
    private RadioButton cbTypeFinish;
    private RadioButton cbTypeCancel;

    //类型
    private LinearLayout llsettlement;
    private CheckBox cbSettlementYes;
    private CheckBox cbSettlementNo;
    private CheckBox cbSettlementFailed;
    private TextView btnReset;
    private TextView btnScreen;
    private TextView textTimeTitle;
    private OnScreenDialogClickLinster onScreenDialogClickLinster;
    private OnOrderScreenDialogClickLinster onOrderScreenDialogClickLinster;
    private SelectDateDialog dateDialog;
    private SelectTimeDialog timeDialog;
    private int flag = 1;
    private ArrayList<ConcurrentHashMap<String, String>> listDataTmp = new ArrayList<>();
    private ArrayList<ConcurrentHashMap<String, String>> listDataPayType = new ArrayList<>();
    private String orderType = null;

    /**
     * 1、交易流水，2、结算管理，3：小票，4：结款， 5：销售单状态，6：退货单状态
     */
    private int type;

    public ScreenDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_screen);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        btnLeft = (TextView) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);
        textTitle = (TextView) findViewById(R.id.textTitle);
        btnRight = (TextView) findViewById(R.id.btnRight);
        btnRight.setVisibility(View.GONE);

        rlStartTime = (RelativeLayout) findViewById(R.id.rlStartTime);
        rlStartTime.setOnClickListener(this);
        rlEndTime = (RelativeLayout) findViewById(R.id.rlEndTime);
        rlEndTime.setOnClickListener(this);
        textStartTime = (TextView) findViewById(R.id.textStartTime);
        textEndTime = (TextView) findViewById(R.id.textEndTime);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        textTimeTitle = (TextView) findViewById(R.id.textTimeTitle);

        llPayType = (LinearLayout) findViewById(R.id.llPayType);
        cbWeixin = (CheckBox) findViewById(R.id.cbWeixin);
        cbZhiFuBao = (CheckBox) findViewById(R.id.cbZhiFuBao);
        cbCash = (CheckBox) findViewById(R.id.cbCash);
        cbElectronic = (CheckBox) findViewById(R.id.cbElectronic);

        llsettlement = (LinearLayout) findViewById(R.id.llsettlement);
        cbSettlementYes = (CheckBox) findViewById(R.id.cbSettlementYes);
        cbSettlementNo = (CheckBox) findViewById(R.id.cbSettlementNo);
        cbSettlementFailed = (CheckBox) findViewById(R.id.cbSettlementFailed);

        llOrderStatue = (LinearLayout) findViewById(R.id.llOrderStatue);
        textOrderStatue = (TextView) findViewById(R.id.textOrderStatue);
        cbTypeWait = (RadioButton) findViewById(R.id.cbTypeWait);
        cbTypeDeliver = (RadioButton) findViewById(R.id.cbTypeDeliver);
        cbTypeDeliverred = (RadioButton) findViewById(R.id.cbTypeDeliverred);
        cbTypeFinish = (RadioButton) findViewById(R.id.cbTypeFinish);
        cbTypeCancel = (RadioButton) findViewById(R.id.cbTypeCancel);

        btnReset = (TextView) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnScreen = (TextView) findViewById(R.id.btnScreen);
        btnScreen.setOnClickListener(this);

        dateDialog = new SelectDateDialog(context);
        dateDialog.setOnClickListener(onDateClickListener);
        timeDialog = new SelectTimeDialog(context);
        timeDialog.setOnUpdateTimeListener(onTimeClickListener);
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
     * 时间回调
     */
    SelectTimeDialog.OnClickListener onTimeClickListener = new SelectTimeDialog.OnClickListener() {
        @Override
        public boolean onSure(int hour, int minute, long time) {
            switch (flag) {
                case 1:
                    textStartTime.setText(dateFormat2.format(time));
                    break;
                case 2:
                    textEndTime.setText(dateFormat2.format(time));
                    break;
            }
            return false;
        }

        @Override
        public boolean onCancel() {
            return false;
        }
    };

    public void show(int type) {
        show(type, "");
    }


    /**
     * 显示
     *
     * @param type：1、交易流水，2、结算管理，3：小票，4：结款，5：销售单状态，6：退货单状态
     */
    public void show(int type, String startTime, String endTime) {
        super.show();
        textTitle.setText("筛选");
        if (!StringUtils.isEmpty(startTime)) {
            textStartTime.setText(startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            textEndTime.setText(endTime);
        } else if (type != 3) {
            textEndTime.setText(dateFormat.format(new Date().getTime()));
        }
        this.type = type;
        llPayType.setVisibility(View.GONE);
        switch (type) {
            case 1:
                llsettlement.setVisibility(View.GONE);
                break;
            case 2:
                llsettlement.setVisibility(View.VISIBLE);
                textTimeTitle.setText("结算单时间");
                break;
            case 3:
                llPayType.setVisibility(View.VISIBLE);
                llsettlement.setVisibility(View.VISIBLE);
                cbSettlementYes.setText("销售");
                cbSettlementNo.setText("退货");
                txtStatus.setText("单据类型");
                cbSettlementFailed.setVisibility(View.GONE);
                break;
            case 4:
                llsettlement.setVisibility(View.VISIBLE);
                textTimeTitle.setText("结款状态");
                cbSettlementYes.setText("未结款");
                cbSettlementNo.setText("已结款");
                cbSettlementFailed.setText("支付中");
                break;
            case 5:
                llsettlement.setVisibility(View.GONE);
                llOrderStatue.setVisibility(View.VISIBLE);
                cbTypeFinish.setVisibility(View.VISIBLE);
                textOrderStatue.setText("销售单状态");
                cbTypeWait.setText("待接单");
                cbTypeDeliver.setText("待发货");
                cbTypeDeliverred.setText("已发货");
                cbTypeCancel.setText("已取消");
                cbTypeFinish.setText("已完成");
                textTimeTitle.setText("下单时间");
                break;
            case 6:
                llsettlement.setVisibility(View.GONE);
                llOrderStatue.setVisibility(View.VISIBLE);
                textOrderStatue.setText("退货单状态");
                cbTypeWait.setText("待审核");
                cbTypeDeliver.setText("已退款");
                cbTypeDeliverred.setText("已取消");
                cbTypeCancel.setText("已拒绝");
                cbTypeFinish.setVisibility(View.GONE);
                textTimeTitle.setText("申请时间");
                break;
        }
    }

    /**
     * 显示
     *
     * @param type：1、交易流水，2、结算管理，3：小票, 4:批量结款, 5:销售单状态, 6:退货单状态
     */
    public void show(int type, String startTime) {
        show(type, startTime, "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                dismiss();
                break;
            case R.id.rlStartTime:
                flag = 1;
                String time = textStartTime.getText().toString().trim();
                if (type == 3) {
                    if (!"请选择".equals(time)) {
                        String[] timeArray = time.split(":");
                        timeDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]));
                    } else {
                        timeDialog.show();
                    }
                } else {
                    if (!"请选择".equals(time)) {
                        String[] timeArray = time.split("-");
                        dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]));
                    } else {
                        dateDialog.show();
                    }
                }
                break;
            case R.id.rlEndTime:
                flag = 2;
                String time2 = textEndTime.getText().toString().trim();
                if (type == 3) {
                    if (!"请选择".equals(time2)) {
                        String[] timeArray = time2.split(":");
                        timeDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]));
                    } else {
                        timeDialog.show();
                    }
                } else {
                    if (!"请选择".equals(time2)) {
                        String[] timeArray = time2.split("-");
                        dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]));
                    } else {
                        dateDialog.show();
                    }
                }
                break;
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnScreen:
                String startTime = textStartTime.getText().toString().trim();
                String endTime = textEndTime.getText().toString().trim();
                if (type == 1) {//1、交易流水，2、结算管理，3：小票，5、销售单状态，6、退货单状态
                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentDate(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }

                    dismiss();
                    onScreenDialogClickLinster.OK(startTime, endTime, listDataTmp, null);
                } else if (type == 2) {//可单一条件筛选
                    //结算状态-多选：0：未到账，1：结算完成，2：全部
                    listDataTmp.clear();
                    ConcurrentHashMap<String, String> map = null;
                    if (cbSettlementYes.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("SettlementType", "2");
                        listDataTmp.add(map);
                    }
                    if (cbSettlementNo.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("SettlementType", "0");
                        listDataTmp.add(map);
                    }
                    if (cbSettlementFailed.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("SettlementType", "3");
                        listDataTmp.add(map);
                    }
                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentDate(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    dismiss();
                    onScreenDialogClickLinster.OK(startTime, endTime, listDataTmp, null);
                } else if (type == 3) {//可单一条件筛选
                    //结算状态-多选：1：销售、3：退货
                    listDataTmp.clear();
                    listDataPayType.clear();
                    ConcurrentHashMap<String, String> map = null;
                    if (cbSettlementYes.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("TicketType", "1");
                        listDataTmp.add(map);
                    }
                    if (cbSettlementNo.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("TicketType", "3");
                        listDataTmp.add(map);
                    }

                    if (cbWeixin.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("PayType", "2203");
                        listDataPayType.add(map);
                    }
                    if (cbZhiFuBao.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("PayType", "2210");
                        listDataPayType.add(map);
                    }
                    if (cbCash.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("PayType", "1");
                        listDataPayType.add(map);
                    }
                    if (cbElectronic.isChecked()) {
                        map = new ConcurrentHashMap<>();
                        map.put("PayType", "3");
                        listDataPayType.add(map);
                    }
                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentTime(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    dismiss();
                    onScreenDialogClickLinster.OK(startTime, endTime, listDataTmp, listDataPayType);
                } else if (type == 4) {//结款
                    //可单一条件筛选
                    //结算状态-多选：0：销售、1：退货、2：换货
                    listDataTmp.clear();
                    if (cbSettlementYes.isChecked()) {
                        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                        map.put("ForPaymentType", "0");
                        listDataTmp.add(map);
                    }
                    if (cbSettlementNo.isChecked()) {
                        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                        map.put("ForPaymentType", "1");
                        listDataTmp.add(map);
                    }
                    if (cbSettlementFailed.isChecked()) {
                        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                        map.put("ForPaymentType", "2");
                        listDataTmp.add(map);
                    }
//                    }
                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentDate(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    dismiss();
                    onScreenDialogClickLinster.OK(startTime, endTime, listDataTmp, null);

                } else if (type == 5){
                    //销售状态
                    //2：待接单、 3：待发货、31：已发货、 4：已完成、30：已取消
                    if (cbTypeWait.isChecked()){
                       orderType = "2";
                    }else if (cbTypeDeliver.isChecked()){
                       orderType = "3";
                    }else if (cbTypeDeliverred.isChecked()){
                       orderType = "31";
                    }else if (cbTypeFinish.isChecked()){
                       orderType = "4";
                    }else if (cbTypeCancel.isChecked()){
                       orderType = "30";
                    }else {
                        orderType = "0";
                    }
                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentDate(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    dismiss();
                    onOrderScreenDialogClickLinster.OK(startTime, endTime, orderType, type);
                } else if (type == 6){
                    //退货单状态
                    //201：待审核、203：已退货、205：已取消、：204已拒绝
                    if (cbTypeWait.isChecked()){
                       orderType = "201";
                    }else if (cbTypeDeliver.isChecked()){
                        orderType = "203";
                    }else if (cbTypeDeliverred.isChecked()){
                        orderType = "205";
                    }else if (cbTypeCancel.isChecked()){
                        orderType = "204";
                    }else {
                        orderType = "0";
                    }

                    if ("请选择".equals(startTime)) {
                        startTime = "";
                    }
                    if ("请选择".equals(endTime)) {
                        endTime = "";
                    }
                    if (!isCurrentDate(startTime, endTime)) {
                        ViewInject.toast(context, "开始时间不能大于结束时间");
                        return;
                    }
                    dismiss();
                    onOrderScreenDialogClickLinster.OK(startTime, endTime, orderType, type);
                }
                break;
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

    /**
     * 时间对比
     *
     * @param start
     * @param end
     * @return
     */
    private boolean isCurrentTime(String start, String end) {
        if (StringUtils.isEmpty(start) || StringUtils.isEmpty(end)) {
            return true;
        }
        boolean current = false;
        try {
            Date dt1 = dateFormat2.parse(start);
            Date dt2 = dateFormat2.parse(end);
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

    public void reset() {
        textStartTime.setText("请选择");
        if (type == 3) {
            textEndTime.setText("请选择");
        } else {
            textEndTime.setText(dateFormat.format(new Date().getTime()));
        }
        cbSettlementYes.setChecked(false);
        cbSettlementNo.setChecked(false);
        cbSettlementFailed.setChecked(false);

        cbWeixin.setChecked(false);
        cbZhiFuBao.setChecked(false);
        cbCash.setChecked(false);
        cbElectronic.setChecked(false);

        cbTypeWait.setChecked(false);
        cbTypeDeliver.setChecked(false);
        cbTypeDeliverred.setChecked(false);
        cbTypeCancel.setChecked(false);
        cbTypeFinish.setChecked(false);
    }

    public interface OnOrderScreenDialogClickLinster{
        public void OK(String startTime, String endTime,String orderType, int type );
    }

    public void setOnOrderScreenDialogClickLinster(OnOrderScreenDialogClickLinster onOrderScreenDialogClickLinster) {
        this.onOrderScreenDialogClickLinster = onOrderScreenDialogClickLinster;
    }

    public interface OnScreenDialogClickLinster {
        public void OK(String startTime, String endTime, ArrayList<ConcurrentHashMap<String, String>> listDataTmp, ArrayList<ConcurrentHashMap<String, String>> listDataTmp2);
    }

    public void setOnScreenDialogClickLinster(OnScreenDialogClickLinster onScreenDialogClickLinster) {
        this.onScreenDialogClickLinster = onScreenDialogClickLinster;
    }
}
