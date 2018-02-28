package com.yatang.xc.xcr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.db.CityDao;
import com.yatang.xc.xcr.views.WheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/8/3.
 */

public class SelectDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private TextView textTitle;
    private WheelView wheelProvince,wheelCity;
    private Button btnCancel, btnConfirm;
    private List<ConcurrentHashMap<String, String>> listProvince;
    private List<ConcurrentHashMap<String, String>> listCity;
    private OnSelectListener onSelectListener;
    private List<String> dataProvience,dataCity;
    private String cityId,provinceId;
    private CityDao cityDao;
    public SelectDialog(@NonNull Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select);
        textTitle = (TextView) findViewById(R.id.textTitle);
        wheelProvince = (WheelView) findViewById(R.id.wheelProvince);
        wheelCity = (WheelView) findViewById(R.id.wheelCity);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        dataProvience = new ArrayList<>();
        dataCity  = new ArrayList<>();
        cityDao = new CityDao(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                onSelectListener.onCancel();
                dismiss();
                break;
            case R.id.btnConfirm:
                if(onSelectListener != null ) {
                    onSelectListener.onConfirm(listProvince.get(wheelProvince.getCurrentItem()),listCity.get(wheelCity.getCurrentItem()));
                }
                dismiss();
                break;
        }
    }

    public void show(String title, List<ConcurrentHashMap<String, String>> provinces, List<ConcurrentHashMap<String, String>> citys) {
        super.show();
        textTitle.setText(title);
        this.listProvince = provinces;
        this.listCity = citys;
        provinceId = listProvince.get(0).get("ProvinceId");
        cityId = listCity.get(0).get("CityId");
        if(dataProvience.isEmpty()) {
            for (ConcurrentHashMap<String, String> currentMap : listProvince) {
                dataProvience.add(currentMap.get("Province"));
            }
        }
        dataCity.clear();
        for(ConcurrentHashMap<String, String> currentMap : listCity) {
            dataCity.add(currentMap.get("City"));
        }
        wheelProvince.setWheelItemList(dataProvience);
        wheelProvince.setCurrentItem(0);
        wheelCity.setWheelItemList(dataCity);
        wheelCity.setCurrentItem(0);
        wheelProvince.setOnSelectListener(new WheelView.onSelectListener() {
            @Override
            public void onSelect(int index, String text) {
                provinceId = listProvince.get(index).get("ProvinceId");
                listCity = cityDao.getAllDataByProvinceId(provinceId);
                dataCity.clear();
                for(ConcurrentHashMap<String, String> currentMap : listCity) {
                    dataCity.add(currentMap.get("City"));
                }
                wheelCity.setWheelItemList(dataCity);
                cityId = listCity.get(0).get("CityId");
            }
        });
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 选择时间对话框回调接口，调用者实现
     */
    public interface OnSelectListener {
        void onConfirm(ConcurrentHashMap<String, String> map_province,ConcurrentHashMap<String, String> map_city);

        void onCancel();
    }
}
