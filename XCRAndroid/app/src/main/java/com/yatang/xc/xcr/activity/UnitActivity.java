package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.UnitAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import cz.msebera.android.httpclient.util.EncodingUtils;

/** 常用单位
 * Created by lusha on 2017/09/06.
 */
@ContentView(R.layout.activity_unit)
public class UnitActivity extends BaseActivity {

    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;


    private UnitAdapter adapter;
    List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    @Override
    public void initWidget() {
        textTitle.setText("常用单位");
        btnRight.setVisibility(View.GONE);

    }

    @Override
    public void initData() {
        adapter = new UnitAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST, (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(adapter);
        getUnitName();
    }

    UnitAdapter.OnItemClickListener onItemClickListener = new UnitAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String unitName) {
            Intent intent = new Intent();
            intent.putExtra("unitName", unitName);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    /**
     * 获取单位数据
     */
    public void getUnitName(){
        InputStream is = null;
        try {
            String str = "";
            is = getClass().getClassLoader().getResourceAsStream("assets/Unit.txt");
            int size = is.available();
            byte [] buffer = new byte[size];
            is.read(buffer);
            str = EncodingUtils.getString(buffer, "UTF-8");
            String [] unitName = str.split(",");
            for (int i = 0; i < unitName.length; i++){
                ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
                map.put("UnitName", unitName[i]);
                listData.add(map);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }

}
