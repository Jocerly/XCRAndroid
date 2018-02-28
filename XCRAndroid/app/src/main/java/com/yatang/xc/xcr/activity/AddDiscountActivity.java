package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.AddCoupAdapter;
import com.yatang.xc.xcr.adapter.DiscountGoodsAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.SelectDateDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建折扣活动
 * Created by dengjiang on 2017/10/16.
 */
@ContentView(R.layout.activity_adddiscount)
public class AddDiscountActivity extends BaseActivity {
    public static final int CODE_SELECT_GOOD = 1;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.EventName)
    private EditText EventName;
    @BindView(id = R.id.StartTime, click = true)
    private TextView StartTime;
    @BindView(id = R.id.EndTime, click = true)
    private TextView EndTime;
    @BindView(id = R.id.textAdd, click = true)
    private TextView textAdd;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    private SelectDateDialog dateDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
    private List<ConcurrentHashMap<String, String>> listdata;
    private ArrayList<String> list_GoodsCode;
    private int flag = 1;
    private NomalDialog dialog;
    private DiscountGoodsAdapter adapter;

    @Override
    public void initWidget() {
        textTitle.setText("商品折扣");
        btnRight.setText("保存");
    }

    @Override
    public void initData() {
        dateDialog = new SelectDateDialog(aty, new Date());
        dateDialog.setOnClickListener(onDateClickListener);
        listdata = new ArrayList<>();
        list_GoodsCode = new ArrayList<>();
        adapter = new DiscountGoodsAdapter(aty, listdata);
        adapter.setOnDeletelistener(onItemDeleteListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("添加折扣商品");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("添加折扣商品");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                checkDiscountData();
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
                if (listdata.size() >= 20) {
                    toast("最多添加20个商品");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ListCode", list_GoodsCode);
                skipActivityForResult(aty, SelectGoodActivity.class, bundle, CODE_SELECT_GOOD);
                break;
            default:
                break;
        }
    }

    /**
     * 效验折扣活动数据
     */
    private void checkDiscountData() {
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

        if (listdata.size() <= 0) {
            toast("请选择商品");
            return;
        }
        params.put("GoodsList", new JSONArray(listdata));
        saveDiscountInfo();
    }

    /**
     * 向服务器提交折扣活动信息
     */
    private void saveDiscountInfo() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ShopName", MyApplication.instance.StoreAbbreName);
        httpRequestService.doRequestData(aty, "User/AddSpecialPriceEvent", params, new HttpRequestService.IHttpRequestCallback() {
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
                    AddDiscountActivity.super.onBackPressed();
                }
            });
        }
        dialog.show("还未保存活动内容\n确认关闭吗？", "取消", "确认");
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

    private DiscountGoodsAdapter.OnItemDeleteListener onItemDeleteListener = new DiscountGoodsAdapter.OnItemDeleteListener() {
        @Override
        public void itemDelete(int position) {
            list_GoodsCode.remove(listdata.get(position).get("GoodsCode"));
            JCLoger.debug("list_GoodsCode=" + list_GoodsCode);
            listdata.remove(position);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_SELECT_GOOD) {
                ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                String tempValue = String.valueOf(data.getExtras().get("GoodsId"));
                map.put("GoodsId", StringUtils.isEmpty(tempValue) ? "123" : tempValue);

                tempValue = String.valueOf(data.getExtras().get("GoodsCode"));
                map.put("GoodsCode", StringUtils.isEmpty(tempValue) ? "" : tempValue);

                tempValue = String.valueOf(data.getExtras().get("GoodsName"));
                map.put("GoodsName", StringUtils.isEmpty(tempValue) ? "" : tempValue);

                tempValue = String.valueOf(data.getExtras().get("GoodsPrice"));
                map.put("Price", StringUtils.isEmpty(tempValue) ? "" : tempValue);

                tempValue = String.valueOf(data.getExtras().get("UnitName"));
                map.put("UnitName", StringUtils.isEmpty(tempValue) ? "" : tempValue);

                tempValue = String.valueOf(data.getExtras().get("GoodsPic"));
                map.put("GoodsPic", StringUtils.isEmpty(tempValue) ? "" : tempValue);

                map.put("DiscountType", String.valueOf(data.getExtras().get("Type")));
                if ("1".equals(map.get("DiscountType"))) {
                    //特价
                    map.put("SpecialPrice", Common.formatFloat(String.valueOf(data.getExtras().get("TypeValue"))));
                    map.put("Discount", "");
                } else if ("2".equals(map.get("DiscountType"))) {
                    //折扣
                    map.put("Discount", String.valueOf(data.getExtras().get("TypeValue")));
                    float oldPrice = Float.parseFloat(map.get("Price"));
                    float discount = Float.parseFloat(map.get("Discount"));
                    discount /= 10;
                    float lastPrice = oldPrice * discount;
                    JCLoger.debug("oldPrice= " + oldPrice);
                    JCLoger.debug("discount= " + discount);
                    JCLoger.debug("lastPrice= " + lastPrice);
                    map.put("SpecialPrice", Common.formatFloat(lastPrice + ""));
                }
                map.put("LimitCount", String.valueOf(data.getExtras().get("RestrictionsNO")));
                map.put("TotalCount", String.valueOf(data.getExtras().get("Stock")));
                listdata.add(map);
                list_GoodsCode.add(map.get("GoodsCode"));
                JCLoger.debug("list_GoodsCode=" + list_GoodsCode);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
