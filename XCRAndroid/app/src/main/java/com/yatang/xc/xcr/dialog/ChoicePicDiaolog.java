package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

/**
 * 相机和相册选择
 * Created by Jocerly on 2017/3/28.
 */

public class ChoicePicDiaolog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textCamera;
    private TextView textGallery;
    private TextView textCancle;

    private OnChoicePicCLickListener onChoicePicCLickListener;

    public ChoicePicDiaolog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_pic);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        textCamera = (TextView) findViewById(R.id.textCamera);
        textGallery = (TextView) findViewById(R.id.textGallery);
        textCancle = (TextView) findViewById(R.id.textCancle);

        textCamera.setOnClickListener(this);
        textGallery.setOnClickListener(this);
        textCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textCamera:
                onChoicePicCLickListener.onCameraClick();
                break;
            case R.id.textGallery:
                onChoicePicCLickListener.onGalleryClick();
                break;
            case R.id.textCancle:
                break;
        }
        dismiss();
    }

    /**
     * 回调接口
     *
     * @author Jocerly
     */
    public interface OnChoicePicCLickListener {
        public void onCameraClick();
        public void onGalleryClick();
    }

    public void setOnChoicePicCLickListener(OnChoicePicCLickListener onChoicePicCLickListener) {
        this.onChoicePicCLickListener = onChoicePicCLickListener;
    }
}
