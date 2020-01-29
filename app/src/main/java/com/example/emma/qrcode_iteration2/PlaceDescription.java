package com.example.emma.qrcode_iteration2;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import android.app.ProgressDialog;
import static android.content.ContentValues.TAG;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import java.io.Serializable;
import java.util.List;

import com.sy.qrcodelibrary.interfaces.OnScanSucceedListener;
import com.sy.qrcodelibrary.utils.QRCodeUtil;


public class PlaceDescription extends Activity implements
        OnGeocodeSearchListener, OnClickListener{
    //    private static int flag = 1;
//    private static int sendnum = 1;
    private AMap aMap;
    private MapView mapView;
    private int walkMode = RouteSearch.WALK_DEFAULT;// 步行默认模式
    private RouteSearch routeSearch;
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private Button walkBtn;
    private ProgressDialog progDialog;
    private GeocodeSearch geocoderSearch;
    private Marker geoMarker;
    private String searchPlace;
    private EditText inputPlace;
    private String addressName;
    LatLng latLng;
    LatLonPoint latLonPoint;
    PoiData poiData;
    private TextView showaddress;
    private TextView shop;
    private String shopname;
    private TextView topname;
    private TextView secondname;
    private Button backbutton;
    private Button codeButton;
    private String scanResult;
    private String result;

    static double longitude;
    static double latitude;
    static String text;
    static  boolean isSearchCliked;


    //Button btn = (Button)findViewById(R.id.geoButton);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_description);
        //mapView = (MapView) findViewById(R.id.map);
        //mapView.onCreate(savedInstanceState);// 此方法必须重写
        //init();

        Intent intent = getIntent();
        List<PoiData> list = (List<PoiData>)getIntent().getSerializableExtra("poiitems");

        //获取list的第一个数据
        poiData = list.get(0);
        latitude = poiData.getLatitude();
        longitude = poiData.getLongitude();
        text=poiData.getText();

//        Intent intent2 = new Intent(PlaceDescription.this, ShopList.class);
//        intent2.putExtra("poiitems2", (Serializable)list);

        //ToastUtil.show(PlaceDescription.this, ""+latitude+longitude);
        latLonPoint = new LatLonPoint(latitude, longitude);
        //addresstoshow = addressName;
        addressName = poiData.getText();
        showaddress = (TextView)findViewById(R.id.addresstoshow);
        showaddress.setText(addressName);

        //shop = (TextView)findViewById(R.id.shopname);
        shopname = poiData.getTitle();
        //shop.setText(shopname);

        //topname = (TextView) findViewById(R.id.topname);
        secondname = (TextView)findViewById(R.id.secondname);
        //topname.setText(shopname);
        secondname.setText(shopname);

        backbutton = (Button)findViewById(R.id.back);
        backbutton.setOnClickListener(this);



        isSearchCliked=false;
        codeButton=(Button)findViewById(R.id.codePage);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCliked=true;
                QRCodeUtil.scanCode(PlaceDescription.this, new OnScanSucceedListener() {
                    @Override
                    public void onScanSucceed(String resultString) {
                       Intent intent=new Intent(PlaceDescription.this,RouteActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("result",resultString);
                        intent.putExtras(bundle);
                       startActivity(intent);
//                        result=resultString;
                    }
                });
//                Intent intent=new Intent(PlaceDescription.this,MainActivity.class);
//                startActivity(intent);

//                Intent intent=new Intent(PlaceDescription.this,RouteActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("result",result);
//                intent.putExtras(bundle);
 //               startActivity(intent);

          }
      });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            scanResult = bundle.getString("result");
           Intent intent=new Intent(PlaceDescription.this,RouteActivity.class);

            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
        //finish();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
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
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.codePage:
                isSearchCliked=true;
                break;
            default:
                break;
        }
    }


    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                latitude = poiData.getLatitude();
//                longitude = poiData.getLongitude();
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(latLonPoint), 19));
                geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                ToastUtil.show(PlaceDescription.this, addressName);

            } else {
                ToastUtil.show(PlaceDescription.this,"error");
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {

    }

    public String getScanResult() {
        return scanResult;
    }

    public void setScanResult(String scanResult) {
        this.scanResult = scanResult;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        PlaceDescription.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        PlaceDescription.latitude = latitude;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        PlaceDescription.text = text;
    }

    public static boolean isSearchCliked() {
        return isSearchCliked;
    }

    public static void setIsSearchCliked(boolean isSearchCliked) {
        PlaceDescription.isSearchCliked = isSearchCliked;
    }
}

