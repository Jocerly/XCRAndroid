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
 * 拍门头照上传Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_upload_door_pic)
public class UploadDoorPicActivity extends BaseActivity implements GalleryFinal.OnHanlderResultCallback {
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

    //门头全景
    @BindView(id = R.id.rlPanoraPic, click = true)
    private RelativeLayout rlPanoraPic;
    @BindView(id = R.id.imagePanoraPic)
    private ImageView imagePanoraPic;
    @BindView(id = R.id.textPanoraPic)
    private TextView textPanoraPic;
    @BindView(id = R.id.imagePanoraPicShade)
    private ImageView imagePanoraPicShade;
    //门头编号特写
    @BindView(id = R.id.rlNoPic, click = true)
    private RelativeLayout rlNoPic;
    @BindView(id = R.id.imageNoPic)
    private ImageView imageNoPic;
    @BindView(id = R.id.textNoPic)
    private TextView textNoPic;
    @BindView(id = R.id.imageNoPicShade)
    private ImageView imageNoPicShade;
    //门头制作发票
    @BindView(id = R.id.rlInvoicePic, click = true)
    private RelativeLayout rlInvoicePic;
    @BindView(id = R.id.imageInvoicePic)
    private ImageView imageInvoicePic;
    @BindView(id = R.id.textInvoicePic)
    private TextView textInvoicePic;
    @BindView(id = R.id.imageInvoicePicShade)
    private ImageView imageInvoicePicShade;
    //门头验收单
    @BindView(id = R.id.rlAcceptPic, click = true)
    private RelativeLayout rlAcceptPic;
    @BindView(id = R.id.imageAcceptPic)
    private ImageView imageAcceptPic;
    @BindView(id = R.id.textAcceptPic)
    private TextView textAcceptPic;
    @BindView(id = R.id.imageAcceptPicShade)
    private ImageView imageAcceptPicShade;
    @BindView(id = R.id.btnUpDate, click = true)
    private TextView btnUpDate;

    @BindView(id = R.id.img_Panora)
    private ImageView img_Panora;
    @BindView(id = R.id.img_Number)
    private ImageView img_Number;
    @BindView(id = R.id.img_Invoice)
    private ImageView img_Invoice;
    @BindView(id = R.id.img_Accept)
    private ImageView img_Accept;

    private ChoicePicDiaolog picDiaolog;
    private NomalDialog nomalDialog;

    private FunctionConfig functionConfig;
    private File file;
    private String panoraPic;
    private String noPic;
    private String invoicePic;
    private String acceptPic;
    private String statementPic;
    /**
     * 类型：1,门头全景;2,门头编号特写;3,门头制作发票;4,门头验收单;
     */
    private int type = 1;
    private String taskId;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("门头照");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("门头照");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("拍门头照");
        btnRight.setVisibility(View.GONE);
        setImageHeight();

        img_Panora.setOnClickListener(new ImgClickListenner(aty, R.drawable.panora));
        img_Number.setOnClickListener(new ImgClickListenner(aty, R.drawable.no));
        img_Invoice.setOnClickListener(new ImgClickListenner(aty, R.drawable.invoice));
        img_Accept.setOnClickListener(new ImgClickListenner(aty, R.drawable.accept));
        detachLayout();
    }

    /**
     * 图片高度设置
     */
    private void setImageHeight() {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) rlPanoraPic.getLayoutParams();
        int width = (DensityUtils.getScreenW(aty) - getResources().getDimensionPixelSize(R.dimen.pad30)) / 2;
        int height = width * 3 / 4;
        JCLoger.debug(width + "-----" + height);
        linearParams.height = height;
        rlPanoraPic.setLayoutParams(linearParams);
        rlNoPic.setLayoutParams(linearParams);
        rlInvoicePic.setLayoutParams(linearParams);
        rlAcceptPic.setLayoutParams(linearParams);
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

        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListene);
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
//                                        Bundle bundle = new Bundle();
//                        bundle.putString("Reason","测试测试");
//                skipActivityForResult(aty,TaskFailActivity.class,bundle,TASKFAILED_CODE);
            }
        });
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            onBackPressed();
        }
    };

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
            nomalDialog.show("还未提交，确认关闭吗？");
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                nomalDialog.show("还未提交，确认关闭吗？");
                break;
            case R.id.rlPanoraPic:
                type = 1;
                picDiaolog.show();
                break;
            case R.id.rlNoPic:
                type = 2;
                picDiaolog.show();
                break;
            case R.id.rlInvoicePic:
                type = 3;
                picDiaolog.show();
                break;
            case R.id.rlAcceptPic:
                type = 4;
                picDiaolog.show();
                break;
            case R.id.btnUpDate:
                if (StringUtils.isEmpty(panoraPic)) {
                    toast("请上传门头全景");
                    return;
                }
                if (StringUtils.isEmpty(noPic)) {
                    toast("请上传门头编号特写");
                    return;
                }
                if (StringUtils.isEmpty(invoicePic)) {
                    toast("请上传门头制作发票");
                    return;
                }
                if (StringUtils.isEmpty(acceptPic)) {
                    toast("请上传门头验收单");
                    return;
                }
                doUploadDoorPic();
                break;
        }
    }

    /**
     * JSON上传图片
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
                            panoraPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(panoraPic) ? "0" : panoraPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imagePanoraPic);
                            textPanoraPic.setVisibility(View.VISIBLE);
                            imagePanoraPicShade.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            noPic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(noPic) ? "0" : noPic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageNoPic);
                            textNoPic.setVisibility(View.VISIBLE);
                            imageNoPicShade.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            invoicePic = resultParam.mapData.get("Url");
                            Picasso.with(aty)
                                    .load(StringUtils.isEmpty(invoicePic) ? "0" : invoicePic)
                                    .error(R.drawable.defualt_img3)
                                    .placeholder(R.drawable.defualt_img3)
                                    .into(imageInvoicePic);
                            textInvoicePic.setVisibility(View.VISIBLE);
                            imageInvoicePicShade.setVisibility(View.VISIBLE);
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
        params.put("StoreName",MyApplication.instance.StoreSerialNameDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TaskId", taskId);
        params.put("PanoraPic", panoraPic);
        params.put("NoPic", noPic);
        params.put("InvoicePic", invoicePic);
        params.put("AcceptPic", acceptPic);
        httpRequestService.doRequestData(aty, "User/UploadDoorPic", params, new HttpRequestService.IHttpRequestCallback() {

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
                                    JCLoger.debug(fileTmp.getAbsolutePath()+"----"+fileTmp.length());
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
                                JCLoger.debug(file.getAbsolutePath()+"----"+file.length());
                                ImageReduce(file);
                            }
                        }).start();
                    }
                }
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CODE:
                    ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) msg.obj;
                    File file = (File) map.get("file");
                    doSaveUserHead(((ByteArrayOutputStream)map.get("stream")).toByteArray(), file.getName());
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

    /**
     * 压缩
     *
     * @param file
     */
    private void ImageReduce(File file) {
        Bitmap bitmap = ImageUtils.reduce(BitmapFactory.decodeFile(file.getAbsolutePath()), 400, 300, true);
        //                        Bitmap bitmap = BitmapUtil.decodeBitmapFromPath(photoInfo.getPhotoPath(), 400, 300);
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

    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {
        if (!StringUtils.isEmpty(errorMsg)) {
            JCLoger.debug(errorMsg);
        }
    }
}
