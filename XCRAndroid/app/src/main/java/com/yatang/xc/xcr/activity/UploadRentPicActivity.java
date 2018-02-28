package com.yatang.xc.xcr.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.ChoicePicDiaolog;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ImgClickListenner;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DensityUtils;
import org.jocerly.jcannotation.utils.ImageUtils;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.SDCardUtils;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;
import org.jocerly.jcannotation.widget.UIHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 申请租金补贴Activity
 */
@ContentView(R.layout.activity_upload_rent_pic)
public class UploadRentPicActivity extends BaseActivity implements GalleryFinal.OnHanlderResultCallback {
    private static final int CAMERA_CODE = 1;
    private static final int GALLERY_CODE = 2;
    private static final int UPDATE_CODE = 3;
    private static final int TASKFAILED_CODE = 4;

    private static final int IMAGE_SIZE_1 = 2000 * 1024;
    private static final int IMAGE_SIZE_2 = 1000 * 1024;
    private static final int IMAGE_SIZE_3 = 500 * 1024;
    private static final int IMAGE_SIZE_4 = 300 * 1024;
    private static final String TMP_FILE_NAME = "temp.jpg";

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    //开启电视机广告
    @BindView(id = R.id.rlOpenTvPic, click = true)
    private RelativeLayout rlOpenTvPic;
    @BindView(id = R.id.imageOpenTvPic)
    private ImageView imageOpenTvPic;
    @BindView(id = R.id.textOpenTvPic)
    private TextView textOpenTvPic;
    @BindView(id = R.id.imageOpenTvPicShade)
    private ImageView imageOpenTvPicShade;
    //收银机登录收银系统
    @BindView(id = R.id.rlLoginCMPic, click = true)
    private RelativeLayout rlLoginCMPic;
    @BindView(id = R.id.imageLoginCMPic)
    private ImageView imageLoginCMPic;
    @BindView(id = R.id.textLoginCMPic)
    private TextView textLoginCMPic;
    @BindView(id = R.id.imageLoginCMPicShade)
    private ImageView imageLoginCMPicShade;
    //货架商品陈列样式
    @BindView(id = R.id.rlShelfDisplayPic, click = true)
    private RelativeLayout rlShelfDisplayPic;
    @BindView(id = R.id.imageShelfDisplayPic)
    private ImageView imageShelfDisplayPic;
    @BindView(id = R.id.textShelfDisplayPic)
    private TextView textShelfDisplayPic;
    @BindView(id = R.id.imageShelfDisplayPicShade)
    private ImageView imageShelfDisplayPicShade;
    //加密验收单
    @BindView(id = R.id.rlAcceptPic, click = true)
    private RelativeLayout rlAcceptPic;
    @BindView(id = R.id.imageAcceptPic)
    private ImageView imageAcceptPic;
    @BindView(id = R.id.textAcceptPic)
    private TextView textAcceptPic;
    @BindView(id = R.id.imageAcceptPicShade)
    private ImageView imageAcceptPicShade;

    @BindView(id = R.id.btnUpDate, click = true)
    private Button btnUpDate;

    @BindView(id = R.id.img_Open_Tv)
    private ImageView img_Open_Tv;
    @BindView(id = R.id.img_Login_Cm)
    private ImageView img_Login_Cm;
    @BindView(id = R.id.img_Shelf_Display)
    private ImageView img_Shelf_Display;
    @BindView(id = R.id.img_Accept2)
    private ImageView img_Accept2;

    private ChoicePicDiaolog picDiaolog;
    private NomalDialog nomalDialog;

    private FunctionConfig functionConfig;
    private File file;
    private String openTvPic;
    private String loginCMPic;
    private String shelfDisplayPic;
    private String acceptPic;
    private String statementPic;
    /**
     * 类型：1,开启电视机广告;2,收银机登录收银系统;3,货架商品陈列样式;4,加密验收单;
     */
    private int type = 1;
    private String taskId;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("申请租金补贴");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("申请租金补贴");
        MobclickAgent.onPause(aty);
    }

    /**
     * 图片高度设置
     */
    private void setImageHeight() {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) rlOpenTvPic.getLayoutParams();
        int width = (DensityUtils.getScreenW(aty) - getResources().getDimensionPixelSize(R.dimen.pad30)) / 2;
        int height = width * 3 / 4;
        JCLoger.debug(width + "-----" + height);
        linearParams.height = height;
        rlOpenTvPic.setLayoutParams(linearParams);
        rlLoginCMPic.setLayoutParams(linearParams);
        rlShelfDisplayPic.setLayoutParams(linearParams);
        rlAcceptPic.setLayoutParams(linearParams);
    }


    @Override
    public void initWidget() {
        textTitle.setText("申请租金补贴");
        btnRight.setVisibility(View.GONE);
        setImageHeight();
        img_Open_Tv.setOnClickListener(new ImgClickListenner(aty, R.drawable.open_tv));
        img_Login_Cm.setOnClickListener(new ImgClickListenner(aty, R.drawable.login_cm));
        img_Shelf_Display.setOnClickListener(new ImgClickListenner(aty, R.drawable.shelf_display));
        img_Accept2.setOnClickListener(new ImgClickListenner(aty, R.drawable.accept2));
        detachLayout();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskId = bundle.getString("TskId");
        }

        functionConfig = initGalleryFinal(aty);
        picDiaolog = new ChoicePicDiaolog(aty);
        picDiaolog.setOnChoicePicCLickListener(onChoicePicCLickListener);
        chekIsFailed(taskId);
    }

    /**
     * 检查任务是否失败
     */
    private void chekIsFailed(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            toast("任务id为空.");
            finish();
            return;
        }
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        httpRequestService.doRequestData(aty, "User/CheckTaskClassDetial", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!StringUtils.isEmpty(resultParam.mapData.get("Reason"))) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Reason", resultParam.mapData.get("Reason"));
                        skipActivityForResult(aty, TaskFailActivity.class, bundle, TASKFAILED_CODE);
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else if (Constants.M02.equals(resultParam.resultId)) {
                    toast(resultParam.message);
                } else {
                    toast(resultParam.message + "");
                }
//                Bundle bundle = new Bundle();
//                bundle.putString("Reason","测试测试");
//                skipActivityForResult(aty,TaskFailActivity.class,bundle,TASKFAILED_CODE);
            }
        });
    }

    /**
     * 相机相册弹框回调
     */
    ChoicePicDiaolog.OnChoicePicCLickListener onChoicePicCLickListener = new ChoicePicDiaolog.OnChoicePicCLickListener() {
        @Override
        public void onCameraClick() {
            if (SystemTool.checkSelfPermission(aty, Manifest.permission.CAMERA) || SystemTool.checkSelfPermission(aty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.Permission.CAMERA);
            } else {
                chooseFromCamera();
            }
        }

        @Override
        public void onGalleryClick() {
            if (SystemTool.checkSelfPermission(aty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.Permission.EXTERNAL_STORAGE);
            } else {
                chooseFromGallery();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitDialog();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                showExitDialog();
                break;
            case R.id.rlOpenTvPic:
                type = 1;
                picDiaolog.show();
                break;
            case R.id.rlLoginCMPic:
                type = 2;
                picDiaolog.show();
                break;
            case R.id.rlShelfDisplayPic:
                type = 3;
                picDiaolog.show();
                break;
            case R.id.rlAcceptPic:
                type = 4;
                picDiaolog.show();
                break;
            case R.id.btnUpDate:
                if (StringUtils.isEmpty(openTvPic)) {
                    toast("请上传开启电视机广告");
                    return;
                }
                if (StringUtils.isEmpty(loginCMPic)) {
                    toast("请上传收银机登录收银系统");
                    return;
                }
                if (StringUtils.isEmpty(shelfDisplayPic)) {
                    toast("请上传货架商品陈列样式");
                    return;
                }
                if (StringUtils.isEmpty(acceptPic)) {
                    toast("请上传加盟验收单");
                    return;
                }
                doUploadDoorPic();
                break;
        }
    }

    /**
     * 退出提示对话框
     */
    public void showExitDialog() {
        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
            @Override
            public void onOkClick() {
                onBackPressed();
            }
        });
        nomalDialog.show("还未提交，确认关闭吗？");
    }

    /**
     * JSON上传头像
     */
    private void doSaveUserHead(byte[] byte1, String fileName) {
        JCLoger.debug("图片大小----" + byte1.length);
        params.clear();
        params.put("FileName", fileName);
        params.put("Pic", byte1);
        httpRequestService.doRequestData(aty, "ImgUpload/UploadAndroid", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    toast("照片上传成功");
                    //删除本地照片
                    SDCardUtils.deleteFie(SDCardUtils.DIR_PATH, TMP_FILE_NAME);
                    switch (type) {//类型：1,门头全景;2,门头编号特写;3,门头制作发票;4,门头验收单;5,非法人手写申明
                        case 1:
                            openTvPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(openTvPic) ? "0" : openTvPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageOpenTvPic);
                            textOpenTvPic.setVisibility(View.VISIBLE);
                            imageOpenTvPicShade.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            loginCMPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(loginCMPic) ? "0" : loginCMPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageLoginCMPic);
                            textLoginCMPic.setVisibility(View.VISIBLE);
                            imageLoginCMPicShade.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            shelfDisplayPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(shelfDisplayPic) ? "0" : shelfDisplayPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageShelfDisplayPic);
                            textShelfDisplayPic.setVisibility(View.VISIBLE);
                            imageShelfDisplayPicShade.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            acceptPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(acceptPic) ? "0" : acceptPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageAcceptPic);
                            textAcceptPic.setVisibility(View.VISIBLE);
                            imageAcceptPicShade.setVisibility(View.VISIBLE);
                            break;
                    }
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 上传数据
     */
    private void doUploadDoorPic() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("StoreName", MyApplication.instance.StoreSerialNameDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        params.put("OpenTvPic", openTvPic);
        params.put("LoginCMPic", loginCMPic);
        params.put("ShelfDisplayPic", shelfDisplayPic);
        params.put("AcceptPic", acceptPic);
        httpRequestService.doRequestData(aty, "User/UploadRentPic", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    skipActivity(aty, UpdateSucActivity.class);
                    finish();
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
     * 拍照选择图片
     */
    private void chooseFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            file = SDCardUtils.createFile(TMP_FILE_NAME);
            Uri uri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(aty, Constants.fileProvider, file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            toast("未发现相机");
        }
    }

    /**
     * 从相册选择图片
     */
    private void chooseFromGallery() {
        GalleryFinal.openGallerySingle(GALLERY_CODE, functionConfig, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.Permission.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFromCamera();
                } else {
                    toast("需要此权限才能打开相机或相册，请到设置里面打开");
                }
                break;
            case Constants.Permission.EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFromGallery();
                } else {
                    toast("需要此权限才能才能打开相册，请到设置里面打开");
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TASKFAILED_CODE:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
            case CAMERA_CODE://照相
                if (resultCode == RESULT_OK) {
                    UIHelper.showLoadDialog(aty);
                    if (data != null) {
                        final Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Uri uri = (Uri) bundle.getSerializable(MediaStore.EXTRA_OUTPUT);
                                    File fileTmp = new File(uri.getPath());
                                    JCLoger.debug(fileTmp.getAbsolutePath() + "----" + fileTmp.length());
                                    ImageReduce(fileTmp);
                                }
                            }).start();
                        }
                    } else {
                        JCLoger.debug("data为空---");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    if (file.length() > 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                JCLoger.debug(file.getAbsolutePath() + "----" + file.length());
                                ImageReduce(file);
                            }
                        }).start();
                    }
                }
                break;
        }
    }

    /**
     * 压缩
     *
     * @param file
     */
    private void ImageReduce(File file) {
        Bitmap bitmap = ImageUtils.reduce(BitmapFactory.decodeFile(file.getAbsolutePath()), 400, 300, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bitmap = null;

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("stream", stream);
        map.put("file", file);
        Message message = new Message();
        message.what = UPDATE_CODE;
        message.obj = map;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CODE:
                    ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) msg.obj;
                    File file = (File) map.get("file");
                    doSaveUserHead(((ByteArrayOutputStream) map.get("stream")).toByteArray(), file.getName());
                    break;
            }
        }
    };

    @Override
    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
        switch (reqeustCode) {
            case GALLERY_CODE://相册
                if (resultList != null) {
                    UIHelper.showLoadDialog(aty);
                    for (final PhotoInfo photoInfo : resultList) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JCLoger.debug(photoInfo.getPhotoPath());
                                File file = new File(photoInfo.getPhotoPath());
                                JCLoger.debug("文件大小-------" + file.length());

                                ImageReduce(file);
                            }
                        }).start();
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {
        if (!StringUtils.isEmpty(errorMsg)) {
            JCLoger.debug(errorMsg);
        }
    }
}
