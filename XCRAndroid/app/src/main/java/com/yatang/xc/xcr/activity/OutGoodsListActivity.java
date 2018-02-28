package com.yatang.xc.xcr.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.OutGoodsListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.SearchDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外送商品管理
 * Created by lusha on 2017/07/17.
 */
@ContentView(R.layout.activity_out_list)
public class OutGoodsListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.rbFrameType, click = true)
    private RadioButton rbFrameType;
    @BindView(id = R.id.rbWaitFrameType, click = true)
    private RadioButton rbWaitFrameType;
    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    @BindView(id = R.id.textClassify, click = true)
    private TextView textClassify;
    @BindView(id = R.id.btnAll, click = true)
    private TextView btnAll;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    @BindView(id = R.id.textBatch, click = true)
    private TextView textBatch;
    @BindView(id = R.id.rlFrameType)
    private RelativeLayout rlFrameType;
    @BindView(id = R.id.textNum)
    private TextView textNum;
    @BindView(id = R.id.btnClose, click = true)
    private TextView btnClose;
    @BindView(id = R.id.btnFrameType, click = true)
    private TextView btnFrameType;
    @BindView(id = R.id.llFrameType)
    private LinearLayout llFrameType;
    @BindView(id = R.id.line)
    private View line;
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private String classifyId = "";
    private int costType = 0;
    private ArrayList<String> list_GoodsCode;
    /**
     * 1：上架，0：下架
     */
    private int frameType = 1;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private SearchDialog dialog;
    private OutGoodsListAdapter adapter;
    private NomalDialog nomalDialog;
    private NomalDialog modifyAllDialog;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("外送商品");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("外送商品");
        btnRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search2, 0, 0, 0);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            costType = bundle.getInt("costType", 0);
            list_GoodsCode = bundle.getStringArrayList("ListCode");
            if (costType == 3) {
                llFrameType.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            } else {
                llFrameType.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
            }
        }
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new OutGoodsListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setType(costType);
        adapter.setList_GoodsCode(list_GoodsCode);
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        dialog = new SearchDialog(aty);
        dialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);
        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
        modifyAllDialog = new NomalDialog(aty);
        modifyAllDialog.setOnNoamlLickListener(onModifyAllListener);
        getFirstPageData();

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
            bundle.putStringArrayList("ListCode",list_GoodsCode);
            skipActivityForResult(aty, OutGoodsResultListActivity.class, bundle, SelectGoodActivity.SELECT_GOOD_CODE);
        }
    };
    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            doModifyGoodsFrameType();
        }
    };

    NomalDialog.OnNoamlLickListener onModifyAllListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            ModifyAllGoodsFrameType();
        }
    };

    OutGoodsListAdapter.OnItemclickLister onItemclickLister = new OutGoodsListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }

        @Override
        public void OnItemClick(ConcurrentHashMap<String, String> map, int type, int num) {//0:查看详情、1：可选择
            if (costType == 3) {
                if(list_GoodsCode.contains(map.get("GoodsCode"))) {
                    toast("不能重复选择");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("GoodsCode", map.get("GoodsCode"));
                intent.putExtra("GoodsPrice", map.get("GoodsPrice"));
                intent.putExtra("GoodsName", map.get("GoodsName"));
                intent.putExtra("UnitName", map.get("UnitName"));
                intent.putExtra("GoodsPic", map.get("GoodsPic"));
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (type == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get("GoodsCode"));
                    skipActivityForResult(aty, EditOutGoodsActivity.class, bundle, Constants.ForResult.EDIT_GOODS);
                } else {
                    textNum.setText(String.valueOf(num));
                }
            }
        }
    };

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getGoodsOutList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getGoodsOutList(false, false);
        }
    };

    /**
     * 设置全部商品上下架
     */
    public void ModifyAllGoodsFrameType() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ClassifyId", classifyId);
        params.put("FrameType", frameType == 1 ? 0 : 1);
        httpRequestService.doRequestData(aty, "User/ModifyAllGoodsFrameType", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    switch (frameType) {
                        case 0:
                            toast("上架成功");
                            break;
                        case 1:
                            toast("下架成功");
                            break;
                    }
                    mSwipeLayout.setEnabled(true);
                    rlFrameType.setVisibility(View.GONE);
                    textBatch.setVisibility(View.VISIBLE);
                    rbFrameType.setEnabled(true);
                    rbWaitFrameType.setEnabled(true);
                    textClassify.setEnabled(true);
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                    modifyAllButton(false);
                    getFirstPageData();
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
     * 设置商品上下架
     */
    public void modifyGoodsFrameType(JSONArray jsonArray, final int frameType) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("FrameType", frameType);
        params.put("GoodsList", jsonArray);
        httpRequestService.doRequestData(aty, "User/ModifyGoodsFrameType", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    switch (frameType) {
                        case 1:
                            toast("上架成功");
                            break;
                        case 0:
                            toast("下架成功");
                            break;
                    }
                    mSwipeLayout.setEnabled(true);
                    rlFrameType.setVisibility(View.GONE);
                    textBatch.setVisibility(View.VISIBLE);
                    rbFrameType.setEnabled(true);
                    rbWaitFrameType.setEnabled(true);
                    textClassify.setEnabled(true);
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                    modifyAllButton(false);
                    getFirstPageData();
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
     * 获取数据
     *
     * @param isShowToast
     */
    public void getGoodsOutList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ClassifyId", classifyId);
        params.put("FrameType", frameType);
        params.put("Search", "");
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/GoodsOutList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);//下拉刷新结束
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
                            adapter.clearListChoice();
                            textNum.setText("0");
                            adapter.setLoadingDefualt();
                        }
                        listData.addAll(resultParam.listData);
                        if (listData.size() > 0) {
                            adapter.notifyDataSetChanged();
                            textNoData.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            try {
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            }
                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
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

    private void modifyAllButton(boolean showAllButton) {
        if (showAllButton) {
            btnAll.setText(frameType == 0 ? "全部上架" : "全部下架");
            btnAll.setVisibility(View.VISIBLE);
        } else {
            btnAll.setVisibility(View.GONE);
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
                dialog.show(4);
                break;
            case R.id.rbFrameType:
                frameType = 1;
                classifyId = "";
                textClassify.setText("商品分类：全部");
                setFrameTypeText();
                getFirstPageData();
                break;
            case R.id.rbWaitFrameType:
                frameType = 0;
                classifyId = "";
                textClassify.setText("商品分类：全部");
                setFrameTypeText();
                getFirstPageData();
                break;
            case R.id.textClassify:
                MobclickAgent.onEvent(aty, "OutGoods_Classify");
                skipActivityForResult(aty, ClassifyActivity.class, Constants.ForResult.CHOICE_CLASSIFY);
                break;
            case R.id.textBatch:
                mSwipeLayout.setEnabled(false);
                textBatch.setVisibility(View.GONE);
                rlFrameType.setVisibility(View.VISIBLE);
                rbFrameType.setEnabled(false);
                rbWaitFrameType.setEnabled(false);
                textClassify.setEnabled(false);
                setFrameTypeText();
                adapter.setType(1);
                adapter.notifyDataSetChanged();
                if (listData.size() > 0) {
                    modifyAllButton(true);
                }
                break;
            case R.id.btnClose:
                mSwipeLayout.setEnabled(true);
                rlFrameType.setVisibility(View.GONE);
                textBatch.setVisibility(View.VISIBLE);
                adapter.setType(0);
                adapter.clearListChoice();
                adapter.notifyDataSetChanged();
                textNum.setText(String.valueOf(0));
                rbFrameType.setEnabled(true);
                rbWaitFrameType.setEnabled(true);
                textClassify.setEnabled(true);
                modifyAllButton(false);
                break;
            case R.id.btnFrameType:
                if (frameType == 0) {
                    if (adapter.getListChoice().size() == 0) {
                        toast("请选择需要上架的商品");
                        return;
                    }
                    nomalDialog.show("确认商品价格无误并上架吗？");
                } else {
                    doModifyGoodsFrameType();
                }
                break;
            case R.id.btnAll:
                if (frameType == 0) {
                    // 全部上架
                    MobclickAgent.onEvent(aty, "OutGoods_AllUp");
                    modifyAllDialog.show("确认上架当前分类下的\n所有商品吗?", "取消", "确认");
                } else {
                    //全部下架
                    MobclickAgent.onEvent(aty, "OutGoods_AllDown");
                    modifyAllDialog.show("确认下架当前分类下的\n所有商品吗?", "取消", "确认");
                }
                break;
        }
    }

    private void setFrameTypeText() {
        switch (frameType) {
            case 0:
                MobclickAgent.onEvent(aty, "OutGoods_Up");
                btnFrameType.setText("上架");
                break;
            case 1:
                MobclickAgent.onEvent(aty, "OutGoods_Down");
                btnFrameType.setText("下架");
                break;
        }
    }

    /**
     * 数据处理
     */
    private void doModifyGoodsFrameType() {
        if (adapter.getListChoice().size() == 0) {
            toast("请选择需要" + ((frameType == 1) ? "下架" : "上架") + "的商品");
            return;
        }
        List<ConcurrentHashMap<String, String>> listTmpData = new ArrayList<>();
        ConcurrentHashMap<String, String> map = null;
        for (String code : adapter.getListChoice()) {
            map = new ConcurrentHashMap<>();
            map.put("GoodsCode", code);

            listTmpData.add(map);
            map = null;
        }
        modifyGoodsFrameType(new JSONArray(listTmpData), frameType == 1 ? 0 : 1);
    }

    /**
     * 获取首页数据
     */
    public void getFirstPageData() {
        pageIndex = 1;
        getGoodsOutList(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.CHOICE_CLASSIFY:
                if (resultCode == RESULT_OK && data != null) {
                    classifyId = data.getStringExtra("classifyId");
                    textClassify.setText("商品分类：" + data.getStringExtra("classifyName"));
                    getFirstPageData();
                }
                break;
            case SelectGoodActivity.SELECT_GOOD_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case Constants.ForResult.EDIT_GOODS:
                if (resultCode == RESULT_OK) {
                    getFirstPageData();
                }
                break;
            case Constants.ForResult.SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String msg = data.getExtras().getString("code");
                    dialog.updateDataBase(msg);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", msg);
                    bundle.putInt("costType", costType);
                    bundle.putStringArrayList("ListCode",list_GoodsCode);
                    showActivityForResult(aty, OutGoodsResultListActivity.class, bundle, SelectGoodActivity.SELECT_GOOD_CODE);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("外送商品");
        MobclickAgent.onResume(aty);
    }
}
