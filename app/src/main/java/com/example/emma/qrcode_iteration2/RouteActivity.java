package com.example.emma.qrcode_iteration2;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.*;

import com.amap.api.im.data.IMDataManager;
import com.amap.api.im.data.IMRoutePlanning;
import com.amap.api.im.listener.DownloadStatusCode;
import com.amap.api.im.listener.IMDataDownloadListener;
import com.amap.api.im.listener.IMMapLoadListener;
import com.amap.api.im.listener.IMRoutePlanningListener;
import com.amap.api.im.listener.MapLoadStatus;
import com.amap.api.im.listener.RoutePLanningStatus;
import com.amap.api.im.util.IMBuildingInfo;
import com.amap.api.im.util.IMSearchResult;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import overlay.WalkRouteOverlay;
import util.AMapUtil;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import android.app.ProgressDialog;
import overlay.ThroughRouteOverlay;
import java.util.List;
import java.util.ArrayList;
import com.example.emma.qrcode_iteration2.R;
public class RouteActivity extends AppCompatActivity implements OnClickListener, OnRouteSearchListener
{
    private AMap aMap;
    private MapView mapView;
    private int walkMode = RouteSearch.WALK_DEFAULT;// 步行默认模式
    private RouteSearch routeSearch;
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private ImageView elevator,stair,escalator,cancel,ad;
    private ProgressDialog progDialog;
    private IMIndoorMapFragment mIndoorMapFragment;
    private IMDataManager imDataManager;
    private IMRoutePlanning routePlanning;
    private WalkRouteOverlay walkRouteOverlay;
    private double latitude1,longtitude1,latitude2,longtitude2;
    private LatLonPoint startPoint,endPoint;
    private WalkPath walkPath;
    private  List<LatLonPoint> throughList;
    private TextView mContentText;// 展示文本内容
    private RelativeLayout mShowMore;// 展示更多
    private ImageView mImageSpread;// 展开
    private ImageView mImageShrinkUp;// 收起
    private List<SelfDefine>dianti,louti,futi,atm,toilt,vending,asking;
    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 1;// 默认展示最大行数3行
    private static final int SHOW_CONTENT_NOMAL_STATE = 1;// 默认收缩时行数
    private static final int SHRINK_UP_STATE = 1;// 收起状态
    private static final int SPREAD_STATE = 2;// 展开状态
    private static int mState = SHRINK_UP_STATE;//默认收起状态
    private static  String button="normal";
    private String walkdetails="";
    private int times=0;
    private String buildingName="朝阳大悦城";
    private String desText;
    private int codeFloor;
    private String isSameFloor;
    private LatLonPoint throughPoint;
    private String orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mapView = (MapView) findViewById(R.id.map);
        elevator=(ImageView)findViewById(R.id.elevator);
        escalator=(ImageView)findViewById(R.id.escalator);
        stair=(ImageView)findViewById(R.id.stair);
        cancel=(ImageView)findViewById(R.id.cancel);
        ad=(ImageView)findViewById(R.id.ad);
        mContentText = (TextView) findViewById(R.id.text_content);
        mShowMore = (RelativeLayout) findViewById(R.id.show_more);
        mImageSpread = (ImageView) findViewById(R.id.spread);
        mImageShrinkUp = (ImageView) findViewById(R.id.shrink_up);


//        mIndoorMapFragment=(IMIndoorMapFragment)getSupportFragmentManager().findFragmentById(R.id.indoor_map);
//        mIndoorMapFragment.setDataPath(Environment.getExternalStorageDirectory()
//                + "/data_path");
//        mIndoorMapFragment.loadMap("B023B173VP", imMapLoadListener);
//        mIndoorMapFragment.initSwitchFloorToolBar();
//        mIndoorMapFragment.initZoomView();
//        mIndoorMapFragment.initCompass();
//        mIndoorMapFragment.initPlottingScale();

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

        init();
    }
    private void init() {
        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        String result=bundle.getString("result");
        String[]results=result.split(",");
        Double lai=Double.parseDouble(results[0]);
        Double longi=Double.parseDouble(results[1]);
        int floor=Integer.parseInt(results[2]);
        orientation=results[3];
             if(ShopList.isSortClicked==true){
            latitude1=lai;
            longtitude1=longi;
            latitude2=ShopList.getShopJingdu();
            longtitude2=ShopList.getShopWeidu();
            desText=ShopList.getText();
        }
        else if(PlaceDescription.isSearchCliked==true){
            latitude1=lai;
            longtitude1=longi;
            latitude2=PlaceDescription.getLatitude();
            longtitude2=PlaceDescription.getLongitude();
            desText=PlaceDescription.getText();
        }
        else{
            latitude1=39.924528;
            longtitude1=116.518448;
            latitude2=39.924758;
            longtitude2=116.517922;
            desText="朝阳北路101号朝阳大悦城2层401室";
        }

        codeFloor=floor;
        louti=new ArrayList<SelfDefine>();
        futi=new ArrayList<SelfDefine>();
        dianti=new ArrayList<SelfDefine>();
        vending=new ArrayList<SelfDefine>();
        toilt=new ArrayList<SelfDefine>();
        asking=new ArrayList<SelfDefine>();

        //售货机
        vending.add(new SelfDefine(39.924591,116.518392));
        vending.add(new SelfDefine(39.924597,116.518301));

        //厕所
        toilt.add(new SelfDefine(39.924143,116.519229));
        toilt.add(new SelfDefine(39.924167,116.519258));

        //问询台
        asking.add(new SelfDefine(39.924636,116.518496));

        //楼梯
        louti.add(new SelfDefine(39.92445,116.51847));
        louti.add(new SelfDefine(39.924183,116.518803));
        louti.add(new SelfDefine(39.924257,116.517966));

        //扶梯
        futi.add(new SelfDefine(39.924643,116.518221));
        futi.add(new SelfDefine(39.924059,116.519052));
        futi.add(new SelfDefine(39.925057,116.518148));

        //电梯
        dianti.add(new SelfDefine(39.924406,116.51848));
        dianti.add(new SelfDefine(39.924655,116.518759));
        dianti.add(new SelfDefine(39.924322,116.51914));



        startPoint=new LatLonPoint(latitude1, longtitude1);
        endPoint=new LatLonPoint(latitude2, longtitude2);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //aMap.showIndoorMap(true);
        elevator.setOnClickListener(this);
        stair.setOnClickListener(this);
        escalator.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mShowMore.setOnClickListener(this);

        LatLng latLng = new LatLng(39.92448, 116.518295);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置地图可视缩放大小

//        MarkerOptions otMarkerOptions = new MarkerOptions();
//        otMarkerOptions.position(latLng);
//        otMarkerOptions.visible(true);//设置可见
//        otMarkerOptions.title("二维码");
//        otMarkerOptions.draggable(true);
//        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.code));
//        aMap.addMarker(otMarkerOptions);

        for(int i=0;i<dianti.size();i++){
            MarkerOptions otMarkerOptions1 = new MarkerOptions();
            latLng=new LatLng(dianti.get(i).getLaititude(),dianti.get(i).getLongtitude());
            otMarkerOptions1.position(latLng);
            otMarkerOptions1.visible(true);//设置可见
            otMarkerOptions1.draggable(true);
            otMarkerOptions1.title("电梯");
            otMarkerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.dian));
            aMap.addMarker(otMarkerOptions1);

        }
        for(int i=0;i<louti.size();i++){
            MarkerOptions otMarkerOptions2 = new MarkerOptions();
            latLng=new LatLng(louti.get(i).getLaititude(),louti.get(i).getLongtitude());
            otMarkerOptions2.position(latLng);
            otMarkerOptions2.visible(true);//设置可见
            otMarkerOptions2.draggable(true);
            otMarkerOptions2.title("楼梯");
            otMarkerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.lou));
            aMap.addMarker(otMarkerOptions2);
        }
        for(int i=0;i<futi.size();i++){
            MarkerOptions otMarkerOptions3 = new MarkerOptions();
            latLng=new LatLng(futi.get(i).getLaititude(),futi.get(i).getLongtitude());
            otMarkerOptions3.position(latLng);
            otMarkerOptions3.visible(true);//设置可见
            otMarkerOptions3.draggable(true);
            otMarkerOptions3.title("扶梯");
            otMarkerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.fu));
            aMap.addMarker(otMarkerOptions3);
        }
        for(int i=0;i<toilt.size();i++){
            MarkerOptions otMarkerOptions4 = new MarkerOptions();
            latLng=new LatLng(toilt.get(i).getLaititude(),toilt.get(i).getLongtitude());
            otMarkerOptions4.position(latLng);
            otMarkerOptions4.visible(true);//设置可见
            otMarkerOptions4.draggable(true);
            otMarkerOptions4.title("厕所");
            otMarkerOptions4.icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet));
            aMap.addMarker(otMarkerOptions4);
        }
        for(int i=0;i<vending.size();i++){
            MarkerOptions otMarkerOptions5 = new MarkerOptions();
            latLng=new LatLng(vending.get(i).getLaititude(),vending.get(i).getLongtitude());
            otMarkerOptions5.position(latLng);
            otMarkerOptions5.visible(true);//设置可见
            otMarkerOptions5.draggable(true);
            otMarkerOptions5.title("售货机");
            otMarkerOptions5.icon(BitmapDescriptorFactory.fromResource(R.drawable.drink));
            aMap.addMarker(otMarkerOptions5);
        }
        for(int i=0;i<asking.size();i++){
            MarkerOptions otMarkerOptions6 = new MarkerOptions();
            latLng=new LatLng(asking.get(i).getLaititude(),asking.get(i).getLongtitude());
            otMarkerOptions6.position(latLng);
            otMarkerOptions6.visible(true);//设置可见
            otMarkerOptions6.draggable(true);
            otMarkerOptions6.title("售货机");
            otMarkerOptions6.icon(BitmapDescriptorFactory.fromResource(R.drawable.asking));
            aMap.addMarker(otMarkerOptions6);
        }
        isSameFloor=findFloor(desText,codeFloor);
        if(isSameFloor=="same"){
            changeIcon();
        }
        route(startPoint,endPoint);

        //route_indoor();
        //findFloor("悦诗风吟");


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();  }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);  }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    public void changeIcon(){
        stair.setImageResource(R.drawable.information_desk);
        escalator.setImageResource(R.drawable.wtc);
        elevator.setImageResource(R.drawable.vending);
    }
    @Override
    public void onClick(View v) {
        if(v==stair){
            if(isSameFloor!="same") {
                stair.setImageResource(R.drawable.stair1);
                elevator.setImageResource(R.drawable.elevator);
                escalator.setImageResource(R.drawable.escalator);
                button = "楼梯";
            }
            else{
                stair.setImageResource(R.drawable.information_desk1);
                escalator.setImageResource(R.drawable.wtc);
                elevator.setImageResource(R.drawable.vending);
                button = "问询台";
            }

            dissmissProgressDialog();
            aMap.clear();// 清理地图上的所有覆盖物

            LatLng start=new LatLng(startPoint.getLatitude(),startPoint.getLongitude());
            LatLng lat=null;
            if(isSameFloor!="same") {
                lat = chooseThroughPoint(start, "louti");
            }
            else{
                lat = chooseThroughPoint(start, "ask");
            }
            throughPoint=new LatLonPoint(lat.latitude,lat.longitude);

            throughList=new ArrayList<LatLonPoint>();
            throughList.add(throughPoint);

            final RouteSearch.FromAndTo fromAndThrough = new RouteSearch.FromAndTo(startPoint, throughPoint);
            WalkRouteQuery query = new WalkRouteQuery(fromAndThrough, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);

            final RouteSearch.FromAndTo fromAndThrough1 = new RouteSearch.FromAndTo(throughPoint, endPoint);
            WalkRouteQuery query1 = new WalkRouteQuery(fromAndThrough1, walkMode);
            routeSearch.calculateWalkRouteAsyn(query1);
        }
        if(v==elevator){
            if(isSameFloor!="same") {
                elevator.setImageResource(R.drawable.elevator1);
                stair.setImageResource(R.drawable.stair);
                escalator.setImageResource(R.drawable.escalator);
                button = "电梯";
            }
            else{
                elevator.setImageResource(R.drawable.vending1);
                stair.setImageResource(R.drawable.information_desk);
                escalator.setImageResource(R.drawable.wtc);
                button = "售货机";
            }
            dissmissProgressDialog();
            aMap.clear();// 清理地图上的所有覆盖物

            LatLng start=new LatLng(startPoint.getLatitude(),startPoint.getLongitude());
            LatLng lat=null;
            if(isSameFloor!="same") {
                lat = chooseThroughPoint(start, "dianti");
            }
            else{
                lat = chooseThroughPoint(start, "drink");
            }
            throughPoint=new LatLonPoint(lat.latitude,lat.longitude);

            throughList=new ArrayList<LatLonPoint>();
            throughList.add(throughPoint);

            final RouteSearch.FromAndTo fromAndThrough = new RouteSearch.FromAndTo(startPoint,throughPoint);
            WalkRouteQuery query = new WalkRouteQuery(fromAndThrough, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);

            final RouteSearch.FromAndTo fromAndThrough1 = new RouteSearch.FromAndTo(throughPoint, endPoint);
            WalkRouteQuery query1 = new WalkRouteQuery(fromAndThrough1, walkMode);
            routeSearch.calculateWalkRouteAsyn(query1);
        }
        if(v==escalator){
            if(isSameFloor!="same") {
                escalator.setImageResource(R.drawable.escalator1);
                elevator.setImageResource(R.drawable.elevator);
                stair.setImageResource(R.drawable.stair);
                button = "扶梯";
            }
            else{
                escalator.setImageResource(R.drawable.wtc1);
                elevator.setImageResource(R.drawable.vending);
                stair.setImageResource(R.drawable.information_desk);
                button = "厕所";
            }

            dissmissProgressDialog();
            aMap.clear();// 清理地图上的所有覆盖物

            //计算离得最近的扶梯
            LatLng start=new LatLng(startPoint.getLatitude(),startPoint.getLongitude());
            LatLng lat=null;
            if(isSameFloor!="same") {
                lat = chooseThroughPoint(start, "futi");
            }
            else{
                lat = chooseThroughPoint(start, "toilet");
            }
            throughPoint=new LatLonPoint(lat.latitude,lat.longitude);

            throughList=new ArrayList<LatLonPoint>();
            throughList.add(throughPoint);

            final RouteSearch.FromAndTo fromAndThrough = new RouteSearch.FromAndTo(startPoint, throughPoint);
            WalkRouteQuery query = new WalkRouteQuery(fromAndThrough, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);

            final RouteSearch.FromAndTo fromAndThrough1 = new RouteSearch.FromAndTo(throughPoint, endPoint);
            WalkRouteQuery query1 = new WalkRouteQuery(fromAndThrough1, walkMode);
            routeSearch.calculateWalkRouteAsyn(query1);
        }
        if(v==cancel){
            ad.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
        }
        switch (v.getId()) {
            case R.id.show_more: {
                if (mState == SPREAD_STATE) {
                    mContentText.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
                    mContentText.requestLayout();
                    mImageShrinkUp.setVisibility(View.GONE);
                    mImageSpread.setVisibility(View.VISIBLE);
                    mState = SHRINK_UP_STATE;
                } else if (mState == SHRINK_UP_STATE) {
                    mContentText.setMaxLines(Integer.MAX_VALUE);
                    mContentText.requestLayout();
                    mImageShrinkUp.setVisibility(View.VISIBLE);
                    mImageSpread.setVisibility(View.GONE);
                    mState = SPREAD_STATE;
                }
                break;
            }
            default: {
                break;
            }
        }

    }
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    }
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }
    List<overlay.WalkStep> newslist=new ArrayList<overlay.WalkStep>();
    overlay.WalkPath newWalkPath=new overlay.WalkPath();
    overlay.WalkPath testPath=new overlay.WalkPath();
    List<WalkStep>steps=new ArrayList<WalkStep>();
    WalkPath path=new WalkPath();
    int size;
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        if(button!="normal"){
            times++;
            walkRouteResult = result;
//            steps.addAll(walkRouteResult.getPaths().get(0).getSteps());
//            path.setSteps(steps);
            WalkPath w=walkRouteResult.getPaths().get(0);
            newWalkPath.setDistance(w.getDistance());
            newWalkPath.setDuration(w.getDuration());
            List<WalkStep> slist=w.getSteps();
            for(int j=0;j<slist.size();j++){
                WalkStep s=slist.get(j);
                overlay.WalkStep news=new overlay.WalkStep();
                news.setDistance(s.getDistance());
                news.setAction(s.getAction());
                news.setAssistantAction(s.getAssistantAction());
                news.setDuration(s.getDuration());
                news.setInstruction(s.getInstruction());
                news.setOrientation(s.getOrientation());
                news.setPolyline(s.getPolyline());
                news.setRoad(s.getRoad());
                newslist.add(news);

            }
            newWalkPath.setSteps(newslist);
            List<overlay.WalkStep> detail = newWalkPath.getSteps();
//            List<WalkStep>detail=steps;
            if(times%2!=0){
                size=detail.size();
            }
            if(times%2==0) {

                //画出路线
                ThroughRouteOverlay th = new ThroughRouteOverlay(this, aMap, newWalkPath, startPoint, endPoint, throughList);
                th.removeFromMap();
                th.addToMap();
                th.zoomToSpan();
//                walkRouteOverlay = new WalkRouteOverlay(this, aMap, path,startPoint, walkRouteResult.getTargetPos());
//                walkRouteOverlay.removeFromMap();
//                walkRouteOverlay.addToMap();
//                walkRouteOverlay.zoomToSpan();
                //写文字提示
                walkdetails+=chooseOrientation(orientation,detail.get(0).getOrientation());
                for (int i = 0; i < size; i++) {
                    //walkdetails +="向"+detail.get(i).getOrientation()+"走"+(int)detail.get(i).getDistance()+"米"+", ";
                    walkdetails+=detail.get(i).getInstruction();
                    String walk[]=walkdetails.split("到达目的");
                    walkdetails=walk[0];
                }
                String panduan=findFloor(desText,codeFloor);
                if(panduan!="same"){
                    walkdetails+="到达"+button+","+"乘坐"+button+"到"+panduan+"层, 然后";
                }
                else walkdetails+="到达"+button+";";
                for (int i = size; i < detail.size(); i++) {
                    walkdetails+=detail.get(i).getInstruction()+", ";
                }
                //walkdetails+="到达目的地。";

                newslist=new ArrayList<overlay.WalkStep>();
                newWalkPath=new overlay.WalkPath();
                mContentText.setText(walkdetails);
                walkdetails="";
                testPath=newWalkPath;

                steps=new ArrayList<WalkStep>();
                path=new WalkPath();

                MarkerOptions otMarkerOptions = new MarkerOptions();
//        otMarkerOptions.position(new LatLng(throughPoint.getLatitude(),throughPoint.getLongitude()));
//        otMarkerOptions.visible(true);//设置可见
//        otMarkerOptions.title(button);
//        otMarkerOptions.draggable(true);
//        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_through));
//        aMap.addMarker(otMarkerOptions);
            }

        }
        else if(findFloor(desText,codeFloor)!="same"){
            onClick(escalator);
        }
        else {
            dissmissProgressDialog();
            aMap.clear();// 清理地图上的所有覆盖物
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                    walkRouteResult = result;
                    walkPath = walkRouteResult.getPaths().get(0);
                    aMap.clear();// 清理地图上的所有覆盖物
                    walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, startPoint, endPoint);
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "  距离" + AMapUtil.getFriendlyLength(dis);
                    List<WalkStep> detail = walkPath.getSteps();
                    String walkdetails = "";
                    walkdetails+=chooseOrientation(orientation,detail.get(0).getOrientation());
                    for (int i = 0; i < detail.size(); i++) {
                        walkdetails += detail.get(i).getInstruction() + ", ";
                    }
                    mContentText.setText(walkdetails);


                } else {
                    showToast("对不起，没有搜索到相关数据！");
                }
            } else if (rCode == 27) {
                showToast("搜索失败,请检查网络连接！");
            } else if (rCode == 32) {
                showToast("key验证无效！");
            } else {
                showToast("未知错误，请稍后重试!错误码为" + rCode);
            }


        }
    }

    private void route(LatLonPoint startPoint,LatLonPoint endPoint){
        // double latitude1,double longtitude1为开始位置，2位结束位置
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
        routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }
    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();       ;
    }
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    public void route_indoor(){
        routePlanning = new IMRoutePlanning(this, mRoutePlanningListener);
        //imDataManager.downloadBuildingData(this,"北京朝阳大悦城",imDataDownloadListener);
        // imDataManager.downloadBuildingData(this,"B000A856LJ",imDataDownloadListener);
        //IMBuildingInfo imBuildingInfo=imDataManager.getBuildingInfo("朝阳大悦城");
        //java.lang.String buildingID=imBuildingInfo.getBuildingId();
        routePlanning.excutePlanningPointToPoint("B000A856LJ",1,39.924542, 116.518858,1,39.925155, 116.517914);


    }
    public String findFloor(String text,int codeFloor){
//        List<IMSearchResult>list=new ArrayList<IMSearchResult>();
//        imDataManager=IMDataManager.getInstance();
//        imDataManager.downloadBuildingData(this,"B000A856LJ",mDataDownloadListener);
//        list=imDataManager.searchByName(name,1);
//        return list.get(0).getFloorNo();
        int index=text.indexOf(buildingName)+buildingName.length();
        char f=text.charAt(index);
        int end=text.indexOf("层")-1;
        String fString=String.valueOf(f);
        if(end!=index){
            for(int i=index+1;i<=end;i++){
                char c=text.charAt(i);
                String cString=String.valueOf(c);
                fString+=cString;
            }
        }
        int desFloor=(int)f-'0';
        if(desFloor==codeFloor)return "same";
        else return fString;

    }
    private IMMapLoadListener mMapLoadListener = new IMMapLoadListener() {

        @Override
        public void onMapLoadSuccess() {
            Toast.makeText(mIndoorMapFragment.getActivity(), "地图加载完毕",
                    Toast.LENGTH_LONG).show();
        }
        @Override
        public void onMapLoadFailure(MapLoadStatus mapLoadStatus) {
            Toast.makeText(mIndoorMapFragment.getActivity(), "地图加载失败,失败状态:" + mapLoadStatus, Toast.LENGTH_LONG).show();
        }
    };
    private IMDataDownloadListener mDataDownloadListener = new IMDataDownloadListener() {

        @Override
        public void onDownloadSuccess(String buildingId) {

        }

        @Override
        public void onDownloadFailure(String buildingId, DownloadStatusCode statusCode) {

        }
        @Override
        public void onDownloadProgress(String buildingId, float progress) {

        }

    };
    private IMRoutePlanningListener mRoutePlanningListener = new IMRoutePlanningListener(){
        @Override
        public void onPlanningSuccess(String routePlanningData) {
            imDataManager=IMDataManager.getInstance();
            mIndoorMapFragment.clearRouteResult();
            mIndoorMapFragment.setRouteStart(imDataManager.searchByName("悦诗风吟",1).get(0).getId()); // 通过PoiId设置起点PoiId点
            mIndoorMapFragment.setRouteStop(imDataManager.searchByName("无印良品",1).get(0).getId());  // 通过PoiId设置终点PoiId点
            mIndoorMapFragment.setRouteData(routePlanningData);	// 设置路算数据
            mIndoorMapFragment.refreshMap();
        }
        @Override
        public void onPlanningFailure(RoutePLanningStatus s) {}
    };
    public double getDistance(LatLng a,LatLng b ){
        Point p1=aMap.getProjection().toScreenLocation(a);
        Point p2=aMap.getProjection().toScreenLocation(b);
        double dis=Math.sqrt(Math.pow((p1.x-p2.x),2)+Math.pow((p1.y-p2.y),2));
        return dis;
    }
    public LatLng chooseThroughPoint(LatLng latLng,String type){
        double min=9999;
        double dis;
        LatLng minL=null;
        if(type=="dianti"){
            for(int i=0;i<dianti.size();i++){
                LatLng l=new LatLng(dianti.get(i).getLaititude(),dianti.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min) {
                    min = dis;
                    minL=l;
                }
            }
        }
        if(type=="futi"){
            for(int i=0;i<futi.size();i++){
                LatLng l=new LatLng(futi.get(i).getLaititude(),futi.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min){
                    min=dis;
                    minL=l;
                }
            }
        }
        if(type=="louti"){
            for(int i=0;i<louti.size();i++){
                LatLng l=new LatLng(louti.get(i).getLaititude(),louti.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min){
                    min=dis;
                    minL=l;
                }
            }
        }
        if(type=="drink"){
            for(int i=0;i<vending.size();i++){
                LatLng l=new LatLng(vending.get(i).getLaititude(),vending.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min){
                    min=dis;
                    minL=l;
                }
            }
        }
        if(type=="toilet"){
            for(int i=0;i<toilt.size();i++){
                LatLng l=new LatLng(toilt.get(i).getLaititude(),toilt.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min){
                    min=dis;
                    minL=l;
                }
            }
        }
        if(type=="ask"){
            for(int i=0;i<asking.size();i++){
                LatLng l=new LatLng(asking.get(i).getLaititude(),asking.get(i).getLongtitude());
                dis=getDistance(l,latLng);
                if(dis<min){
                    min=dis;
                    minL=l;
                }
            }
        }
        return minL;
    }
    public String chooseOrientation(String code,String step){
        String ori="";
        if(code.equals("N")){
            if(step.equals("北")){ori="直走,";}
            if(step.equals("南")){ori="向后转,";}
            if(step.equals("西")){ori="向左转,";}
            if(step.equals("东")){ori="向右转,";}
            if(step.equals("东北")){ori="向右前方,";}
            if(step.equals("东南")){ori="向右斜后方,";}
            if(step.equals("西北")){ori="向左前方,";}
            if(step.equals("西南")){ori="向左斜后方,";}

        }
        if(code=="S"){
            if(step=="北"){ori="向后转,";}
            if(step=="南"){ori="直走,";}
            if(step=="西"){ori="向右转,";}
            if(step=="东"){ori="向左转,";}
            if(step=="东北"){ori="向左前方,";}
            if(step=="东南"){ori="向左斜后方,";}
            if(step=="西北"){ori="向右前方,";}
            if(step=="西南"){ori="向右斜后方,";}

        }
        if(code=="W"){
            if(step=="北"){ori="向右转,";}
            if(step=="南"){ori="向左转,";}
            if(step=="西"){ori="直走,";}
            if(step=="东"){ori="向后转,";}
            if(step=="东北"){ori="向右后方转,";}
            if(step=="东南"){ori="向左后方转,";}
            if(step=="西北"){ori="向右前转,";}
            if(step=="西南"){ori="向左前转,";}

        }
        if(code=="E"){
            if(step=="北"){ori="向左转,";}
            if(step=="南"){ori="向右转,";}
            if(step=="西"){ori="向后转,";}
            if(step=="东"){ori="直走,";}
            if(step=="东北"){ori="向左后方转,";}
            if(step=="东南"){ori="向右后方转,";}
            if(step=="西北"){ori="向左前转,";}
            if(step=="西南"){ori="向右前转,";}

        }
        return ori;
    }
    class SelfDefine{
        private Double laititude;
        private Double longtitude;
        private int floor;
        public SelfDefine(){}
        public SelfDefine(Double laititude,Double longtitude){
            this.laititude=laititude;
            this.longtitude=longtitude;
        }
        public Double getLaititude() {
            return laititude;
        }
        public Double getLongtitude() {
            return longtitude;
        }
        public int getFloor() {
            return floor;
        }
        public void setLaititude(Double laititude) {
            this.laititude = laititude;
        }
        public void setLongtitude(Double longtitude) {
            this.longtitude = longtitude;
        }
        public void setFloor(int floor) {
            this.floor = floor;
        }

    }

}


