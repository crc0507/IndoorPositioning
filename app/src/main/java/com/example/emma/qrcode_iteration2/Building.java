package com.example.emma.qrcode_iteration2;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.emma.qrcode_iteration2.R;
import android.app.ProgressDialog;
import static android.content.ContentValues.TAG;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;


public class Building extends Activity implements
        OnGeocodeSearchListener, OnClickListener{
    private AMap aMap;
    private MapView mapView;
    private int walkMode = RouteSearch.WALK_DEFAULT;// 步行默认模式
    private RouteSearch routeSearch;
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private Button walkBtn;
    private ProgressDialog progDialog;
    private GeocodeSearch geocoderSearch;
    private Marker geoMarker;
    double longitude;
    double latitude;
    private String searchPlace;
    private EditText inputPlace;
    private String addressName;
    LatLng latLng;
    LatLonPoint latLonPoint;
    private Button typebutton;
    private double jingdu1, weidu1;
//    /////////////jia
//    private double jingdu;
//    private double weidu;
//    ////////////jiawan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        Intent intent = getIntent();
        Bundle temp = intent.getExtras();
        latitude = temp.getDouble("latitude");
        longitude = temp.getDouble("longitude");
//        ///////////////jia
//        jingdu = temp.getDouble("x");
//        weidu = temp.getDouble("y");
//        ///////////////jiawan
        jingdu1 = latitude;
        weidu1 = longitude;
        latLng = new LatLng(latitude,longitude);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置地图可视缩放大小

    }
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            //aMap.showIndoorMap(true);

        }

        Button geoButtonPlace = (Button)findViewById(R.id.geoButtonplace);
        geoButtonPlace.setOnClickListener(this);
        typebutton = (Button)findViewById(R.id.typebutton);
        typebutton.setOnClickListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //finish();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);  }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /**
             * 响应地理编码按钮
             */
            case R.id.geoButtonplace:
                inputPlace = (EditText)findViewById(R.id.inputplace);
                searchPlace = inputPlace.getText().toString();
                inputPlace.setText("");
                getLatlon(searchPlace);
                latLonPoint = new LatLonPoint(latitude,longitude);
//                /////////////////jia
//                Bundle bundle = new Bundle();
//                Intent intent1 = new Intent(Building.this, PoiAroundSearchActivity.class);
//                bundle.putDouble("jingdu", latitude);
//                bundle.putDouble("weidu", longitude);
//                intent1.putExtras(bundle);
//                startActivity(intent1);
//                ////////////////jiawan
                break;
            default:
                break;

            case R.id.typebutton:
//                Intent intent = new Intent();
//                intent.setClass(Building.this, typeSearch.class);

                //                startActivity(intent);
                //传给分类
                Intent intent2 = new Intent(Building.this, typeSearch.class);
                Bundle bundle2 = new Bundle();
                bundle2.putDouble("jing", jingdu1);
                bundle2.putDouble("wei", weidu1);
                intent2.putExtras(bundle2);
                startActivity(intent2);

        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                //用户输入转化成经纬度
                longitude = address.getLatLonPoint().getLongitude();
                latitude = address.getLatLonPoint().getLatitude();
//                geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                        .icon(BitmapDescriptorFactory
//                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 18));
                //geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));
                //addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:" + address.getFormatAddress();
                addressName = address.getFormatAddress();
                Bundle bundle = new Bundle();
                Intent intent = new Intent(Building.this, PoiAroundSearchActivity.class);
                bundle.putString("destination", searchPlace);
                bundle.putDouble("jingdu", jingdu1);
                bundle.putDouble("weidu", weidu1);
                intent.putExtras(bundle);
                startActivity(intent);



            } else {
                ToastUtil.show(Building.this, "error");
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }


    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        showDialog();
        GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

    }

}


