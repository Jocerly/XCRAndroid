package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.SearchAdapter;
import com.yatang.xc.xcr.db.SearchDao;

import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外送商品搜索
 * Created by lusha on 2017/07/19.
 */
public class OutGoodsSearchDialog extends Dialog implements View.OnClickListener {
    public static final int MAX_NUM = 10;
    private Context context;
    private TextView btnLeft;
    private EditText editSearch;
    private TextView btnRight;
    private ImageView imageClear;
    private ImageView imageGoodsCode;

    private RecyclerView listHistory;
    private SearchAdapter adapter;
    private ArrayList<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private SearchDao searchDao;
    private OutGoodsSearchDialogClickinster outGoodsSearchDialogClickinster;

    public OutGoodsSearchDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        listHistory = (RecyclerView) findViewById(R.id.listHistory);
        btnLeft = (TextView) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);
        btnRight = (TextView) findViewById(R.id.btnRight);
        btnRight.setOnClickListener(this);
        imageClear = (ImageView) findViewById(R.id.imageClear);
        imageClear.setOnClickListener(this);
        imageGoodsCode = (ImageView) findViewById(R.id.imageGoodsCode);
        imageGoodsCode.setOnClickListener(this);
        imageGoodsCode.setVisibility(View.VISIBLE);
        initData();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                editSearch.setFocusable(true);
                editSearch.setFocusableInTouchMode(true);
                editSearch.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) editSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editSearch, 0);
            }
        });
    }


    private void initData() {
        searchDao = new SearchDao(context);
        adapter = new SearchAdapter(context, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listHistory.setLayoutManager(layoutManager);
        listHistory.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST,
                (int) context.getResources().getDimension(R.dimen.pad1_px), context.getResources().getColor(R.color.line)));
        listHistory.setAdapter(adapter);
    }

    SearchAdapter.OnItemClickListener onItemClickListener = new SearchAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String msg) {
            dismiss();
            searchDao.doAdd("", msg);
            outGoodsSearchDialogClickinster.OK(msg);
        }

        @Override
        public void cleanHistory() {
            listData.clear();
            adapter.notifyDataSetChanged();
            searchDao.delete("");
        }

    };

    public void show() {
        super.show();
        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.setHint("输入商品名称或条码");
        editSearch.setVisibility(View.VISIBLE);
        editSearch.setText("");
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 > 0) {
                    imageClear.setVisibility(View.VISIBLE);
                    imageGoodsCode.setVisibility(View.GONE);
                } else {
                    imageClear.setVisibility(View.GONE);
                    imageGoodsCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showListData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageClear:
                editSearch.setText("");
                break;
            case R.id.btnLeft:
                dismiss();
                break;
            case R.id.btnRight:
                String msg = editSearch.getText().toString().trim();
                if (StringUtils.isEmpty(msg)) {
                    ViewInject.toast(context, "请输入商品名称或条码");
                    return;
                }
                searchDao.doAdd("", msg);
                if (outGoodsSearchDialogClickinster != null) {
                    outGoodsSearchDialogClickinster.OK(msg);
                }
                dismiss();
                break;
            case R.id.imageGoodsCode:
                outGoodsSearchDialogClickinster.onScanClick();
                break;
        }
    }

    private void showListData() {
        listData.clear();
        listData.addAll(searchDao.getAllData(""));
        int size = listData.size();
        if (size > MAX_NUM) {
            searchDao.delete("", listData.get(size - 1).get("SearchMsg"));
            listData.remove(size - 1);
        }
        adapter.notifyDataSetChanged();
    }

    public interface OutGoodsSearchDialogClickinster {
        public void OK(String msg);

        /**
         * 扫码查询商品
         */
        public void onScanClick();
    }

    public void setOutGoodsSearchDialogClickinster(OutGoodsSearchDialogClickinster outGoodsSearchDialogClickinster) {
        this.outGoodsSearchDialogClickinster = outGoodsSearchDialogClickinster;
    }
}
