package com.yatang.xc.xcr.uitls;

import android.content.Context;
import android.view.View;

import com.yatang.xc.xcr.dialog.ImageDialog;

/**
 * Created by dengjiang on 2017/3/31.
 */

public class ImgClickListenner implements View.OnClickListener{
    private int imgResource;
    private Context context;
    private ImageDialog imgDialog;
    public ImgClickListenner(Context context, int imgResource) {
        this.context = context;
        this.imgResource = imgResource;
    }
    @Override
    public void onClick(View view) {
        imgDialog = new ImageDialog(context);
        imgDialog.showPicDialog(imgResource);
    }
}
