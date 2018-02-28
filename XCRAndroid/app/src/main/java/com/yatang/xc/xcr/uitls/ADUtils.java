package com.yatang.xc.xcr.uitls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * 广告获取工具类
 * Created by dengjiang on 2017/9/6.
 */
public class ADUtils {
    public static final int UPDATE_TEAY_TIME = 0X01;
    public static final int ONAD_FINISHED = 0X02;
    public String PATH = "";//缓存目录
    private int delayTime = 5;// 广告4秒倒计时
    private ADCallBack aDCallBack;//广告回调
    private HttpRequestService httpRequestService;
    private HashMap<String, Object> params;
    private Context context;
    private Handler handler;
    private ImageView img;//广告图片
    private TextView text_Time;//广告倒计时
    private LinearLayout btn_Skip;//跳过广告按钮
    private boolean isSkip;
    private String SP_AdPic;//缓存广告图片的地址
    private String SP_AdJump;//缓存广告的跳转地址
    private String AdPic;//广告图片的地址
    private String AdJump;//广告的跳转地址
    private boolean isJump;
    private boolean isShowingAD;

    public ADUtils(HttpRequestService httpRequestService, HashMap<String, Object> params, Context context) {
        isSkip = false;
        isJump = false;
        isShowingAD = false;
        this.httpRequestService = httpRequestService;
        this.params = params;
        this.context = context;
        PATH = context.getFilesDir().toString() + "/XCRAndroid";
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_TEAY_TIME:
                        if (delayTime > 0 && !isSkip && !isJump) {
                            handler.sendEmptyMessageDelayed(UPDATE_TEAY_TIME, 1000);
                            text_Time.setText(delayTime + "秒");
                            delayTime--;
                        } else {
                            if (!isJump && !isSkip) {
                                //到计时完成
                                aDCallBack.onADfinished();
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    /**
     * 设置显示广告图片的UI
     *
     * @param img 显示广告图片的ImageView
     */
    public ADUtils setADImg(ImageView img) {
        this.img = img;
        return this;
    }

    /**
     * 设置显示广告倒计时的UI
     *
     * @param text_Time 显示广告倒计时的textview
     */
    public ADUtils setADText(TextView text_Time) {
        this.text_Time = text_Time;
        return this;
    }

    private void restoreUI() {
        text_Time.setVisibility(View.GONE);
        btn_Skip.setVisibility(View.GONE);
    }

    /**
     * 设置跳过广告的button
     *
     * @param btn_Skip 跳过广告的按钮
     */
    public ADUtils setADBtn(final LinearLayout btn_Skip) {
        this.btn_Skip = btn_Skip;
        this.btn_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSkip = true;
                aDCallBack.onADfinished();
            }
        });
        return this;
    }

    /**
     * 设置回调监听器
     *
     * @param getADCallBack 回调监听器
     */
    public ADUtils setGetADCallBack(ADCallBack getADCallBack) {
        this.aDCallBack = getADCallBack;
        return this;
    }

    /**
     * 获取并显示广告图片
     */
    public void getADInfo() {
        SP_AdPic = Common.getAppInfo(context, Constants.Preference.ADPicUrl, "");
        SP_AdJump = Common.getAppInfo(context, Constants.Preference.AdJumpUrl, "");
        JCLoger.debug("getADInfo=" + SP_AdPic);
        if (!StringUtils.isEmpty(SP_AdPic) && !"-".equals(SP_AdPic)) {
            //本地有缓存图片
            showADUI();
            isShowingAD = true;
        }
        if (!Common.isNetWorkOK(context)) {
            if (!isShowingAD) {
                aDCallBack.onADfinished();
            }
            return;
        }
        params.clear();
        httpRequestService.doRequestData(context, "System/HomeAd", false, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    AdPic = resultParam.mapData.get("AdPic");
                    AdJump = resultParam.mapData.get("AdJump");
                    if (StringUtils.isEmpty(AdPic)) {
                        //说明后台没有配的有广告图片 需要删除 本地缓存广告
                        Common.setAppInfo(context, Constants.Preference.ADPicUrl, "-");
                        Common.setAppInfo(context, Constants.Preference.AdJumpUrl, "-");
                    } else {
                        Common.setAppInfo(context, Constants.Preference.ADPicUrl, AdPic);
                        Common.setAppInfo(context, Constants.Preference.AdJumpUrl, trimAdUrl(AdJump));
                    }
                    //保存网络图片到本地
                    if (!StringUtils.isEmpty(AdPic) && !SP_AdPic.equals(AdPic)) {
                        new TaskLoadBitmap().execute(AdPic);
                    }
                    if (!isShowingAD) {
                        aDCallBack.onADfinished();
                    }
                } else {
                    if (!isShowingAD) {
                        aDCallBack.onADfinished();
                    }
                }
            }
        });

    }

    /**
     * 显示广告图片
     */
    private void showADUI() {
        text_Time.setVisibility(View.VISIBLE);
        text_Time.setText(delayTime + "秒");
        btn_Skip.setVisibility(View.VISIBLE);
        img.setImageBitmap(getImageByFile(PATH + "/AD.temp"));
        if (!StringUtils.isEmpty(SP_AdJump)) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!"-".equals(SP_AdJump)) {
                        //跳转广告;
                        aDCallBack.onShowAD(SP_AdJump);
                        isJump = true;
                    }
                }
            });
        }
        handler.sendEmptyMessageDelayed(UPDATE_TEAY_TIME, 500);
    }

    public interface ADCallBack {
        /**
         * 广告显示完成
         */
        void onADfinished();

        /**
         * 跳转到广告详情页面
         */
        void onShowAD(String url);
    }


    /**
     * 异步线程下载图片 保存图片
     */
    class TaskLoadBitmap extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            Common.setAppInfo(context, Constants.Preference.ADPicUrl, AdPic);
            Common.setAppInfo(context, Constants.Preference.AdJumpUrl, trimAdUrl(AdJump));
            try {
                SavaImage(getBitmapFormUri(context, params[0]));
            } catch (Exception e) {
                e.printStackTrace();
                Common.setAppInfo(context, Constants.Preference.ADPicUrl, "-");
                Common.setAppInfo(context, Constants.Preference.AdJumpUrl, "-");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    /**
     * 获取本地图片
     *
     * @param filePath 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap getImageByFile(String filePath) {
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Common.setAppInfo(context, Constants.Preference.ADPicUrl, "-");
            Common.setAppInfo(context, Constants.Preference.AdJumpUrl, "-");
        }
        return bitmap;
    }


    /**
     * 通过uri获取图片并进行压缩
     *
     * @param imageurl
     */
    public Bitmap getBitmapFormUri(Context ac, String imageurl) throws Exception {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        url = new URL(imageurl);
        connection = (HttpURLConnection) url.openConnection();
        InputStream input = new BufferedInputStream(connection.getInputStream());
        /**
         * BufferedInputStream类调用mark(int readlimit)方法后读取多少字节标记才失效，是取readlimit和BufferedInputStream类的缓冲区大小两者中的最大值，而并非完全由readlimit确定。这个在JAVA文档中是没有提到的。
         */
        input.mark(input.available());
        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, boundsOptions);
        connection.disconnect();
        int originalWidth = boundsOptions.outWidth;
        int originalHeight = boundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1)) {
            throw new Exception();
        }

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        boundsOptions.inSampleSize = be;//设置缩放比例
        boundsOptions.inJustDecodeBounds = false;
        connection = (HttpURLConnection) url.openConnection();
        input = new BufferedInputStream(connection.getInputStream());
        input.mark(input.available());
        bitmap = BitmapFactory.decodeStream(input, null, boundsOptions);
        connection.disconnect();
        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap getBitmapByURL(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Common.setAppInfo(context, Constants.Preference.ADPicUrl, "-");
            Common.setAppInfo(context, Constants.Preference.AdJumpUrl, "-");
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @return void
     */
    public void SavaImage(Bitmap bitmap) throws Exception {
        File file = new File(PATH);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        fileOutputStream = new FileOutputStream(PATH + "/AD.temp");
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
    }

    /**
     * 对广告地址进行前后去空格处理，以及空地址返回 “-”
     * @param url
     * @return 处理后的url
     */
    private String trimAdUrl(String url) {
        url = url.trim();
        return StringUtils.isEmpty(url) ? "-":url;
    }
}
