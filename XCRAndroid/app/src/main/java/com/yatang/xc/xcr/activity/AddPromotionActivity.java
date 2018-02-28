package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.AddPromotionAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.entity.PromotionEntity;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增活动选择页面
 * Created by dengjiang on 2017/10/11.
 */
@ContentView(R.layout.activity_addpromotion)
public class AddPromotionActivity extends BaseActivity {
    public static final int CODE_ADDCOUPON = 1;
    public static final int CODE_ADDDISCOUNT = 2;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;

    private AddPromotionAdapter adapter;
    private List<PromotionEntity> listData;

    @Override
    public void initWidget() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("添加活动");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("添加活动");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initData() {
        initDataList();
        textTitle.setText("添加活动");
        adapter = new AddPromotionAdapter(aty, listData);
        adapter.setOnAddPromotionClickListenner(onAddPromotionClickListenner);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化两种活动的数据
     */
    private void initDataList() {
        listData = new ArrayList<>();
        listData.add(new PromotionEntity("0", "优惠券", "发放店铺优惠券让客户领取"));
        listData.add(new PromotionEntity("1", "商品折扣", "设置最佳优惠价格或折扣吸引顾客下单"));
    }

    private AddPromotionAdapter.OnAddPromotionClickListenner onAddPromotionClickListenner = new AddPromotionAdapter.OnAddPromotionClickListenner() {

        @Override
        public void onAddPromotion(String type) {
            switch (type) {
                case "0":
                    checkCanAddCoup();
                    break;
                case "1":
                    checkCanAddDiscount();
                    break;
            }
        }
    };

    private void checkCanAddDiscount() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/CheckCanAddSpecialPriceEvent", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    String result = resultParam.mapData.get("CanAddEvent");
                    if ("0".equals(result)) {
                        toast("只可添加一个折扣活动!");
                    }
                    if ("1".equals(result)) {
                        skipActivityForResult(aty, AddDiscountActivity.class,CODE_ADDDISCOUNT);
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    private void checkCanAddCoup() {
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/CheckCanAddCouponEvent", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    String result = resultParam.mapData.get("CanAddEvent");
                    if ("0".equals(result)) {
                        toast("只可添加一个优惠券活动!");
                    }
                    if ("1".equals(result)) {
                        skipActivityForResult(aty, AddCouponActivity.class,CODE_ADDCOUPON);
                    }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == CODE_ADDCOUPON || requestCode == CODE_ADDDISCOUNT) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
