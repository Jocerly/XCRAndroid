package com.yatang.xc.xcr.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.GoodsListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.SearchDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.SystemTool;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品列表
 * Created by Jocerly on 2017/5/20.
 */
@ContentView(R.layout.activity_goods_list)
public class GoodsListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    @BindView(id = R.id.textClassify, click = true)
    private TextView textClassify;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    //    @BindView(id = R.id.btnAdd, click = true)
//    private TextView btnAdd;
    @BindView(id = R.id.btnScan, click = true)
    private TextView btnScan;

    private GoodsListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    private SearchDialog searchDialog;
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private String classifyId = "";
    private String classifyName = "";
    private int costType = 1;//1：编辑商品、2：商品入库 3,选择商品

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("门店商品");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("门店商品");
        btnRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search2, 0, 0, 0);

        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器

        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new GoodsListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        getGoodsList(true, false);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            costType = bundle.getInt("costType", 1);
            if (costType == 3) {
                textTitle.setText("选择商品");
                btnScan.setVisibility(View.GONE);
            }
        }
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getGoodsList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getGoodsList(false, false);
        }
    };

    /**
     * 单选回调
     */
    GoodsListAdapter.OnItemclickLister onItemclickLister = new GoodsListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }

        @Override
        public void OnItemClick(ConcurrentHashMap<String, String> map) {
            if (costType == 3) {
                Intent intent = new Intent();
                intent.putExtra("GoodsId", map.get("GoodsId"));
                intent.putExtra("GoodsCode", map.get("GoodsCode"));
                intent.putExtra("GoodsPrice", map.get("GoodsPrice"));
                intent.putExtra("GoodsName", map.get("GoodsName"));
                intent.putExtra("UnitName", map.get("UnitName"));
                JCLoger.debug("GoodsId="+map.get("GoodsId"));
                JCLoger.debug("GoodsCode="+map.get("GoodsCode"));
                JCLoger.debug("GoodsPrice="+map.get("GoodsPrice"));
                JCLoger.debug("GoodsName="+map.get("GoodsName"));
                JCLoger.debug("UnitName="+map.get("UnitName"));
                setResult(RESULT_OK, intent);
                finish();
            } else {
                queryGoodsDetial(map.get("GoodsCode"), false);
            }
        }
    };

    /**
     * 查询商品内容
     *
     * @param code
     */
    private void queryGoodsDetial(final String code, final boolean isChoice) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", code);
        params.put("CostType", costType);
        httpRequestService.doRequestData(aty, "User/GoodsDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    //门店中有此商品
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mapData", resultParam.mapData);
                    if (isChoice) {
                        skipActivity(aty, AddToStockActivity.class, bundle);
                        finish();
                    } else {
                        skipActivityForResult(aty, EditGoodsActivity.class, bundle, Constants.ForResult.EDIT_GOODS);
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast("账号过期重新登录");
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 获取数据
     */
    private void getGoodsList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ClassifyId", classifyId);
        params.put("ClassifyName", classifyName);
        params.put("Search", "");
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/GoodsList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
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
                    if (pageIndex > 1) {
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    /**
     * 搜索回调
     */
    SearchDialog.OnSearchDialogClickLinster onSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg);
            bundle.putInt("costType", costType);
            skipActivityForResult(aty, GoodsResultListActivity.class, bundle, SelectGoodActivity.SELECT_GOOD_CODE);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                MobclickAgent.onEvent(aty, "Goods_Search");
                searchDialog.show(1);
                break;
            case R.id.textClassify:
                skipActivityForResult(aty, ClassifyActivity.class, Constants.ForResult.CHOICE_CLASSIFY);
                break;
//            case R.id.btnAdd:
//                skipActivityForResult(aty, AddGoodsActivity.class, Constants.ForResult.ADD_GOODS);
//                break;
            case R.id.btnScan:
                MobclickAgent.onEvent(aty, "Goods_Scan");
                if (SystemTool.checkSelfPermission(aty, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.CAMERA}, Constants.Permission.CAMERA_SCAN);
                } else {
                    skipActivityForResult(aty, ScanCodeActivity.class, Constants.ForResult.SCAN_CODE);
                }
                break;
            case R.id.imagePwdClear:
                textClassify.setText("当前分类：全部");
                classifyId = "";
                pageIndex = 1;
                getGoodsList(true, false);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.Permission.CAMERA_SCAN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    skipActivityForResult(aty, ScanCodeActivity.class, Constants.ForResult.SCAN_CODE);
                } else {
                    toast("需要此权限才能打开相机，请到设置里面打开");
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String msg = data.getExtras().getString("code");
                    searchDialog.updateDataBase(msg);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", msg);
                    bundle.putInt("costType", costType);
                    skipActivityForResult(aty, GoodsResultListActivity.class, bundle, SelectGoodActivity.SELECT_GOOD_CODE);
                }
                break;
            case SelectGoodActivity.SELECT_GOOD_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case Constants.ForResult.SCAN_CODE:
            case Constants.ForResult.ADD_GOODS:
            case Constants.ForResult.EDIT_GOODS:
                if (resultCode == RESULT_OK) {
                    pageIndex = 1;
                    toast("保存成功");
                    getGoodsList(true, false);
                }
                break;
            case Constants.ForResult.CHOICE_CLASSIFY:
                if (resultCode == RESULT_OK && data != null) {
                    classifyId = data.getExtras().getString("classifyId");
                    classifyName = data.getExtras().getString("classifyName");
                    textClassify.setText("当前分类：" + classifyName);
                    pageIndex = 1;
                    getGoodsList(true, false);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("门店商品");
        MobclickAgent.onResume(aty);
        getGoodsList(true, false);
    }
}
