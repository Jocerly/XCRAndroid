package com.yatang.xc.xcr.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.TaskAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.TaskExplainDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务中心Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_task)
public class TaskActivity extends BaseActivity {
    /**
     * 1：本地任务拍门头照
     */
    public static final int TASK_TAKEPHOTO = 1;
    /**
     * 2：本地跳转（根据url页面）
     */
    public static final int TASK_NATIVE_BUY = 2;
    /**
     * 3：后台自动处理
     */
    public static final int TASK_AUTO_BACKGROUND = 3;
    /**
     * 4：本地输入单号
     */
    public static final int TASK_BUYPOS = 4;
    /**
     * 5：申请租金补贴
     */
    public static final int TASK_RENT = 5;
    /**
     * 6：签到
     */
    public static final int TASK_SIHN = 6;

    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textCashAward)
    private TextView textCashAward;
    @BindView(id = R.id.textAwardUnit)
    private TextView textAwardUnit;
    @BindView(id = R.id.rbDaily, click = true)
    private RadioButton rbDaily;
    @BindView(id = R.id.rbRecommended, click = true)
    private RadioButton rbRecommended;
    @BindView(id = R.id.rbLearning, click = true)
    private RadioButton rbLearning;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    @BindView(id = R.id.recyclerViewTask)
    private RecyclerView recyclerViewTask;
    private NomalDialog dialog;
    private TaskExplainDialog taskExplainDialog;

    private TaskAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private List<ConcurrentHashMap<String, String>> listTmpData = new ArrayList<>();
    /**
     * 1：日常任务，2：推荐任务、3：学习任务
     */
    private int type = 1;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("任务中心");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.blue_task);
        rlTitle.setBackgroundColor(color);
        textTitle.setText("任务中心");
        btnRight.setText("完成记录");
        mSwipeLayout.setColorSchemeResources(R.color.blue_task);

        setWindowColor(color);
    }

    @Override
    public void initData() {
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        adapter = new TaskAdapter(aty, listTmpData);
        adapter.setOnTaskClistener(onTaskClistener);
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewTask.setLayoutManager(layoutManager);
        recyclerViewTask.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerViewTask.setAdapter(adapter);
        rbDaily.setChecked(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("任务中心");
        MobclickAgent.onResume(aty);
        getTaskList(true);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTaskList(false);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 获取数据
     */
    private void getTaskList(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/TaskList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    JCLoger.debug(resultParam.mapData.get("Award"));
                    textCashAward.setText(resultParam.mapData.get("Award"));
                    textAwardUnit.setText("奖励(" + resultParam.mapData.get("AwardUnit") + ")");
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    choiceListData();
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

    /**
     * 领取奖励
     */
    private void doReceiveReward(String taskId) {
        MobclickAgent.onEvent(aty, "Task_Award");
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        httpRequestService.doRequestData(aty, "User/ReceiveReward", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (dialog == null) {
                        dialog = new NomalDialog(aty);
                        dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                            @Override
                            public void onOkClick() {
                                getTaskList(true);
                            }
                        });
                    }
                    dialog.showClose(resultParam.mapData.get("SucMsg"));
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
     * 分类刷新数据
     */
    private void choiceListData() {
        listTmpData.clear();
        for (int i = 0; i < listData.size(); i++) {
            if (type == Integer.parseInt(listData.get(i).get("TaskClassfy"))) {
                listTmpData.add(listData.get(i));
            }
        }
        adapter.notifyDataSetChanged();
        textNoData.setVisibility(listTmpData.size() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 触发item按钮
     */
    TaskAdapter.OnTaskClistener onTaskClistener = new TaskAdapter.OnTaskClistener() {
        @Override
        public void onTask(ConcurrentHashMap<String, String> map) {
            JCLoger.debug(map.toString());
            //1：本地任务拍门头照、2：本地跳转（根据url页面）、3：后台自动处理、4：本地输入单号，5：申请租金补贴
            JCLoger.debug("TaskStatue=" + map.get("TaskStatue") + " taskType=" + map.get("TaskType"));
            if ("0".equals(map.get("TaskStatue")) && "1".equals(map.get("IsRelate"))) {
                MobclickAgent.onEvent(aty, "Task_Do");
                CheckTask(map);
            } else {
                doTaskJump(map);
            }
        }
    };

    /**
     * 触发item 弹出任务说明对话框
     */
    TaskAdapter.OnItemClickListener onItemClickListener = new TaskAdapter.OnItemClickListener() {

        @Override
        public void itemClick(ConcurrentHashMap<String, String> map, String btnText) {
            MobclickAgent.onEvent(aty, "Task_Detial");
            if (StringUtils.isEmpty(map.get("TaskExplain"))) {
                JCLoger.debug("TaskExplain is null");
                return;
            }
            initTaskDialog();
            if ("继续做".equals(btnText) || "做任务".equals(btnText) || "领奖励".equals(btnText)) {
                taskExplainDialog.show("关闭", btnText, map);
            } else {
                taskExplainDialog.show("关闭", map);
            }

        }
    };

    private void doTaskJump(ConcurrentHashMap<String, String> map) {
        switch (Integer.parseInt(map.get("TaskType"))) {
            /**
             * TASK_TAKEPHOTO = 1; 本地任务拍门头照
             * TASK_NATIVE_BUY = 2; 本地购买（根据url页面
             * TASK_AUTO_BACKGROUND = 3; 后台自动处理
             * TASK_BUYPOS = 4; 本地输入单号、购买收银机
             * TASK_RENT = 5：申请租金补贴
             * TASK_SIHN = 6：签到
             */
            case TASK_TAKEPHOTO:
                if ("0".equals(map.get("TaskStatue")) || "3".equals(map.get("TaskStatue"))) {
                    Bundle bundle = new Bundle();
                    bundle.putString("TskId", map.get("TaskId"));
                    skipActivity(aty, UploadDoorPicActivity.class, bundle);
                } else if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }
                break;
            case TASK_NATIVE_BUY:
                if ("0".equals(map.get("TaskStatue")) || "3".equals(map.get("TaskStatue"))) {
                    if ("3".equals(map.get("TaskClassfy"))) {
                        //点击了课堂学习任务项
                        getClassDetails(map.get("TaskId"));
                    } else {
                        if (!MyApplication.instance.isX5Over) {
                            toast(R.string.x5_toast);
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("ClassUrl", map.get("TaskUrl"));
                        bundle.putString("ClassName", map.get("TaskName"));
                        bundle.putString("ClassId", map.get("TaskId"));
                        skipActivity(aty, WebViewActivity.class, bundle);
                    }
                } else if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }

                break;
            case TASK_BUYPOS:
                if ("0".equals(map.get("TaskStatue")) || "3".equals(map.get("TaskStatue"))) {
                    Bundle bundle = new Bundle();
                    bundle.putString("TskId", map.get("TaskId"));
                    skipActivity(aty, BuyPosActivity.class, bundle);
                } else if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }
                break;
            case TASK_RENT:
                if ("0".equals(map.get("TaskStatue")) || "3".equals(map.get("TaskStatue"))) {
                    Bundle bundle = new Bundle();
                    bundle.putString("TskId", map.get("TaskId"));
                    skipActivity(aty, UploadRentPicActivity.class, bundle);
                } else if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }
                break;
            case TASK_AUTO_BACKGROUND:
                if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }
                break;
            case TASK_SIHN:
                if ("0".equals(map.get("TaskStatue")) || "3".equals(map.get("TaskStatue"))) {
                    skipActivity(aty, SignActivity.class);
                } else if ("1".equals(map.get("IsTakeReward")) && "2".equals(map.get("TaskStatue"))) {
                    doReceiveReward(map.get("TaskId"));
                }
                break;
        }
    }

    /**
     * 检查是否能执行任务
     */
    private void CheckTask(final ConcurrentHashMap<String, String> map) {
        String taskId = map.get("TaskId");
        if (StringUtils.isEmpty(taskId)) {
            toast("任务id为空.");
            return;
        }
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        httpRequestService.doRequestData(aty, "User/CheckTask", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    doTaskJump(map);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else if (Constants.M02.equals(resultParam.resultId)) {
                    toast("获取出错！");
                } else if (Constants.M03.equals(resultParam.resultId)) {
                    toast("请先做关联任务！");
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 获取任务详情
     */
    private void getClassDetails(final String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            toast("任务id为空.");
            return;
        }
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        httpRequestService.doRequestData(aty, "User/TaskClassDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!MyApplication.instance.isX5Over) {
                        toast(R.string.x5_toast);
                        return;
                    }
                    //获取课堂详情成功 进入课堂页面
                    Bundle bundle = new Bundle();
                    bundle.putString("ClassName", resultParam.mapData.get("ClassName"));
                    bundle.putString("ClassTimes", resultParam.mapData.get("ClassTimes"));
                    bundle.putString("ClassUrl", resultParam.mapData.get("ClassUrl"));
                    bundle.putString("TaskId", resultParam.mapData.get("ClassId"));
                    bundle.putBoolean("IsFinished", false);
                    bundle.putString("ReturnText", "返回任务中心");
                    skipActivity(aty, WebViewActivity.class, bundle);
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                skipActivity(aty, TaskFinishActivity.class);
                break;
            case R.id.rbDaily:
                type = 1;
                choiceListData();
                break;
            case R.id.rbRecommended:
                type = 2;
                choiceListData();
                break;
            case R.id.rbLearning:
                type = 3;
                choiceListData();
                break;
        }
    }

    /**
     * 初始化任务说明对话框
     */
    private void initTaskDialog() {
        if (taskExplainDialog == null) {
            taskExplainDialog = new TaskExplainDialog(aty);
            taskExplainDialog.setOnDoTaskClickListenner(new TaskExplainDialog.OnDoTaskClickListenner() {
                @Override
                public void onDoTask(ConcurrentHashMap<String, String> mapsData) {
                    JCLoger.debug(mapsData.toString());
                    //1：本地任务拍门头照、2：本地跳转（根据url页面）、3：后台自动处理、4：本地输入单号，5：申请租金补贴
                    JCLoger.debug("TaskStatue=" + mapsData.get("TaskStatue") + " taskType=" + mapsData.get("TaskType"));
                    if ("0".equals(mapsData.get("TaskStatue")) && "1".equals(mapsData.get("IsRelate"))) {
                        CheckTask(mapsData);
                    } else {
                        doTaskJump(mapsData);
                    }
                }
            });
        }
    }
}
