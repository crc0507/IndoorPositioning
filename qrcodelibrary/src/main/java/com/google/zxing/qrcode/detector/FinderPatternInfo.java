//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.detector;

import android.util.Log;

import com.sy.qrcodelibrary.orientation.QRCenters;

public final class FinderPatternInfo {
    private final FinderPattern bottomLeft;
    private final FinderPattern topLeft;
    private final FinderPattern topRight;

    public FinderPatternInfo(FinderPattern[] patternCenters) {
        this.bottomLeft = patternCenters[0];
        this.topLeft = patternCenters[1];
        this.topRight = patternCenters[2];

        QRCenters centers=new QRCenters();
        centers.setCenters(patternCenters);

        Log.d("左下A：",patternCenters[0].getX()+","+patternCenters[0].getY());
        Log.d("左上B：",patternCenters[1].getX()+","+patternCenters[1].getY());
        Log.d("右上C：",patternCenters[2].getX()+","+patternCenters[2].getY());
    }

    public FinderPattern getBottomLeft() {
        return this.bottomLeft;
    }

    public FinderPattern getTopLeft() {
        return this.topLeft;
    }

    public FinderPattern getTopRight() {
        return this.topRight;
    }
}
