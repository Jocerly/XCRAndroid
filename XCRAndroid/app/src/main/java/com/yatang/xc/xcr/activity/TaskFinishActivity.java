package com.yatang.xc.xcr.activity;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.TaskFinishAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务完成Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_task_finish)
public class TaskFinishActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.recyclerViewTaskFinish)
    private RecyclerView recyclerViewTaskFinish;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private TaskFinishAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("完成记录");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("完成记录");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("完成记录");
        btnRight.setVisibility(View.GONE);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        adapter = new TaskFinishAdapter(aty, listData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewTaskFinish.setLayoutManager(layoutManager);
        recyclerViewTaskFinish.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerViewTaskFinish.setAdapter(adapter);

        getTaskCompleteList(true);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTaskCompleteList(false);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 获取数据
     */
    private void getTaskCompleteList(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/TaskCompleteList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    recyclerViewTaskFinish.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    textNoData.setVisibility(listData.size() == 0 ? View.VISIBLE : View.GONE);
                } else if (Constants.M01.equals(resultParam.resultId)){
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    textNoData.setVisibility(View.VISIBLE);
                    textNoData.setText(resultParam.message);
                }
            }
        });
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
}
