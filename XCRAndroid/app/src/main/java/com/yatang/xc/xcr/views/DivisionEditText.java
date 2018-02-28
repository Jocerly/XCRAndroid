package com.yatang.xc.xcr.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 四个字符用空格隔开
 * Created by dengjiang on 2017/8/7.
 */

public class DivisionEditText extends android.support.v7.widget.AppCompatEditText {
    private boolean isRun = false;
    private String d = "";

    public DivisionEditText(Context context) {
        super(context);
        setBankCardTypeOn();
    }

    public DivisionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBankCardTypeOn();
    }

    public DivisionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBankCardTypeOn();
    }


    private void setBankCardTypeOn() {
        DivisionEditText.this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isRun) {
                    isRun = false;
                    return;
                }
                isRun = true;
                d = "";
                String newStr = s.toString();
                newStr = newStr.replace(" ", "");
                int index = 0;
                while ((index + 4) < newStr.length()) {
                    d += (newStr.substring(index, index + 4) + " ");
                    index += 4;
                }
                d += (newStr.substring(index, newStr.length()));
                int i = getSelectionStart();
                setText(d);
                try {


                    if (i % 5 == 0 && before == 0) {
                        if (i + 1 <= d.length()) {
                            setSelection(i + 1);
                        } else {
                            setSelection(d.length());
                        }
                    } else if (before == 1 && i < d.length()) {
                        setSelection(i);
                    } else if (before == 0 && i < d.length()) {
                        setSelection(i);
                    } else
                        setSelection(d.length());


                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //对外提供暴漏的方法
    private void insertText(EditText editText, String mText) {
        editText.getText().insert(getSelectionStart(), mText);

    }
}
