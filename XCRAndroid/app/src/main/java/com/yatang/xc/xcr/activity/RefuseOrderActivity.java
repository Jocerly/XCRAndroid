package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 拒绝订单页面
 * Created by DengJiang on 2017/6/9.
 */
@ContentView(R.layout.activity_refuse_order)
public class RefuseOrderActivity extends BaseActivity {

    public static final String REFUSE_TYPE_CUSTOM = "2";//客户拒绝
    public static final String REFUSE_TYPE_BUSINESS = "1";//商家拒绝
    public static final String REFUSE_TYPE_RETURN_GOODS = "3";
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.tipSelect)
    private TextView tipSelect;
    @BindView(id = R.id.rb1, click = true)
    private RadioButton rb1;
    @BindView(id = R.id.rb2, click = true)
    private RadioButton rb2;
    @BindView(id = R.id.rb3, click = true)
    private RadioButton rb3;
    @BindView(id = R.id.rb4, click = true)
    private RadioButton rb4;
    @BindView(id = R.id.rb5, click = true)
    private RadioButton rb5;
    @BindView(id = R.id.lin5)
    private LinearLayout lin5;
    @BindView(id = R.id.lin4)
    private LinearLayout lin4;
    @BindView(id = R.id.lin3)
    private LinearLayout lin3;

    @BindView(id = R.id.rbOther, click = true)
    private RadioButton rbOther;
    @BindView(id = R.id.editReson)
    private EditText editReson;
    @BindView(id = R.id.textTip)
    private TextView textTip;

    @BindView(id = R.id.linOtherReason)
    private LinearLayout linOtherReason;
    @BindView(id = R.id.btnConfirm, click = true)
    private TextView btnConfirm;

    private String refuseType = "";
    private String OrderNo = "";
    private String type = REFUSE_TYPE_BUSINESS;


    @Override
    public void initWidget() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            OrderNo = bundle.getString("OrderNo", "");
            type = bundle.getString("Type", "");
        }
    }

    @Override
    public void initData() {
        if (REFUSE_TYPE_BUSINESS.equals(type)) {
            //商家拒绝
            textTip.setText("原因会告知下单客户，务必认真填写。");
            textTitle.setText("拒绝接单");
            tipSelect.setText("选择拒绝原因");
            btnConfirm.setText("确定并关闭订单");
        } else if (REFUSE_TYPE_CUSTOM.equals(type)) {
            //客户拒绝
            textTip.setText("平台会抽查回访客户，务必认真填写！");
            textTitle.setText("客户拒收");
            tipSelect.setText("选择拒收原因");
            btnConfirm.setText("确定并关闭订单");
        } else if (REFUSE_TYPE_RETURN_GOODS.equals(type)) {
            //拒绝退货
            textTitle.setText("拒绝退货");
            textTip.setText("");
            tipSelect.setText("选择拒绝原因");
            btnConfirm.setText("确定");
        }
        refuseType = "";
        initRadioButton();
    }

    private void initRadioButton() {
        switch (type) {
            case REFUSE_TYPE_BUSINESS:
                rb1.setText("不在配送范围");
                rb2.setText("商品已售完");
                rb3.setText("电话联系不上");
                rb4.setText("客户不买了");
                rb5.setText("不在营业时间");
                break;
            case REFUSE_TYPE_CUSTOM:
                rb1.setText("客户不买了");
                rb2.setText("电话联系不上");
                rb3.setText("客户重复下单");
                rb4.setText("客户下错单");
                rb5.setVisibility(View.GONE);
                lin5.setVisibility(View.GONE);
                break;
            case REFUSE_TYPE_RETURN_GOODS:
                rb1.setText("商品已影响正常销售");
                rb2.setText("该商品不支持退换货");
                lin3.setVisibility(View.GONE);
                lin4.setVisibility(View.GONE);
                lin5.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);
                rb4.setVisibility(View.GONE);
                rb5.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rb1:
                linOtherReason.setVisibility(View.GONE);
                textTip.setVisibility(View.GONE);
                refuseType = rb1.getText().toString().trim();
                break;
            case R.id.rb2:
                linOtherReason.setVisibility(View.GONE);
                textTip.setVisibility(View.GONE);
                refuseType = rb2.getText().toString().trim();
                break;
            case R.id.rb3:
                linOtherReason.setVisibility(View.GONE);
                textTip.setVisibility(View.GONE);
                refuseType = rb3.getText().toString().trim();
                break;
            case R.id.rb4:
                linOtherReason.setVisibility(View.GONE);
                textTip.setVisibility(View.GONE);
                refuseType = rb4.getText().toString().trim();
                break;
            case R.id.rb5:
                linOtherReason.setVisibility(View.GONE);
                textTip.setVisibility(View.GONE);
                refuseType = rb5.getText().toString().trim();
                break;
            case R.id.rbOther:
                linOtherReason.setVisibility(View.VISIBLE);
                textTip.setVisibility(View.VISIBLE);
                refuseType = "";
                break;
            case R.id.btnLeft:
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.btnConfirm:
                if (linOtherReason.getVisibility() == View.VISIBLE) {
                    refuseType = editReson.getText().toString().trim();
                    if (StringUtils.isEmpty(refuseType)) {
                        toast("请输入原因");
                        return;
                    } else if (containsEmoji(refuseType)) {
                        toast("不支持表情符号");
                        return;
                    }
                }
                if (StringUtils.isEmpty(refuseType)) {
                    toast("请选择原因");
                    return;
                }
                Intent i = new Intent();
                i.putExtra("refuseType", refuseType);
                i.putExtra("OrderNo", OrderNo);
                setResult(RESULT_OK, i);
                finish();
                break;
        }
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
}
