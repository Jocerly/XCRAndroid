package com.yatang.xc.xcr.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BaseActivity;
import com.yatang.xc.xcr.activity.ScanActivity;
import com.yatang.xc.xcr.adapter.SearchAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.SearchDao;

import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索弹框
 * Created by dengjiang on 2017/5/16.
 */
@ContentView(R.layout.dialog_search)
public class SearchDialog extends Dialog implements View.OnClickListener {
    public static final int MAX_NUM = 10;
    private BaseActivity context;
    private TextView btnLeft;
    private EditText editSearch;
    private TextView btnRight;
    private ImageView imageClear;
    private ImageView imageGoodsCode;

    private RecyclerView listHistory;
    private SearchAdapter adapter;
    private ArrayList<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private SearchDao searchDao;
    /**
     * 搜索类型：1：商品，2：小票、3：小区/写字楼/学校、4：外送商品
     */
    private int type;
    private OnSearchDialogClickLinster onSearchDialogClickLinster;
    private OnSearchScanDialogClickLinster onSearchScanDialogClickLinster;

    public SearchDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = (BaseActivity)context;
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
            if (type == 2 && msg.length() != 7) {
                ViewInject.toast(context, "小票号长度必须为7位");
                return;
            }
            dismiss();
            searchDao.doAdd(type + "", msg);
            onSearchDialogClickLinster.OK(msg);
        }

        @Override
        public void cleanHistory() {
            listData.clear();
            adapter.notifyDataSetChanged();
            searchDao.delete("" + type);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageClear:
                editSearch.setText("");
                break;
            case R.id.imageGoodsCode:
                MobclickAgent.onEvent(context, "Goods_SearchScan");
                if (onSearchScanDialogClickLinster != null) {
                    onSearchScanDialogClickLinster.Scan();
                } else {
                    if (SystemTool.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, Constants.Permission.CAMERA_SCAN);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "2");
                        context.skipActivityForResult(context, ScanActivity.class, bundle, Constants.ForResult.SCAN);
                    }
                }

                break;

            case R.id.btnLeft:
                dismiss();
                break;
            case R.id.btnRight:
                String msg = editSearch.getText().toString().trim();
                if (StringUtils.isEmpty(msg)) {
                    switch (type) {
                        case 1:
                        case 4:
                            ViewInject.toast(context, "请输入商品名称或条码");
                            return;
                        case 2:
                            ViewInject.toast(context, "请输入小票号");
                            return;
                    }
                }

                if (type == 2 && msg.length() != 7) {
                    ViewInject.toast(context, "小票号长度必须为7位");
                    return;
                }

                searchDao.doAdd(type + "", msg);
                if (onSearchDialogClickLinster != null) {
                    onSearchDialogClickLinster.OK(msg);
                }
                dismiss();
                break;
        }
    }

    /**
     * 更新搜索历史记录 并且 隐藏对话框
     *
     * @param msg 搜索关键字
     */
    public void updateDataBase(String msg) {
        searchDao.doAdd(type + "", msg);
        dismiss();
    }

    /**
     * 显示
     *
     * @param type 搜索类型：1：商品，2：小票、3：小区/写字楼/学校、4：外送商品
     */
    public void show(int type) {
        super.show();
        this.type = type;
        imageGoodsCode.setVisibility(View.GONE);
        switch (type) {
            case 1:
                editSearch = (EditText) findViewById(R.id.editSearch);
                editSearch.setHint("输入商品名称或条码");
                imageGoodsCode.setVisibility(View.VISIBLE);
                break;
            case 2:
                editSearch = (EditText) findViewById(R.id.editSearchNO);
                editSearch.setHint("输入小票号");
                break;
            case 3:
                editSearch = (EditText) findViewById(R.id.editSearch);
                editSearch.setHint("输入小区/写字楼/学校等");
                break;
            case 4:
                editSearch = (EditText) findViewById(R.id.editSearch);
                editSearch.setHint("输入商品名称或条码");

                imageGoodsCode.setVisibility(View.VISIBLE);
                break;
        }
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
                } else {
                    imageClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showListData(type);
    }

    private void showListData(int type) {
        listData.clear();
        listData.addAll(searchDao.getAllData(type + ""));
        int size = listData.size();
        if (size > MAX_NUM) {
            searchDao.delete(type + "", listData.get(size - 1).get("SearchMsg"));
            listData.remove(size - 1);
        }
        adapter.notifyDataSetChanged();
    }

    public interface OnSearchScanDialogClickLinster {
        public void Scan();
    }

    public void setOnSearchScanDialogClickLinster(OnSearchScanDialogClickLinster onSearchScanDialogClickLinster) {
        this.onSearchScanDialogClickLinster = onSearchScanDialogClickLinster;
    }

    public interface OnSearchDialogClickLinster {
        public void OK(String msg);
    }

    public void setOnSearchDialogClickLinster(OnSearchDialogClickLinster onSearchDialogClickLinster) {
        this.onSearchDialogClickLinster = onSearchDialogClickLinster;
    }
}
