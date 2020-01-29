package com.example.emma.qrcode_iteration2;

import com.amap.api.services.core.PoiItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jizhimeicrc on 17/7/10.
 */

public class PoiData implements Serializable{
    private double longitude;//经度
    private double latitude;//纬度
    private String title;//信息标题
    private String text;//信息内容
    public PoiData(double lon, double lat, String title, String text){
        this.longitude = lon;
        this.latitude = lat;
        this.title = title;
        this.text = text;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public String getTitle() {
        return title;
    }
    public String getText(){
        return text;
    }

}
