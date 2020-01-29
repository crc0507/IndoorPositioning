package com.sy.qrcodelibrary.orientation;

import com.google.zxing.qrcode.detector.FinderPattern;

/**
 * Created by Gong on 2017/5/5.
 */

public class QRCenters {
    private static Center A=new Center();
    private static Center B=new Center();
    private static Center C=new Center();

    public static void setCenters(FinderPattern[] patternCenters){
        A.setCoord(patternCenters[0].getX(),patternCenters[0].getY());
        B.setCoord(patternCenters[1].getX(),patternCenters[1].getY());
        C.setCoord(patternCenters[2].getX(),patternCenters[2].getY());
    }

    public static Center getA(){
        return A;
    }

    public static Center getB(){
        return B;
    }

    public static Center getC(){
        return C;
    }
}
