package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.SearchBankAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.BankCardDao;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索银行
 * Created by dengjiang on 2017/8/19.
 */
@ContentView(R.layout.activity_search_bank)
public class SearchBankActivity extends BaseActivity implements View.OnClickListener{
    public static final String SEARCH_BANK = "1"; //搜索开户行
    public static final String SEARCH_BRANCH = "2";//搜索支行
    @BindView(id = R.id.btnLeft, click = true)
    private PressTextView btnLeft;
    @BindView(id = R.id.textTitle, click = true)
    private TextView textTitle;
    @BindView(id = R.id.btnSearch, click = true)
    private PressTextView btnSearch;
    @BindView(id = R.id.editSearch, click = true)
    private EditText editSearch;
    @BindView(id = R.id.imageClear, click = true)
    private ImageView imageClear;
    @BindView(id = R.id.listbank, click = true)
    private LoadMoreRecyclerView recyclerBank;

    private String type;//搜索类型 1:收索开户行 2：收索支行
    private int pageIndex;
    private BankCardDao bankCardDao;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private SearchBankAdapter adapter;
    private String bankID;
    @Override
    public void initWidget() {
        btnLeft.setOnClickListener(this);
        imageClear.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        editSearch.addTextChangedListener(textWatcher);
        adapter = new SearchBankAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载

        recyclerBank.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerBank.initDecoration(aty, 1, getResources().getColor(R.color.line));
        recyclerBank.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        recyclerBank.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        bankCardDao = new BankCardDao(aty);
        detachLayout();
    }

    @Override
    public void initData() {
//        String type, String bankID
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String type = bundle.getString("Type");
            String bankID = bundle.getString("BankID");
            show(type,bankID);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeft:
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.imageClear:
                editSearch.setText("");
                break;
            case R.id.btnSearch:
                pageIndex = 1;
                listData.clear();
                switch (type) {
                    case SEARCH_BANK:
                        getBankList();
                        break;
                    case SEARCH_BRANCH:
                        getBankBranchList();
                        break;
                }
                break;
        }
    }

    /**
     * 搜索开户行数据
     */
    private void getBankList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter.setType(type);
                try {
                    Thread.sleep(500);
                    final List<ConcurrentHashMap<String, String>> listTemp = bankCardDao.getAllData(editSearch.getText().toString().trim(), pageIndex, Constants.PageSize);
                    aty.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listData.addAll(listTemp);
                            if (listTemp.size() < Constants.PageSize) {
                                //没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 搜索分行数据
     */
    private void getBankBranchList() {
        adapter.setType(type);
        params.clear();
        params.put("Key", editSearch.getText().toString().trim());
        params.put("BankCardId", bankID);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        MyApplication.instance.getHttpRequestService().doRequestData(aty, "System/GetBankCardBranch", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (pageIndex > 1 && resultParam.listData.size() < 1) {//没有更多数据了
                        pageIndex--;
                        //没有更多数据了
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                    } else {
                        if (pageIndex == 1) {
                            listData.clear();
                            adapter.setLoadingDefualt();
                        }
                        listData.addAll(resultParam.listData);
                        if (listData.size() > 0) {
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                        }else {
                            //没有更多数据了
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                        }

                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //如果请求下一页数据失败 pageIndex 减一
                        pageIndex = pageIndex--;
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            //加载更多
            pageIndex++;
            switch (type) {
                case SEARCH_BANK:
                    getBankList();
                    break;
                case SEARCH_BRANCH:
                    getBankBranchList();
                    break;
            }
        }
    };

    SearchBankAdapter.OnItemClickListener onItemClickListener = new SearchBankAdapter.OnItemClickListener() {

        @Override
        public void itemClick(String bankName, String bankId) {
            Intent i = new Intent();
            i.putExtra("BankName", bankName);
            i.putExtra("BankID", bankId);
            setResult(RESULT_OK, i);
            finish();
        }

        @Override
        public void OnFooterClick() {
            recyclerBank.startLoadMore();
        }
    };

    /**
     * 显示对话框
     *
     * @param type:1：搜索开户行 2：搜索支行
     * @param bankID       :搜索支行时候的 开户行ID
     */
    public void show(String type, String bankID) {
        this.bankID = bankID;
        this.type = type;
        pageIndex = 1;
        editSearch.setText("");
        listData.clear();
        switch (type) {
            case SEARCH_BANK:
                textTitle.setText("选择开户行");
                editSearch.setHint("请输入银行关键字");
                getBankList();
                break;
            case SEARCH_BRANCH:
                getBankBranchList();
                textTitle.setText("选择支行");
                editSearch.setHint("请输入支行关键字");
                break;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (StringUtils.isEmpty(charSequence.toString())) {
                imageClear.setVisibility(View.GONE);
            } else {
                imageClear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
