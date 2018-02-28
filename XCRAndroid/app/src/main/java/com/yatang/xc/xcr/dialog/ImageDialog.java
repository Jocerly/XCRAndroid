package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.views.MyImageView;
import com.yatang.xc.xcr.views.ZoomImageView;

import org.jocerly.jcannotation.ui.ViewInject;

import java.io.File;

/**
 * Created by dengjiang on 2017/3/31.
 */

public class ImageDialog extends Dialog {

    private Context context;
    private ZoomImageView img;
    private RelativeLayout linearLayout;
    public ImageDialog(Context context) {
        super(context, R.style.ImageDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_img);
        img = (ZoomImageView) findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        linearLayout = (RelativeLayout)findViewById(R.id.ll);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void showPicDialog(int resource) {
           super.show();
        if(img != null) {
            img.setImageResource(resource);
            startAnimations(this);
        }else {
            dismiss();
        }
    }
    public void showPicDialog(Drawable drawable) {
           super.show();
           if(img != null) {
               img.setImageDrawable(drawable);
               startAnimations(this);
           }else {
               dismiss();
           }

    }

    public void showPicDialog(String url) {
        super.show();
        if(img != null) {
            Bitmap bmp = BitmapFactory.decodeFile(url);
            if(bmp != null) {
                img.setImageBitmap(bmp);
                startAnimations(this);
                return;
            }
        }
            dismiss();

    }
    private void startAnimations(Dialog dia) {
        Animation Anim = AnimationUtils.loadAnimation(dia.getContext(),R.anim.gf_flip_horizontal_in);
        img.setAnimation(Anim);
    }
}
