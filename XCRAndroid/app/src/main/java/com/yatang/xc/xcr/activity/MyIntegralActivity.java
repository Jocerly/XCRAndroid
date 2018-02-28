package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.MyIntegralAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 我的积分
 * Created by dengjiang on 2017/11/6.
 */
@ContentView(R.layout.activity_my_integral)
public class MyIntegralActivity extends BaseActivity {
    public static final int MAX_LEVEL = 10;
    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.textThisMonthIntegral)
    private TextView textThisMonthIntegral;
    @BindView(id = R.id.textActNextMonthGrade)
    private TextView textActNextMonthGrade;
    @BindView(id = R.id.PreNextGrade)
    private TextView PreNextGrade;
    @BindView(id = R.id.recyclerViewIntegral)
    private LoadMoreRecyclerView recyclerViewIntegral;
    @BindView(id = R.id.linIntegral)
    private LinearLayout linIntegral;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private int pageIndex = 1;
    private MyIntegralAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private int currentLevel = 1;

    @Override
    public void initWidget() {
        textTitle.setText(R.string.msg);
        btnRight.setVisibility(View.GONE);
        textTitle.setText("我的积分");
        int color = getResources().getColor(R.color.vipback_start);
        rlTitle.setBackgroundColor(color);
        setWindowColor(color);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentLevel = bundle.getInt("CurrentLevel", 1);
        }
        pageIndex = 1;
        adapter = new MyIntegralAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewIntegral.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerViewIntegral.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1), colorGap);
        recyclerViewIntegral.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        recyclerViewIntegral.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        getIntegralData(true, false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }

    /**
     * 获取我的特权列表数据
     */
    private void getIntegralData(final boolean isShowDialog, final boolean isShowToast) {
        {
            params.clear();
            params.put("UserId", MyApplication.instance.UserId);
            params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
            params.put("Token", MyApplication.instance.Token);
            params.put("PageIndex", pageIndex);
            params.put("PageSize", Constants.PageSize);
            httpRequestService.doRequestData(aty, "User/IntegralInfo", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        textThisMonthIntegral.setText(resultParam.mapData.get("ThisMonthIntegral"));
                        String nextMonthGrade = resultParam.mapData.get("ActNextMonthGrade");
                        textActNextMonthGrade.setText("V" + nextMonthGrade);
                        String pNextGrade = resultParam.mapData.get("PreNextGrade");
                        if (!nextMonthGrade.equals(pNextGrade)) {
                            String preNextGrade = "V" + resultParam.mapData.get("PreNextGrade");
                            String preGradeNeedGral = resultParam.mapData.get("PreGradeNeedGral");
                            String upgradeMSG = "本月再获得 " + preGradeNeedGral + " 积分，" + "预计下月可获得 " + preNextGrade + " 等级";
                            PreNextGrade.setText(getSpannableString(upgradeMSG, preNextGrade, R.color.vip_yellow));
                        } else {
                            PreNextGrade.setText("您下月的有效积分已达到最高等级标准");
                        }

                        if (pageIndex > 1 && resultParam.listData.size() < 1) {//没有更多数据了
                            pageIndex--;
                            //没有更多数据了
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                        } else {
                            if (pageIndex == 1) {
                                if (isShowToast) {
                                    toast("刷新成功");
                                }
                                listData.clear();
                                adapter.setLoadingDefualt();
                            }
                            listData.addAll(resultParam.listData);
                            if (listData.size() > 0) {
                                adapter.notifyDataSetChanged();
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                                if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                                }
                                linIntegral.setVisibility(View.VISIBLE);
                                textNoData.setVisibility(View.GONE);
                            } else {
                                linIntegral.setVisibility(View.GONE);
                                textNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (Constants.M01.equals(resultParam.resultId)) {
                        toast(R.string.accout_out);
                        doEmpLoginOut();
                    } else {
                        //如果请求下一页数据失败 pageIndex 减一
                        if (pageIndex > 1) {
                            pageIndex = pageIndex--;
                        }
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                    }
                }
            });
        }

    }

    MyIntegralAdapter.OnItemClickListener onItemClickListener = new MyIntegralAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String url, String title) {
        }

        @Override
        public void OnFooterClick() {
            //加载更多
            recyclerViewIntegral.startLoadMore();
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (adapter.getLoadState() != BaseRecyclerViewAdapter.STATE_FAILURE) {
                pageIndex++;
            }
            getIntegralData(false, false);
        }
    };

    /**
     * 设置 string 部分特殊颜色显示
     */
    private SpannableString getSpannableString(String str, String spStgr, int color) {
        SpannableString s = new SpannableString(str);
        try {
            if (str.contains(spStgr)) {
                int start = str.indexOf(spStgr);
                int end = start + spStgr.length();
                s.setSpan(new ForegroundColorSpan(getResources().getColor(color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
