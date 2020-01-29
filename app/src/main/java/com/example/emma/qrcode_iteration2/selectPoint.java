package com.example.emma.qrcode_iteration2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

public class selectPoint extends AppCompatActivity implements AMap.OnMapClickListener{
    private Button doneSelect;
    private MapView mMapView = null;
    private AMap aMap = null;
    double latitude;
    double longtitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_point);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();

        //点击按钮发送经纬度给feedback界面
        doneSelect = (Button)findViewById(R.id.doneSelect);
        doneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(selectPoint.this,Feedback.class);
                String la = String.valueOf(latitude);
                String lo = String.valueOf(longtitude);
                intent.putExtra("latitude",la);
                intent.putExtra("longitude",lo);
                startActivity(intent);
            }
        });
    }
    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        aMap.showIndoorMap(true);//显示室内地图
        LatLng latLng = new LatLng(39.92448, 116.518295);//默认中心为朝阳大悦城
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置地图可视缩放大小
        aMap.setOnMapClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        aMap.clear();
        latitude=latLng.latitude;
        longtitude=latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder));
        markerOptions.position(latLng);
        aMap.addMarker(markerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }
}
