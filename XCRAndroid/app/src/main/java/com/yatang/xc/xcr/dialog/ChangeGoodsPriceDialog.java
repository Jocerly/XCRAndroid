package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.ViewInject;

/**
 * 修改商品价格弹框
 * Created by Jocerly on 2017/3/20.
 */

public class ChangeGoodsPriceDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView textTitle;
    private TextView textName;
    private EditText editGoodsPrice;
    private Button btnOk;
    private Button btnCancle;
    private OnChangeGoodsPriceClickListener onChangeGoodsPriceClickListener;
    private String goodsId, price, goodsName, goodsCode,goodsPrice;

    public ChangeGoodsPriceDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_goods_price);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        textTitle = (TextView)findViewById(R.id.textTitle);
        textName = (TextView)findViewById(R.id.textName);
        editGoodsPrice = (EditText) findViewById(R.id.editGoodsPrice);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancle = (Button)findViewById(R.id.btnCancle);
        btnOk.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void show(String goodsId, String goodsCode, String goodsName,String price) {
        super.show();
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsCode = goodsCode;
        this.price=price;
        editGoodsPrice.setText(price);
        editGoodsPrice.setSelection(editGoodsPrice.getText().length());
        textName.setText(goodsName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancle:
                break;
            case R.id.btnOk:
                price = editGoodsPrice.getText().toString().trim();
                if (org.jocerly.jcannotation.utils.StringUtils.isEmpty(price) ) {
                    ViewInject.toast(context, "请输入新价格");
                    return;
                } else if (isPriceOK(price).length()>0){
                    return;
                }
                onChangeGoodsPriceClickListener.onOkClick(goodsId, goodsCode, price);
                break;
        }
        dismiss();
    }

    /**
     * 判断售价的长度和小数点后允许的位数
     * @param price 用户输入的价格
     * @return 返回错误提示 如果长度为0 则没有错误
     */
    private String isPriceOK(String price) {
        String result = "";
        boolean isLengthOk = true;
        boolean isPointOK = true;
        int position = 0;
        int length = price.length();
        if(price.contains(".")) {
            position = price.indexOf(".");
            if (length - position > 3) {
                isPointOK = false;
            }
        }
        if (position > 0) {
            if (position > 9) {
                isLengthOk = false;
            }
        } else {
            if (length > 9) {
                isLengthOk = false;
            }
        }
        if(!isLengthOk) {
            result = "售价过大，请修改";
            if(!isPointOK) {
                result = "售价过大，小数点后面最多2位";
            }
        }else {
            if(!isPointOK) {
                result = "售价小数点后面最多2位";
            }
        }
        if(result.length()>0) {
            ViewInject.toast(context, result);
        }
        return result;
    }

    /**
     * 回调接口
     *
     * @author Jocerly
     *
     */
    public interface OnChangeGoodsPriceClickListener {
        public void onOkClick(String goodsId, String goodsCode, String price);
    }

    public void setOnChangeGoodsPriceClickListener(OnChangeGoodsPriceClickListener onChangeGoodsPriceClickListener) {
        this.onChangeGoodsPriceClickListener = onChangeGoodsPriceClickListener;
    }
}
