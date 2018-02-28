package com.yatang.xc.xcr.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.StockListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.ChoiceAddToStockTypeDiaolg;
import com.yatang.xc.xcr.dialog.SearchDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.yatang.xc.xcr.R.drawable.search2;
import static com.yatang.xc.xcr.R.id.imagePwdClear;

/**
 * 库存列表
 * Created by Jocerly on 2017/5/20.
 */
@ContentView(R.layout.activity_goods_list)
public class StockListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.btnAdd, click = true)
    private TextView btnAdd;
    @BindView(id = R.id.btnScan, click = true)
    private TextView btnScan;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    @BindView(id = R.id.textClassify, click = true)
    private TextView textClassify;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    @BindView(id = R.id.rlStockAllValues)
    private RelativeLayout rlStockAllValues;
    @BindView(id = R.id.textStockAllValues)
    private TextView textStockAllValues;
    @BindView(id = R.id.textSortType, click = true)
    private TextView textSortType;

    private StockListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    private SearchDialog searchDialog;
    private ChoiceAddToStockTypeDiaolg stockTypeDiaolg;
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private String classifyId = "";
    private String classifyName = "";
    private boolean isChoice = false;// true 表示搜索商品 false 标识搜索库存
    private int sortType = -1;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("库存管理");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("库存管理");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        btnAdd.setText("商品入库");
        btnAdd.setVisibility(View.VISIBLE);
        btnScan.setText("出入库记录");
        textTitle.setText("库存管理");
        btnRight.setCompoundDrawablesWithIntrinsicBounds(search2, 0, 0, 0);
        btnAdd.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_stock, 0, 0);
        btnScan.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_stock_list, 0, 0);
        rlStockAllValues.setVisibility(View.VISIBLE);

        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
    }

    @Override
    public void initData() {
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onGoodsSearchDialogClickLinster);
        stockTypeDiaolg = new ChoiceAddToStockTypeDiaolg(aty);
        stockTypeDiaolg.setOnChoiceClickListener(onChoiceClickListener);

        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new StockListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        getStockList(true, false);
        setSortType(sortType);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getStockList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getStockList(false, false);
        }
    };

    /**
     * 单条item点击回调
     */
    StockListAdapter.OnItemclickLister onItemclickLister = new StockListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }
    };

    /**
     * 获取数据
     */
    private void getStockList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ClassifyId", classifyId);
        params.put("ClassifyName", classifyName);
        params.put("SortType", sortType);
        params.put("Search", "");
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/StockList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!StringUtils.isEmpty(resultParam.mapData.get("StockAllValues"))){
                        textStockAllValues.setText("￥" + Common.formatTosepara(resultParam.mapData.get("StockAllValues"), 3, 2));
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
                            textNoData.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    //如果请求下一页数据失败 pageIndex 减一
                    if(pageIndex > 1) {
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    /**
     * 选择入库方式回调
     */
    ChoiceAddToStockTypeDiaolg.OnChoiceClickListener onChoiceClickListener = new ChoiceAddToStockTypeDiaolg.OnChoiceClickListener() {
        @Override
        public void onChoiceClick() {
            //点击搜索直接跳转到搜索页面 非搜索结果页面
            MobclickAgent.onEvent(aty, "Stock_Search");
            isChoice = true;
            searchDialog.show(1);
        }

        @Override
        public void onScanClick() {
            MobclickAgent.onEvent(aty, "Stock_Scan");
            if (SystemTool.checkSelfPermission(aty, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.CAMERA}, Constants.Permission.CAMERA);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isCheck", true);
                showActivity(aty, ScanActivity.class, bundle);
            }
        }
    };

    /**
     * 搜索回调
     */
    SearchDialog.OnSearchDialogClickLinster onGoodsSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            if(isChoice) {
                Bundle bundle = new Bundle();
                bundle.putString("msg", msg);
                bundle.putBoolean("isChoice", true);
                bundle.putInt("costType", 2);
                skipActivity(aty, GoodsResultListActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("msg", msg);
                skipActivity(aty, StockResultListActivity.class, bundle);
            }

        }
    };

    /**
     * 设置库存排序
     * @param sortType -1：默认排序（降序），0：降序，1：升序
     */
    public void setSortType(int sortType){
        switch (sortType){
            case -1:
                textSortType.setText("降序");
                textSortType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imgdown, 0, 0, 0);
                break;
            case 1:
                textSortType.setText("降序");
                textSortType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imgup, 0, 0, 0);
                break;
            case 0:
                textSortType.setText("升序");
                textSortType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imgdown, 0, 0, 0);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                isChoice = false;
                searchDialog.show(1);
                break;
            case R.id.textClassify:
                skipActivityForResult(aty, ClassifyActivity.class, Constants.ForResult.CHOICE_CLASSIFY);
                break;
            case R.id.btnAdd:
                MobclickAgent.onEvent(aty, "Stock_Storage");
                stockTypeDiaolg.show();
                break;
            case R.id.btnScan:
                MobclickAgent.onEvent(aty, "Stock_Record");
                skipActivity(aty, AddToStockListActivity.class);
                break;
            case imagePwdClear:
                textClassify.setText("当前分类：全部");
                classifyId = "";
                pageIndex = 1;
                getStockList(true, false);
                break;
            case R.id.textSortType:
                MobclickAgent.onEvent(aty, "Stock_Sort");
                if (sortType == 0) {
                    pageIndex = 1;
                    sortType = 1;
                    setSortType(sortType);
                    getStockList(true, false);
                } else {
                    pageIndex = 1;
                    sortType = 0;
                    setSortType(sortType);
                    getStockList(true, false);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.Permission.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isCheck", true);
                    showActivity(aty, ScanActivity.class, bundle);
                } else {
                    toast("获取相机权限失败，请到设置里面打开");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String msg = data.getExtras().getString("code");
                    searchDialog.updateDataBase(msg);
                    if(isChoice) {
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", msg);
                        bundle.putBoolean("isChoice", true);
                        bundle.putInt("costType", 2);
                        skipActivity(aty, GoodsResultListActivity.class, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", msg);
                        skipActivity(aty, StockResultListActivity.class, bundle);
                    }
                }
                break;
            case Constants.ForResult.CHOICE_CLASSIFY:
                if (resultCode == RESULT_OK && data != null) {
                    classifyId = data.getExtras().getString("classifyId");
                    classifyName = data.getExtras().getString("classifyName");
                    textClassify.setText("当前分类：" + classifyName);
                    pageIndex = 1;
                    getStockList(true, false);
                }
                break;
        }
    }
}
