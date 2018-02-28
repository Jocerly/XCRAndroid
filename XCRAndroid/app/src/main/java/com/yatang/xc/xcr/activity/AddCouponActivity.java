package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.AddCoupAdapter;
import com.yatang.xc.xcr.adapter.AddcouponAdapter1;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.SelectDateDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.CheckSwitchButton;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.listView.CustomerListView;
import org.jocerly.jcannotation.widget.recyclevew.FullyLinearLayoutManager;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建优惠券活动页面
 * Created by dengjiang on 2017/10/13.
 */
@ContentView(R.layout.activity_addcoupon)
public class AddCouponActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    public static final int CREATE_COUPON_CODE = 1;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.scrollview)
    private ScrollView scrollview;
    @BindView(id = R.id.EventName)
    private EditText EventName;
    @BindView(id = R.id.StartTime, click = true)
    private TextView StartTime;
    @BindView(id = R.id.EndTime, click = true)
    private TextView EndTime;
    @BindView(id = R.id.TotalCount)
    private EditText TotalCount;
    @BindView(id = R.id.rbOne)
    private RadioButton rbOne;
    @BindView(id = R.id.rbEveryDay)
    private RadioButton rbEveryDay;
    @BindView(id = R.id.IsNewUserCanUse)
    private CheckSwitchButton IsNewUserCanUse;
    @BindView(id = R.id.textAdd, click = true)
    private PressTextView textAdd;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    @BindView(id = R.id.recyclerView)
    private CustomerListView recyclerView;
    private AddcouponAdapter1 adapter;
    private SelectDateDialog dateDialog;
    private List<ConcurrentHashMap<String, String>> list_Coupon;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    private int flag = 1;
    private NomalDialog dialog;

    @Override
    public void initWidget() {
        textTitle.setText("优惠券");
        btnRight.setText("保存");
        list_Coupon = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("优惠券");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("优惠券");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initData() {
        list_Coupon = new ArrayList<>();
        adapter = new AddcouponAdapter1(aty, list_Coupon);
        adapter.setOnDeletelistener(onDeletelistener);
        recyclerView.setAdapter(adapter);
        dateDialog = new SelectDateDialog(aty, new Date());
        dateDialog.setOnClickListener(onDateClickListener);
        rbOne.setOnCheckedChangeListener(this);
        rbEveryDay.setOnCheckedChangeListener(this);
        IsNewUserCanUse.setOnCheckedChangeListener(this);
        IsNewUserCanUse.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.StartTime:
                flag = 1;
                String time1 = StartTime.getText().toString().trim();
                if (!"请选择".equals(time1)) {
                    String[] timeArray = time1.split("-");
                    dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]));
                } else {
                    dateDialog.show();
                }
                break;
            case R.id.EndTime:
                flag = 2;
                String time2 = EndTime.getText().toString().trim();
                if (!"请选择".equals(time2)) {
                    String[] timeArray = time2.split("-");
                    dateDialog.show(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) - 1, Integer.parseInt(timeArray[2]));
                } else {
                    dateDialog.show();
                }
                break;
            case R.id.textAdd:
                if (list_Coupon.size() >= 5) {
                    toast("最多只能添加5张券");
                    return;
                }
                skipActivityForResult(aty, CreateCouponActivity.class, CREATE_COUPON_CODE);
                break;
            case R.id.btnRight:
                checkInputData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    /**
     * 弹出退出确认对话框
     */
    private void showBackDialog() {
        if (dialog == null) {
            dialog = new NomalDialog(aty);
            dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                @Override
                public void onOkClick() {
                    AddCouponActivity.super.onBackPressed();
                }
            });
        }
        dialog.show("还未保存活动内容\n确认关闭吗？", "取消", "确认");
    }

    /**
     * 验证优惠券活动数据 并填充提交给服务器的参数
     */
    private void checkInputData() {
        String temp = EventName.getText().toString().trim();
        params.clear();
        if (StringUtils.isEmpty(temp)) {
            toast("请输入活动名称");
            return;
        } else if (temp.length() > 10) {
            toast("活动名称最多10个字");
            return;
        }
        params.put("EventName", temp);

        temp = StartTime.getText().toString().trim();
        if (StringUtils.isEmpty(temp) || "请选择".equals(temp)) {
            toast("请选择开始日期");
            return;
        }
        params.put("StartDate", temp);

        temp = EndTime.getText().toString().trim();
        if (StringUtils.isEmpty(temp) || "请选择".equals(temp)) {
            toast("请选择结束日期");
            return;
        }
        params.put("EndDate", temp);
        if (!Common.isCurrentDate((String) params.get("StartDate"), (String) params.get("EndDate"))) {
            toast("开始日期必须小于或等于结束日期");
            return;
        }
        temp = TotalCount.getText().toString().trim();
        if (StringUtils.isEmpty(temp)) {
            toast("请输入礼包券数量");
            return;
        } else if (Integer.parseInt(temp) <= 0) {
            toast("礼包券数量必须大于0");
            return;
        } else if (Integer.parseInt(temp) > 100000) {
            toast("礼包券数量不得大于10万");
            return;
        }
        params.put("TotalCount", temp);

        if (rbOne.isChecked()) {
            params.put("CountForEveryOne", "1");
        } else if (rbEveryDay.isChecked()) {
            params.put("CountForEveryOne", "2");
        } else {
            toast("请选择每人限领规则");
            return;
        }

        if (!IsNewUserCanUse.isChecked()) {
            params.put("IsNewUserCanUse", "1");
        } else {
            params.put("IsNewUserCanUse", "0");
        }

        if (list_Coupon.size() <= 0) {
            toast("请添加优惠券");
            return;
        } else {
            params.put("CouponList", new JSONArray(list_Coupon));
        }
        saveCouponInfo();
    }

    /**
     * 向服务器提交优惠券活动信息
     */
    private void saveCouponInfo() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ShopName", MyApplication.instance.StoreAbbreName);
        httpRequestService.doRequestData(aty, "User/AddCouponEvent", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    toast("保存成功");
                    Intent intent = new Intent();
                    intent.putExtra("StartTime", StartTime.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 日期回调
     */
    SelectDateDialog.OnClickListener onDateClickListener = new SelectDateDialog.OnClickListener() {
        @Override
        public boolean onSure(int year, int month, int day, long time) {
            switch (flag) {
                case 1:
                    StartTime.setText(dateFormat.format(time));
                    break;
                case 2:
                    EndTime.setText(dateFormat.format(time));
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
     * 删除优惠券回调
     */
    private AddcouponAdapter1.OnDeletelistener onDeletelistener = new AddcouponAdapter1.OnDeletelistener() {

        @Override
        public void onDelete(int position) {
            list_Coupon.remove(position);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CREATE_COUPON_CODE && data != null) {
                ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                map.put("CouponBalance", String.valueOf(data.getExtras().get("CouponBalance")));
                map.put("UseCondition", String.valueOf(data.getExtras().get("UseCondition")));
                map.put("Duration", String.valueOf(data.getExtras().get("Duration")));
                list_Coupon.add(map);
                adapter.notifyDataSetChanged();
                scrollview.requestLayout();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.rbOne:
                if (b) {
                    IsNewUserCanUse.setEnabled(true);
                }
                break;
            case R.id.rbEveryDay:
                if (b) {
                    IsNewUserCanUse.setEnabled(false);
                }
                break;
            case R.id.IsNewUserCanUse:
                if (!b) {
                    rbEveryDay.setEnabled(false);
                } else {
                    rbEveryDay.setEnabled(true);
                }
        }
    }
}
