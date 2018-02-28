package com.yatang.xc.xcr.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.LocationAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.SearchDialog;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 设置O2O店铺地址
 * Created by lusha on 2017/06/12.
 */
@ContentView(R.layout.activity_location)
public class LocationActivity extends BaseActivity implements
        OnGetPoiSearchResultListener, OnGetGeoCoderResultListener {

    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.llSearch, click = true)
    private LinearLayout llSearch;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.imgLocation, click = true)
    private ImageView imgLocation;
    @BindView(id = R.id.imageClear, click = true)
    private ImageView imgClear;

    @BindView(id = R.id.mapView)
    private MapView mapView;
    private BaiduMap baiduMap = null;
    private PoiSearch mPoiSearch = null;
    private LocationClient locationClient = null;
    private String latitude;
    private String longitude;
    private String address;
    private String location;
    private GeoCoder geoCoder = null;
    private LocationAdapter locationAdapter;
    private LatLng center = null;
    List<PoiInfo> list = null;
    PoiInfo poiInfo = null;
    PoiInfo poiInfoFirst = null;

    private SearchDialog searchDialog;
    private boolean isFirstLoc = true;

    @Override
    public void initWidget() {
        textTitle.setText("位置");
        btnRight.setText("确认");
        detachLayout();
    }

    @Override
    public void initData() {
        baiduMap = mapView.getMap();
        //初始化地理编码模块，设置地理编码事件监听
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        baiduMap.setMapStatus(MapStatusUpdateFactory
                .newMapStatus(new MapStatus.Builder().zoom(16).build()));//设置缩放级别
        //初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            address = bundle.getString("address");
            location = bundle.getString("location");
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
        }
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);
        list = new ArrayList<>();
        locationAdapter = new LocationAdapter(aty, list);
        locationAdapter.setOnItemClickLinster(onItemClickLinster);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        baiduMap.setMyLocationEnabled(true);//开启定位图层
        locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
        locationClient.registerLocationListener(myListener); // 注册监听函数
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST, (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(locationAdapter);
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                JCLoger.debug("onMapStatusChangeFinish........");
                isFirstLoc = false;
                geoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(mapStatus.target));
            }
        });
        if ("点击开始定位".equals(location)) {
            isFirstLoc = true;
            if (StringUtils.isEmpty(address)) {
                //地址为空 定位
                startLocation();
            } else {
                //地理编码
                poiInfoFirst = new PoiInfo();
                poiInfoFirst.address = address;
                geoCoder.geocode((new GeoCodeOption()).city("").address(address));
            }
        } else {
            isFirstLoc = false;
            center = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            //反地理编码
            geoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(center));
        }
    }

    LocationAdapter.OnItemClickLinster onItemClickLinster = new LocationAdapter.OnItemClickLinster() {
        @Override
        public void itemClickLinster(String address, LatLng location) {
            poiInfo = new PoiInfo();
            poiInfo.address = address;
            poiInfo.location = location;
            moveMapToPoiInfo(location);
        }
    };

    /**
     * 搜索回调
     */
    SearchDialog.OnSearchDialogClickLinster onSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            center = baiduMap.getMapStatus().target;
            mPoiSearch.searchNearby((new PoiNearbySearchOption().keyword(msg).location(center).radius(3000)));
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                if (poiInfo == null) {
                    toast("请选择地址");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("address", poiInfo.address);
                intent.putExtra("latitude", String.valueOf(poiInfo.location.latitude));
                intent.putExtra("longitude", String.valueOf(poiInfo.location.longitude));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.llSearch:
                searchDialog.show(3);
                break;
            case R.id.imgLocation:
                this.setLocationOption();    //设置定位参数
                startLocation(); // 开始定位
                break;
        }
    }

    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);    //设置定位数据
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);    //设置地图中心点以及缩放级别
            baiduMap.animateMapStatus(u);
            toast("定位成功");
            locationClient.stop();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(0); // 设置发起定位请求的间隔时间
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        locationClient.setLocOption(option);
    }

    // 三个状态实现地图生命周期管理
    @Override
    protected void onDestroy() {
        locationClient.stop();
        mPoiSearch.destroy();
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("位置");
        MobclickAgent.onResume(aty);
        mapView.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("位置");
        MobclickAgent.onPause(aty);
        mapView.onPause();
    }

    /**
     * poi搜索返回的结果
     *
     * @param poiResult
     */
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            toast("抱歉，未找到结果");
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            list.clear();
            list.addAll(poiResult.getAllPoi());
            locationAdapter.setSelection(0);
            locationAdapter.notifyDataSetChanged();
            if (list.size() > 0) {
                moveMapToPoiInfo(list.get(0).location);
            }
            poiInfo = new PoiInfo();
            poiInfo.address = list.get(0).address;
            poiInfo.location = list.get(0).location;
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 获取地理编码的结果（把地址转换为经纬度）
     *
     * @param geoCodeResult
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            toast("没有检索到结果");
            return;
        }
        moveMapToPoiInfo(geoCodeResult.getLocation());
        geoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(geoCodeResult.getLocation()));
    }

    /**
     * 获取反向地理编码结果(把经纬度转为地址)
     *
     * @param reverseGeoCodeResult
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            toast("没有检索到结果");
            return;
        }
        poiInfo = new PoiInfo();
        poiInfo.location = reverseGeoCodeResult.getLocation();
        poiInfo.address = reverseGeoCodeResult.getAddress();
        moveMapToPoiInfo(poiInfo.location);
        list.clear();
        if (isFirstLoc) {
            list.add(0, poiInfoFirst);
        }
        list.addAll(reverseGeoCodeResult.getPoiList());
        locationAdapter.setSelection(0);
        locationAdapter.setFirstLoc(isFirstLoc);
        locationAdapter.notifyDataSetChanged();
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(LocationActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LocationActivity.this, new
                        String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {
                locationClient.start();
                toast("定位中");
            }
        } else {
            locationClient.start();
            toast("定位中");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case Constants.Permission.ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationClient.start();
                    toast("定位中");
                } else {
                    toast("获取定位权限失败");
                }
                break;

        }
    }

    private void moveMapToPoiInfo(LatLng latLng) {
        if (baiduMap != null) {
            baiduMap.clear();
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        }
    }
}
