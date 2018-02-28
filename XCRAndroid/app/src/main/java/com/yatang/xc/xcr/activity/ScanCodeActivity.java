package com.yatang.xc.xcr.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.ScanCodeAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.ScanGoodsDao;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.zbar.camera.CameraManager;
import com.yatang.xc.xcr.zbar.decode.MainHandler;
import com.yatang.xc.xcr.zbar.utils.BeepManager;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫码调价Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_scan_code)
public class ScanCodeActivity extends BaseActivity implements SurfaceHolder.Callback {

    public static final String STATUS_STORE = "2";//店铺有此商品
    public static final String STATUS_MAINDATA = "1";//主数据有此商品
    public static final String STATUS_NEW = "0";//新商品

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.imageCodeClear, click = true)
    private ImageView imageCodeClear;

    //非扫码
    @BindView(id = R.id.rlNoScan)
    private RelativeLayout rlNoScan;
    @BindView(id = R.id.btnOpenScan, click = true)
    private Button btnOpenScan;

    //扫描
    @BindView(id = R.id.capture_preview)
    private SurfaceView scanPreview;
    @BindView(id = R.id.capture_container)
    private RelativeLayout scanContainer;
    @BindView(id = R.id.capture_crop_view)
    private RelativeLayout scanCropView;

    @BindView(id = R.id.textLight, click = true)
    private TextView textLight;
    @BindView(id = R.id.textClose, click = true)
    private TextView textClose;

    @BindView(id = R.id.editCode)
    private EditText editCode;
    @BindView(id = R.id.textOk, click = true)
    private TextView textOk;
    @BindView(id = R.id.btnNext, click = true)
    private TextView btnNext;
    @BindView(id = R.id.mRecyclerView)
    private RecyclerView mRecyclerView;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private ScanCodeAdapter adapter;

    private ScanGoodsDao goodsDao;

    private boolean isOpen = true;
    private boolean isScan = true;

    private MainHandler mainHandler;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private BeepManager beepManager;

    private Rect mCropRect = null;

    private boolean isHasSurface = false;
    private Thread thread = null;
    private boolean isCameraOpen = true;
    private NomalDialog dialog;
    private String goodsCode;
    private int costType = 1;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("扫一扫");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textTitle.setText("扫一扫");
        btnRight.setVisibility(View.GONE);
        btnRight.setText("调价记录");
        editCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!StringUtils.isEmpty(editCode.getText().toString().trim())) {
                        imageCodeClear.setVisibility(View.VISIBLE);
                    } else {
                        imageCodeClear.setVisibility(View.GONE);
                    }
                }
            }
        });
        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    imageCodeClear.setVisibility(View.VISIBLE);
                } else {
                    imageCodeClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog = new NomalDialog(aty);
        detachLayout();
    }

    @Override
    public void initData() {
        goodsDao = new ScanGoodsDao(aty);
        adapter = new ScanCodeAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        mRecyclerView.setAdapter(adapter);

        //构造出扫描管理器
        isHasSurface = false;
        beepManager = new BeepManager(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void refrashData(boolean isFirst) {
        listData.clear();
        listData.addAll(goodsDao.getAllData());
        adapter.notifyDataSetChanged();
        if (!isFirst && listData.size() >= 10) {
            close();
        }

    }

    /**
     * 删除回调
     */
    ScanCodeAdapter.OnItemClickListener onItemClickListener = new ScanCodeAdapter.OnItemClickListener() {
        @Override
        public void delete(int posiotion, String code) {
            if (goodsDao.deleteByGoodsCode(code)) {
                JCLoger.debug(code + "");
                refrashData(false);
            }
        }
    };

    @Override
    public void onResume() {
        if (isOpen) {
            resumeCamera();
        }
        super.onResume();
        MobclickAgent.onPageStart("扫一扫");
        MobclickAgent.onResume(aty);
        refrashData(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOpen) {
            releaseCamera();
            //关闭相机
            if (mCameraManager != null) {
                mCameraManager.closeDriver();
                mCameraManager = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭声音
        if (null != beepManager) {
            JCLoger.debug("releaseCamera: beepManager onDestroy");
            beepManager.close();
            beepManager = null;
        }
        if (!isHasSurface) {
            mHolder.removeCallback(this);
        }
        if (thread != null) {
            thread.isInterrupted();
            thread = null;
        }
    }

    /**
     * 初始化和回收相关资源
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        mainHandler = null;
        try {
            mCameraManager.openDriver(surfaceHolder);
            if (mainHandler == null) {
                mainHandler = new MainHandler(this, mCameraManager);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            toast("相机被占用");
        } catch (RuntimeException e) {
            e.printStackTrace();
            toast("相机连接失败，检测是否关闭相机权限");
            isCameraOpen = false;
            textLight.setEnabled(false);
        }
    }

    private void resumeCamera() {
        if (mCameraManager == null) {
            mCameraManager = new CameraManager(getApplication());
        }
        mHolder = scanPreview.getHolder();
        if (isHasSurface) {
            thread = new Thread(new OpenCameraRunnable(aty, mHolder));
            thread.start();
        } else {
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    public Handler getHandler() {
        return mainHandler;
    }

    private void releaseCamera() {
        if (null != mainHandler) {
            //关闭聚焦,停止预览,清空预览回调,quit子线程looper
            mainHandler.quitSynchronously();
            mainHandler = null;
        }
    }

    //region 扫描结果
    public void checkResult(final String result) {
        if (beepManager != null) {
            beepManager.playBeepSoundAndVibrate();
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JCLoger.debug(result.trim());
                scanResult(result.trim());
            }
        }, 500);
        releaseCamera();
    }

    /**
     * 扫描成功结果
     */
    public void scanResult(String code) {
        if (StringUtils.isEmpty(code) || !isCodeOK(code)) {
            toast("识别错误");
            resumeCamera();
            return;
        }
        if (listData.size() >= 10) {
            toast("最多添加10条，已爆表");
            close();
            return;
        }

        if (goodsDao.checkGoodsCodeIsExists(code)) {//存在
            toast("商品已在列表中，不可重复添加");
            resumeCamera();
        } else {
            goodsCode = code;
            isScan = true;
            getGoodsDetial();
        }
    }

    /**
     * 获取数据
     */
    private void getGoodsDetial() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", goodsCode);
        params.put("CostType", costType);
        httpRequestService.doRequestData(aty, "User/GoodsDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                editCode.setText("");
                if (Constants.M00.equals(resultParam.resultId)) {
                    String goodStatus = resultParam.mapData.get("DataSource");
                    switch (goodStatus) {
                        case STATUS_STORE:
                            //店铺有此商品
                            MobclickAgent.onEvent(aty, "Firm_Goods_Had");
                            addGoodsToList(resultParam.mapData);
                            break;
                        case STATUS_MAINDATA:
                            //主数据有此商品
                            MobclickAgent.onEvent(aty, "Firm_Goods_Import");
                            showImportDialog();
                            resumeCamera();
                            break;
                        case STATUS_NEW:
                            //新增商品
                            MobclickAgent.onEvent(aty, "Firm_Goods_Add");
                            showNewGoodDDialog();
                            resumeCamera();
                            break;
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    if (isOpen && isScan) {
                        resumeCamera();
                    }
                    toast(resultParam.message);
                }
            }

        });
    }

    /**
     * 获取数据
     */
    private void importGoodToStore() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", goodsCode);
        httpRequestService.doRequestData(aty, "User/ShopImportGoods", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                editCode.setText("");
                if (Constants.M00.equals(resultParam.resultId)) {
                    //导入成功
                    MobclickAgent.onEvent(aty, "Firm_Goods_ImportDone");
                    addGoodsToList(resultParam.mapData);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    private void showImportDialog() {
        dialog.show("门店无该商品\n是否从平台商品库导入");
        dialog.setOnNoamlLickListener(onImportDialogListener);
    }

    private void showNewGoodDDialog() {
        dialog.show("门店与平台商品库中均无该商品\n是否新增商品");
        dialog.setOnNoamlLickListener(onNewGoodDialogListener);
    }

    NomalDialog.OnNoamlLickListener onImportDialogListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            //点击确认从主数据导入商品
            importGoodToStore();
        }
    };

    NomalDialog.OnNoamlLickListener onNewGoodDialogListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            //点击确跳转到新增商品页面
            Bundle bundle = new Bundle();
            bundle.putString("code", goodsCode);
            bundle.putBoolean("needReturn", true);
            skipActivityForResult(aty, AddGoodsActivity.class, bundle, Constants.ForResult.ADD_GOODS);
        }
    };

    /**
     * 将扫描商品存入数据库加入到列表中
     */
    private void addGoodsToList(ConcurrentHashMap<String, String> mapData) {
        if (goodsDao.doAdd(mapData.get("GoodsId"),
                mapData.get("GoodsName"),
                mapData.get("GoodsPrice"),
                mapData.get("CostPrice"),
                mapData.get("GoodsPrice"),
                mapData.get("GoodsCode"),
                mapData.get("UnitName"),
                mapData.get("GoodsStatue"))) {
            refrashData(false);
            if (isOpen && isScan) {
                resumeCamera();
            }
        } else {
            toast("增加商品出错");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                finish();
                break;
            case R.id.btnRight:
                skipActivity(aty, ScanCodeListActivity.class);
                break;
            case R.id.textLight:
                mCameraManager.switchLight();
                textLight.setBackgroundResource(mCameraManager.getIsOpenLight() ? R.drawable.light : R.drawable.light_not);
                break;
            case R.id.btnOpenScan:
                if (listData.size() >= 10) {
                    toast("最多添加10条，已爆表");
                    close();
                    return;
                }
                open();
                break;
            case R.id.textClose:
                close();
                break;
            case R.id.btnNext:
                if (listData.size() == 0) {
                    toast("无商品可调价，请添加");
                    return;
                }
                Bundle bundle = new Bundle();
                skipActivityForResult(aty, ScanCodeConfirmActivity.class, bundle, Constants.ForResult.SCAN_CODE);
                break;
            case R.id.textOk:
                if (listData.size() >= 10) {
                    toast("最多添加10条，已爆表");
                    editCode.setText("");
                    close();
                    return;
                }
                String code = editCode.getText().toString().trim();
                if (StringUtils.isEmpty(code)) {
                    toast("请输入商品条形码");
                    return;
                } else if (code.length() > 20) {
                    toast("条形码最多输入20个字符");
                    return;
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏软键盘
                if (goodsDao.checkGoodsCodeIsExists(code)) {//存在
                    toast("商品已在列表中，不可重复添加");
                } else {
                    goodsCode = code;
                    isScan = false;
                    getGoodsDetial();
                }
                break;
            case R.id.imageCodeClear:
                editCode.setText("");
                break;
        }
    }

    private void close() {
        isOpen = false;
        releaseCamera();
        rlNoScan.setVisibility(View.VISIBLE);
        scanContainer.setVisibility(View.GONE);
    }

    private void open() {
        isOpen = true;
        resumeCamera();
        rlNoScan.setVisibility(View.GONE);
        scanContainer.setVisibility(View.VISIBLE);
    }

    /**
     * region  初始化截取的矩形区域
     */
    public Rect initCrop() {
        int cameraWidth = 0;
        int cameraHeight = 0;
        if (null != mCameraManager) {
            cameraWidth = mCameraManager.getCameraResolution().y;
            cameraHeight = mCameraManager.getCameraResolution().x;
        }

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
        return new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    //endregion

    //region SurfaceHolder Callback 回调方法
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            JCLoger.debug("*** 没有添加SurfaceHolder的Callback");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            thread = new Thread(new OpenCameraRunnable(aty, holder));
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        JCLoger.debug("surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SCAN_CODE:
                if (resultCode == RESULT_OK) {
                    toast("调价成功");
                }
                break;
            case Constants.ForResult.ADD_GOODS:
                if (resultCode == RESULT_OK) {
                    if (isOpen && isScan) {
                        resumeCamera();
                    }
                    refrashData(false);
                }
                break;
        }
    }

    private class OpenCameraRunnable implements Runnable {
        private Activity activity;
        SurfaceHolder holder;

        public OpenCameraRunnable(Activity activity, SurfaceHolder holder) {
            this.activity = activity;
            this.holder = holder;
        }

        @Override
        public void run() {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initCamera(holder);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
