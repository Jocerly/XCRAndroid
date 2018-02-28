package com.yatang.xc.xcr.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.zbar.camera.CameraManager;
import com.yatang.xc.xcr.zbar.decode.MainHandler;
import com.yatang.xc.xcr.zbar.utils.BeepManager;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 扫描一维码Activity
 * Created by Jocerly on 2017/3/16.
 */
@ContentView(R.layout.activity_scan)
public class ScanActivity extends BaseActivity implements SurfaceHolder.Callback {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.capture_preview)
    private SurfaceView scanPreview;
    @BindView(id = R.id.capture_container)
    private RelativeLayout scanContainer;
    @BindView(id = R.id.capture_crop_view)
    private RelativeLayout scanCropView;
    @BindView(id = R.id.textLight, click = true)
    private TextView textLight;

    //输入区域
    @BindView(id = R.id.imageCodeClear, click = true)
    private ImageView imageCodeClear;
    @BindView(id = R.id.editCode)
    private EditText editCode;
    @BindView(id = R.id.textOk, click = true)
    private TextView textOk;
    @BindView(id = R.id.llBottom)
    private LinearLayout llBottom;

    private MainHandler mainHandler;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private BeepManager beepManager;
    private boolean isCameraOpen = true;
    private Rect mCropRect = null;

    private boolean isHasSurface = false;
    private Thread thread = null;
    private NomalDialog dialog;
    private boolean isCheck;
    private String goodsCode;
    private String type;
    private int costType = 2;//1：编辑商品、2：商品入库

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("扫描商品条码");
        MobclickAgent.onPause(aty);
    }

    public Handler getHandler() {
        return mainHandler;
    }

    @Override
    public void initWidget() {
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textTitle.setText("扫描商品条码");
        btnRight.setVisibility(View.GONE);
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
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isCheck = bundle.getBoolean("isCheck", false);
            type = bundle.getString("type");
            if ("1".equals(type) || "2".equals(type)) {
                llBottom.setVisibility(View.GONE);
            } else {
                llBottom.setVisibility(View.VISIBLE);
            }
        }

        isHasSurface = false;
        beepManager = new BeepManager(this);

        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(onNoamlLickListene);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.textLight:
                mCameraManager.switchLight();
                textLight.setBackgroundResource(mCameraManager.getIsOpenLight() ? R.drawable.light : R.drawable.light_not);
                break;
            case R.id.textOk:
                String code = editCode.getText().toString().trim();
                if (StringUtils.isEmpty(code)) {
                    toast("请输入商品条形码");
                    return;
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏软键盘
                if (isCheck) {
                    queryGoodsDetial(code);
                } else {
                    scanResult(code);
                }
                break;
            case R.id.imageCodeClear:
                editCode.setText("");
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseCamera();
        //关闭相机
        if (mCameraManager != null) {
            mCameraManager.closeDriver();
            mCameraManager = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove SurfaceCallback
        //关闭声音
        if (null != beepManager) {
            JCLoger.debug("releaseCamera: beepManager release");
            beepManager.close();
            beepManager = null;
        }
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        if (thread != null) {
            thread.isInterrupted();
            thread = null;
        }
    }

    //region 初始化和回收相关资源
    private void initCamera(SurfaceHolder surfaceHolder) {
        mainHandler = null;
        try {
            mCameraManager.openDriver(surfaceHolder);
            if (mainHandler == null) {
                mainHandler = new MainHandler(this, mCameraManager);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            JCLoger.debug("相机被占用", ioe);
        } catch (RuntimeException e) {
            e.printStackTrace();
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

    @Override
    public void onResume() {
        resumeCamera();
        super.onResume();
        MobclickAgent.onPageStart("扫描商品条码");
        MobclickAgent.onResume(aty);
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
        MobclickAgent.onEvent(aty, "Goods_SearchScanDone");
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JCLoger.debug(result.trim());
                if (isCheck) {
                    queryGoodsDetial(result.trim());
                } else {
                    scanResult(result.trim());
                }
            }
        }, 100);
    }

    /**
     * 扫描成功结果
     */
    public void scanResult(String result) {
        if (StringUtils.isEmpty(result) || !isCodeOK(result)) {
            toast("识别错误");
            releaseCamera();
            resumeCamera();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("code", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 查询商品内容
     *
     * @param code
     */
    private void queryGoodsDetial(final String code) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("GoodsCode", code);
        params.put("CostType", costType);
        httpRequestService.doRequestData(aty, "User/GoodsDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M04.equals(resultParam.resultId)) {
                    goodsCode = code;
                    dialog.show("商品条码不存在，请新增后再入库！", "继续入库", "新增商品");
                    resumeCamera();
                } else if (Constants.M00.equals(resultParam.resultId)) {
                    String goodStatus = resultParam.mapData.get("DataSource");
                    if (goodStatus.equals(ScanCodeActivity.STATUS_STORE)) {
                        //门店中有此商品
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("mapData", resultParam.mapData);
                        skipActivity(aty, AddToStockActivity.class, bundle);
                        finish();
                    } else {
                        //门店中没有此商品 弹一个提示
                        dialog.showTitle("门店不存在该商品", "请通过以下方式导入或新增后再入库\n商品管理->扫一扫", "确认");
                        editCode.setText("");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                //isFrist = bundle.getBoolean("isFrist", true);
                llBottom.setVisibility(View.GONE);
            }

        }
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
//            Bundle bundle = new Bundle();
//            bundle.putString("code", goodsCode);
//            skipActivityForResult(aty, AddGoodsActivity.class, bundle, Constants.ForResult.ADD_GOODS);
//            finish();

        }
    };

    //region  初始化截取的矩形区域
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
