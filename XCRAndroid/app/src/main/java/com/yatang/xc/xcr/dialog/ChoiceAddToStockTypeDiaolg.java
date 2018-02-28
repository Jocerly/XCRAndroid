package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

/**
 * 选择入库方式弹框
 * Created by Jocerly on 2017/5/25.
 */

public class ChoiceAddToStockTypeDiaolg extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textFromGoods;
    private TextView textScan;
    private TextView textCancle;

    private OnChoiceClickListener onChoiceClickListener;

    public ChoiceAddToStockTypeDiaolg(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_add_to_stock);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        textFromGoods = (TextView) findViewById(R.id.textFromGoods);
        textFromGoods.setOnClickListener(this);
        textScan = (TextView) findViewById(R.id.textScan);
        textScan.setOnClickListener(this);
        textCancle = (TextView) findViewById(R.id.textCancle);
        textCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textFromGoods:
                onChoiceClickListener.onChoiceClick();
                break;
            case R.id.textScan:
                onChoiceClickListener.onScanClick();
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
    public interface OnChoiceClickListener {
        /**
         * 搜索选择商品
         */
        public void onChoiceClick();

        /**
         * 扫码查询商品
         */
        public void onScanClick();
    }

    public void setOnChoiceClickListener(OnChoiceClickListener onChoiceClickListener) {
        this.onChoiceClickListener = onChoiceClickListener;
    }
}
