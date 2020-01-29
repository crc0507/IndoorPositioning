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
public class typeSearch extends Activity implements OnClickListener,
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
    private Double longitudey;
    private Double latitudex;
    private Button checktype;
    private Intent intent;
    private Button clothes;
    private Button makeup;
    private Button supermarket;
    private Button electrony;
    private Button pe;
    private Button stationary;
    private Button chinese;
    private Button foreign;
    private Button fastfood;
    private Button coffee;
    private Button dessert;
    private Button tea;
    private Button jewel;
    private Button hair;
    private Button meirong;
    private Button gift;
    private Button shasha;
    private Button photo;
    private Button film;
    private Button ktv;
    private Button game;
    private Button music;
    private Button chess;
    private Button sports;
    private Button china;
    private Button gongshang;
    private Button jianshe;
    private Button zhaoshang;
    private Button jiaotong;
    private Button nongye;

//    private String typecode;
//    Intent intenttype = new Intent(typeSearch.this, TypeMap.class);
//    Bundle bundletype = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_search);
        //mapview = (MapView)findViewById(R.id.typemap);
        //mapview.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
        //周围
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        latitudex = bundle.getDouble("jing");
        longitudey = bundle.getDouble("wei");

//        Intent intentintent = new Intent(typeSearch.this, TypeMap.class);
//        Bundle bundlebundle = new Bundle();
//        bundlebundle.putDouble("jingjing", latitudex);
//        bundlebundle.putDouble("weiwei", longitudey);
//        intentintent.putExtras(bundlebundle);
//        startActivity(intentintent);

        clothes = (Button)findViewById(R.id.clothes);
        clothes.setOnClickListener(this);
        makeup = (Button)findViewById(R.id.makeup);
        makeup.setOnClickListener(this);
        supermarket = (Button)findViewById(R.id.supermarket);
        supermarket.setOnClickListener(this);
        electrony = (Button)findViewById(R.id.electrony);
        electrony.setOnClickListener(this);
        pe = (Button)findViewById(R.id.pe);
        pe.setOnClickListener(this);
        stationary = (Button)findViewById(R.id.stationary);
        stationary.setOnClickListener(this);
        chinese = (Button)findViewById(R.id.chinese);
        chinese.setOnClickListener(this);
        foreign = (Button)findViewById(R.id.foreign);
        foreign.setOnClickListener(this);
        fastfood = (Button)findViewById(R.id.fastfood);
        fastfood.setOnClickListener(this);
        coffee = (Button)findViewById(R.id.coffee);
        coffee.setOnClickListener(this);
        dessert = (Button)findViewById(R.id.dessert);
        dessert.setOnClickListener(this);
        tea = (Button)findViewById(R.id.tea);
        tea.setOnClickListener(this);
        jewel = (Button)findViewById(R.id.jewel);
        jewel.setOnClickListener(this);
        hair = (Button)findViewById(R.id.hair);
        hair.setOnClickListener(this);
        meirong = (Button)findViewById(R.id.meirong);
        meirong.setOnClickListener(this);
        gift = (Button)findViewById(R.id.gift);
        gift.setOnClickListener(this);
        shasha = (Button)findViewById(R.id.shasha);
        shasha.setOnClickListener(this);
        photo = (Button)findViewById(R.id.photo);
        photo.setOnClickListener(this);
        film = (Button)findViewById(R.id.film);
        film.setOnClickListener(this);
        ktv = (Button)findViewById(R.id.ktv);
        ktv.setOnClickListener(this);
        game = (Button)findViewById(R.id.game);
        game.setOnClickListener(this);
        music = (Button)findViewById(R.id.music);
        music.setOnClickListener(this);
        chess = (Button)findViewById(R.id.chess);
        chess.setOnClickListener(this);
        sports = (Button)findViewById(R.id.sports);
        sports.setOnClickListener(this);
        china = (Button)findViewById(R.id.china);
        china.setOnClickListener(this);
        gongshang = (Button)findViewById(R.id.gongshang);
        gongshang.setOnClickListener(this);
        jianshe = (Button)findViewById(R.id.jianshe);
        jianshe.setOnClickListener(this);
        zhaoshang = (Button)findViewById(R.id.zhaoshang);
        zhaoshang.setOnClickListener(this);
        jiaotong = (Button)findViewById(R.id.jiaotong);
        jiaotong.setOnClickListener(this);
        nongye = (Button)findViewById(R.id.nongye);
        nongye.setOnClickListener(this);

//		/////////////jia
//		Intent intent2 = getIntent();
//		Bundle bundle2 = intent2.getExtras();
//		latitudex = bundle2.getDouble("jingdu");
//		longitudey = bundle2.getDouble("weidu");
//
//		/////////////jiawan
        //doSearchQuery();
        //init();
    }


//    /**
//     * 初始化AMap对象
//     */
//    private void init() {
//        if (mAMap == null) {
//            mAMap = mapview.getMap();
//            mAMap.showIndoorMap(true);
//            mAMap.setOnMapClickListener(this);
//            mAMap.setOnMarkerClickListener(this);
//            mAMap.setOnInfoWindowClickListener(this);
//            mAMap.setInfoWindowAdapter(this);
//            checktype = (Button)findViewById(R.id.checktype);
//            checktype.setOnClickListener(this);
//
//        }
//        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 19));
//    }
    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
//		keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query("", "0611", "010");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(200);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

//		//////////////////////jia
//		lp = new LatLonPoint(latitudex, longitudey);
//		ToastUtil.show(PoiAroundSearchActivity.this, lp+"crc");
//		//////////////////////jiawan
        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new SearchBound(lp, 200, true));//
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
        //mapview.onResume();
//		whetherToShowDetailInfo(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        //mapview.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapview.onDestroy();
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

                        intent = new Intent(typeSearch.this, ShopList.class);
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
                        ToastUtil.show(typeSearch.this,"this city no");
                    } else {
                        ToastUtil.show(typeSearch.this,
                                "error");
                    }
                }
            } else {
                ToastUtil.show(typeSearch.this, "error");
            }
        } else  {
            ToastUtil.showerror(this.getApplicationContext(), rcode);
        }
    }

    @Override
    public void onClick(View v) {
        String typecode;
        Intent intenttype = new Intent(typeSearch.this, TypeMap.class);
        Bundle bundletype = new Bundle();
        bundletype.putDouble("jingjing", latitudex);
        bundletype.putDouble("weiwei", longitudey);
//        Intent intentintent = new Intent(typeSearch.this, TypeMap.class);
//        Bundle bundlebundle = new Bundle();
//        bundlebundle.putDouble("jingjing", latitudex);
//        bundlebundle.putDouble("weiwei", longitudey);
//        intentintent.putExtras(bundlebundle);
//        startActivity(intentintent);
        switch (v.getId()) {
            case R.id.clothes:
                typecode = "061100";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.makeup:
                typecode = "061400";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.supermarket:
                typecode = "060400";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.electrony:
                typecode = "060300";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.pe:
                typecode = "060900";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.stationary:
                typecode = "060800";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.chinese:
                typecode = "050100";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.foreign:
                typecode = "050200";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.fastfood:
                typecode = "050300";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.coffee:
                typecode = "050500";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.dessert:
                typecode = "050900";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.tea:
                typecode = "050600";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.jewel:
                typecode = "061202";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.hair:
                typecode = "071100";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.meirong:
                typecode = "090201";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.gift:
                typecode = "061209";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.shasha:
                typecode = "061401";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.photo:
                typecode = "071300";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.film:
                typecode = "080601";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.ktv:
                typecode = "080302";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.game:
                typecode = "080305";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.music:
                typecode = "080602";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.chess:
                typecode = "080306";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.sports:
                typecode = "080100";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.china:
                typecode = "160104";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.gongshang:
                typecode = "160105";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.jianshe:
                typecode = "160106";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.zhaoshang:
                typecode = "160109";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.jiaotong:
                typecode = "160108";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
                break;
            case R.id.nongye:
                typecode = "160107";
                bundletype.putString("typecode", typecode);
                intenttype.putExtras(bundletype);
                startActivity(intenttype);
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
