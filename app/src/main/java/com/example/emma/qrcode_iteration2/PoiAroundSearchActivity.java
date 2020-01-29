package com.example.emma.qrcode_iteration2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 介绍poi周边搜索功能
 */
public class PoiAroundSearchActivity extends Activity implements OnClickListener,
OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener,
OnPoiSearchListener {
	private MapView mapview;
	private AMap mAMap;

	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	//private LatLonPoint lp = new LatLonPoint(39.924548, 116.519035);
	private LatLonPoint lp;
	private Marker locationMarker; // 选择的点
	private Marker detailMarker;
	private Marker mlastMarker;
	private PoiSearch poiSearch;
	private myPoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据

	private RelativeLayout mPoiDetail;
	private TextView mPoiName, mPoiAddress;
	private String keyWord = "";
	private EditText mSearchText;
    private String destination;
	private Double longitudey;
	private Double latitudex;
	private Button checkDetail;
	private Intent intent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place);
		mapview = (MapView)findViewById(R.id.placemap);
		mapview.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //用户输入的目的地名称
        destination = bundle.getString("destination");
//		/////////////jia
		latitudex = bundle.getDouble("jingdu");
		longitudey = bundle.getDouble("weidu");
		lp = new LatLonPoint(latitudex, longitudey);
		//ToastUtil.show(PoiAroundSearchActivity.this, lp+"");
//
//		/////////////jiawan
        doSearchQuery();
		init();
	}


	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mapview.getMap();
			//mAMap.showIndoorMap(true);
			mAMap.setOnMapClickListener(this);
			mAMap.setOnMarkerClickListener(this);
			mAMap.setOnInfoWindowClickListener(this);
			mAMap.setInfoWindowAdapter(this);
			checkDetail = (Button)findViewById(R.id.checkdetail);
			checkDetail.setOnClickListener(this);

		}
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 19));
	}
	/**
	 * 开始进行poi搜索
	 */
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
//		keyWord = mSearchText.getText().toString().trim();
		currentPage = 0;
		query = new PoiSearch.Query(destination, "", "010");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(200);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

//		//////////////////////jia
//		lp = new LatLonPoint(latitudex, longitudey);
//		ToastUtil.show(PoiAroundSearchActivity.this, lp+"crc");
//		//////////////////////jiawan
		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 1000, true));//
			// 设置搜索区域为以lp点为圆心，其周围5000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
//		whetherToShowDetailInfo(false);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private List<PoiData> getData(List<PoiItem> data){
		List<PoiData> list = new ArrayList<PoiData>();
		for(PoiItem item : data){
			PoiData temp = new PoiData(item.getLatLonPoint().getLongitude(),item.getLatLonPoint().getLatitude(),
					item.getTitle(),item.getSnippet());
			list.add(temp);
		}
		return list;
	}

	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		if (rcode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						//清除POI信息显示
						//whetherToShowDetailInfo(false);
						//并还原点击marker样式
						if (mlastMarker != null) {
							//resetlastmarker();
						}
						//清理之前搜索结果的marker
						if (poiOverlay !=null) {
							poiOverlay.removeFromMap();
						}
						mAMap.clear();
						poiOverlay = new myPoiOverlay(mAMap, poiItems);
						intent = new Intent(PoiAroundSearchActivity.this, PlaceDescription.class);
						List<PoiData> list = getData(poiItems);
						intent.putExtra("poiitems", (Serializable)getData(poiItems));
						//startActivity(intent);

						//用来测试的toast显示
						double a = poiItems.get(0).getLatLonPoint().getLatitude();
						double b = poiItems.get(0).getLatLonPoint().getLongitude();
//						String aa = a+"";
//						String bb = b+"";
						//ToastUtil.show(PoiAroundSearchActivity.this, aa+bb);

						locationMarker = mAMap.addMarker(new MarkerOptions()
								.anchor(0.5f, 0.5f)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
								.position(new LatLng(a, b)));
						locationMarker.showInfoWindow();


						poiOverlay.addToMap();

					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						//showSuggestCity(suggestionCities);
						ToastUtil.show(PoiAroundSearchActivity.this,"this city no");
					} else {
						ToastUtil.show(PoiAroundSearchActivity.this,
								"error");
					}
				}
			} else {
				ToastUtil.show(PoiAroundSearchActivity.this, "error");
			}
		} else  {
			ToastUtil.showerror(this.getApplicationContext(), rcode);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkdetail:
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onMarkerClick(Marker marker) {


		return true;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onMapClick(LatLng arg0) {
	}


	/**
	 * 自定义PoiOverlay
	 *
	 */

	private class myPoiOverlay {
		private AMap mamap;
		private List<PoiItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	        for (int i = 0; i < mPois.size(); i++) {
	            //Marker marker = mamap.addMarker(getMarkerOptions(i));
	            PoiItem item = mPois.get(i);
				//marker.setObject(item);
	            //mPoiMarks.add(marker);
	        }
	    }

	    /**
	     * 去掉PoiOverlay上所有的Marker。
	     *
	     * @since V2.1.0
	     */
	    public void removeFromMap() {
	        for (Marker mark : mPoiMarks) {
	            mark.remove();
	        }
	    }

	}

}
