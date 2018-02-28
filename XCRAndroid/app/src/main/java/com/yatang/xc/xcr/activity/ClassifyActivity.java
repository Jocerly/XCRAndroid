package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.ClassifyGroupAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.ClassifyDao;
import com.yatang.xc.xcr.db.ClassifyFirstDao;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.UIHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类
 * Created by Jocerly on 2017/5/25.
 */
@ContentView(R.layout.activity_classify)
public class ClassifyActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    private RecyclerView mRecyclerView;
    private ClassifyGroupAdapter groupAdapter;
    private ArrayList<ConcurrentHashMap<String, Object>> listGroupData = new ArrayList<>();

    private ClassifyFirstDao classifyFirstDao;
    private ClassifyDao classifyDao;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("商品分类");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品分类");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        btnRight.setText("全部");
        textTitle.setText("商品分类");
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        groupAdapter = new ClassifyGroupAdapter(aty, listGroupData);
        groupAdapter.setOnClassifyGroupClistener(onClassifyGroupClistener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad15), getResources().getColor(R.color.base_bg)));
        mRecyclerView.setAdapter(groupAdapter);
    }

    @Override
    public void initData() {
        classifyFirstDao = new ClassifyFirstDao(aty);
        classifyDao = new ClassifyDao(aty);
        if (Common.isToday(Common.getAppInfo(aty, Constants.Preference.ClassifyDate, ""))) {
            setData();
        } else {
            classifyDao.delete();
            classifyFirstDao.delete();
            getClassifyList();
        }
    }

    private void setData() {
        listGroupData.clear();
        ConcurrentHashMap<String, Object> mapDataTmp = null;
        List<ConcurrentHashMap<String, String>> listDataTmp = null;
        List<ConcurrentHashMap<String, String>> listData = classifyDao.getAllData();
        List<ConcurrentHashMap<String, String>> listGroupDataTmp = classifyFirstDao.getAllData();
        for (ConcurrentHashMap<String, String> map : listGroupDataTmp) {
            mapDataTmp = new ConcurrentHashMap<>();
            listDataTmp = new ArrayList<>();
            for (ConcurrentHashMap<String, String> hashMap : listData) {
                if (map.get("ClassifyFirstId").equals(hashMap.get("ClassifyFirstId"))) {
                    listDataTmp.add(hashMap);
                }
            }
            mapDataTmp.put("SecondList", listDataTmp);
            mapDataTmp.putAll(map);
            listGroupData.add(mapDataTmp);
            mapDataTmp = null;
            listDataTmp = null;
        }
        groupAdapter.notifyDataSetChanged();
        UIHelper.cloesLoadDialog();
    }

    /**
     * 获取分类
     */
    private void getClassifyList() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/ClassifyList", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    UIHelper.showLoadDialog(aty);
                    ArrayList<ConcurrentHashMap<String, String>> listData = resultParam.listData;
                    for (ConcurrentHashMap<String, String> map : listData) {
                        try {
                            classifyFirstDao.doAdd(map.get("ClassifyFirstId"), map.get("ClassifyFirstName"));
                            JSONArray array = new JSONArray(map.get("SecondList"));
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonMap = array.getJSONObject(i);
                                classifyDao.doAdd(jsonMap.getString("ClassifyId"), map.get("ClassifyFirstId"), jsonMap.getString("ClassifyName")
                                        , StringUtils.isEmpty(jsonMap.getString("ClassifyPic")) ? "0" : jsonMap.getString("ClassifyPic"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Common.setAppInfo(aty, Constants.Preference.ClassifyDate, Common.getDate(new Date()));
                    setData();
                }
            }
        });
    }

    ClassifyGroupAdapter.OnClassifyGroupClistener onClassifyGroupClistener = new ClassifyGroupAdapter.OnClassifyGroupClistener() {
        @Override
        public void onClassifyGroupItem(String classifyId, String classifyName) {
            MobclickAgent.onEvent(aty, "OutGoods_ClassifyDetial");
            doReBack(classifyId, classifyName);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                doReBack("", "全部");
                break;
        }
    }

    /**
     * 选择回调
     * @param classifyId
     * @param classifyName
     */
    private void doReBack(String classifyId, String classifyName) {
        Intent intent = new Intent();
        intent.putExtra("classifyId", classifyId);
        intent.putExtra("classifyName", classifyName);
        setResult(RESULT_OK, intent);
        finish();
    }
}
