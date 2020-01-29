package com.example.emma.qrcode_iteration2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;

import static android.content.ContentValues.TAG;


public class GeocoderActivity extends Activity implements
		OnGeocodeSearchListener, OnClickListener {
	//private static int flag = 1;
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private AMap aMap = null;
	private MapView mapView;
	private Marker geoMarker;
	//要查找的建筑物
	private String searchBuilding;
	private EditText inputBuilding;
	double latitude;
	double longitude;
	private Button giftbutton;

	//Button btn = (Button)findViewById(R.id.geoButton);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geocoder_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		giftbutton = (Button)findViewById(R.id.giftbutton);
		giftbutton.setOnClickListener(this);

		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			//aMap.showIndoorMap(true);
			geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		}
		Button geoButton = (Button) findViewById(R.id.geoButtonbuilding);
		geoButton.setOnClickListener(this);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
//		else if(flag%2!=0){
//			flag++;
//			//获取用户输入的字符串
//			getLatlon(searchBuilding);
//			Bundle bundle = new Bundle();
//			Intent intent = new Intent(GeocoderActivity.this, Building.class);
//			Log.d(TAG, "rinimama" + longitude + latitude);
//			bundle.putDouble("longitude", longitude);
//			bundle.putDouble("latitude", latitude);
//			intent.putExtras(bundle);
//			startActivity(intent);
//		}else{
//			flag++;
//		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		Log.d(TAG, "onDestroy: ");
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

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		showDialog();
		GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
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
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
				geoMarker.setPosition(AMapUtil.convertToLatLng(address
						.getLatLonPoint()));
				addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:" + address.getFormatAddress();
				//ToastUtil.show(GeocoderActivity.this, addressName);
				LatLng latLng = new LatLng(latitude,longitude);
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点
				aMap.moveCamera(CameraUpdateFactory.zoomTo(17)); // 设置地图可视缩放大小
				Bundle bundle = new Bundle();
				Intent intent = new Intent(GeocoderActivity.this,Building.class);
				bundle.putDouble("longitude", longitude);
				bundle.putDouble("latitude", latitude);
				intent.putExtras(bundle);
				startActivity(intent);

			} else {
				ToastUtil.show(GeocoderActivity.this, "error");
			}
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}
	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			/**
			 * 响应地理编码按钮
			 */
			case R.id.geoButtonbuilding:
				inputBuilding = (EditText)findViewById(R.id.inputbuilding);
				searchBuilding = inputBuilding.getText().toString();
				inputBuilding.setText("");
				getLatlon(searchBuilding);
				break;
            case R.id.giftbutton:
                Intent intentgift = new Intent(GeocoderActivity.this, Feedback.class);
                startActivity(intentgift);
                break;
			default:
				break;
		}
	}
}