package com.yatang.xc.xcr.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.ClassesAdapter;
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
 * 小超课堂Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_classes)
public class ClassesActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.recyclerViewClass)
    private RecyclerView recyclerViewClass;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private ClassesAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("小超课堂");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText(R.string.classes);
        btnRight.setVisibility(View.GONE);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        adapter = new ClassesAdapter(aty, listData);
        adapter.setOnClassesClistener(onClassesClistener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewClass.setLayoutManager(layoutManager);
        recyclerViewClass.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerViewClass.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("小超课堂");
        MobclickAgent.onResume(aty);
        getClassList(true);
    }

    ClassesAdapter.OnClassesClistener onClassesClistener = new ClassesAdapter.OnClassesClistener() {
        @Override
        public void onClasses(String className, String classTimes, String classUrl, String taskId, boolean isFinished) {
            if (!MyApplication.instance.isX5Over) {
                toast(R.string.x5_toast);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("ClassName", className);
            bundle.putString("ClassTimes", classTimes);
            bundle.putString("ClassUrl", classUrl);
            bundle.putString("TaskId", taskId);
            bundle.putBoolean("IsFinished", isFinished);
            bundle.putString("ReturnText", "返回小超课堂");
            skipActivity(aty, WebViewActivity.class, bundle);
        }
    };

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getClassList(false);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 获取数据
     */
    private void getClassList(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/ClassList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

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
                    adapter.notifyDataSetChanged();
                    textNoData.setVisibility(listData.size() == 0 ? View.VISIBLE : View.GONE);
                } else if (Constants.M01.equals(resultParam.resultId)) {
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
