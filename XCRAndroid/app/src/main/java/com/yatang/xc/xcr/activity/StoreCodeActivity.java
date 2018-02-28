package com.yatang.xc.xcr.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.ZXingUtils;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.SDCardUtils;
import org.jocerly.jcannotation.utils.StringUtils;


/**
 * 店铺推荐二维码
 * Created by Jocerly on 2017/10/18.
 */
@ContentView(R.layout.activity_store_code)
public class StoreCodeActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.imgStoreCode)
    private ImageView imgStoreCode;
    @BindView(id = R.id.btnSave, click = true)
    private TextView btnSave;

    private String codeUrl;
    private String storeSerialNo;
    private Bitmap BitmapCode;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("店铺推荐二维码");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("店铺推荐二维码");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("店铺推荐二维码");
        btnRight.setText("统计");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            codeUrl = bundle.getString("StoreCodeUrl");
            storeSerialNo = bundle.getString("StoreSerialNo");
            if (!StringUtils.isEmpty(codeUrl)) {
                Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.store_logo);
                BitmapCode = ZXingUtils.createQRLogoImage(codeUrl, logoBitmap);
                imgStoreCode.setImageBitmap(BitmapCode);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                if (!StringUtils.isEmpty(storeSerialNo)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("StoreSerialNo", storeSerialNo);
                    skipActivity(aty, StoreRecomeStatisticsActivity.class, bundle);
                }
                break;
            case R.id.btnSave:
                saveCodeToAlbum();
                break;
        }
    }

    /**
     * 保存图片到相册
     */
    private void saveCodeToAlbum() {
        Bitmap codeBg = BitmapFactory.decodeResource(getResources(), R.drawable.store_code_bg);

        int w = codeBg.getWidth();
        int h = codeBg.getHeight();
        int codeW = BitmapCode.getWidth();
        int codeH = BitmapCode.getHeight();
        PointF fromPoint = new PointF(w / 2 - codeW / 2, h / 2 - 60);

        Bitmap bitmap = ZXingUtils.mixtureBitmap(codeBg, BitmapCode, fromPoint);

        if (SDCardUtils.saveData(aty, bitmap, SDCardUtils.DCIM_PATH, "店铺推荐二维码.jpg")) {
            toast("保存成功，请到相册中查看");
            MobclickAgent.onEvent(aty, "Firm_Store_Code");
        } else {
            toast("保存失败");
        }
    }
}
