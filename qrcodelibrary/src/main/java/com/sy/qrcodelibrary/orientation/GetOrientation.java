package com.sy.qrcodelibrary.orientation;

/**
 * Created by Gong on 2017/5/5.
 */

public class GetOrientation {
    static final float D=75;  //误差，按5%计算

    public String Orientation() {
        if (Math.abs(QRCenters.getC().getY() - QRCenters.getB().getY()) < D && Math.abs(QRCenters.getA().getX() - QRCenters.getB().getX()) < D) {

            if (QRCenters.getB().getX() < QRCenters.getC().getX() && QRCenters.getB().getY() < QRCenters.getA().getY()) {
                return "N";
            }

            else{
                return "S";
            }
        }


        else if (Math.abs(QRCenters.getC().getX() - QRCenters.getB().getX()) < D && Math.abs(QRCenters.getA().getY() - QRCenters.getB().getY()) < D) {
            if (QRCenters.getB().getX() > QRCenters.getA().getX() && QRCenters.getC().getY() > QRCenters.getB().getY()) {
                return "W";
            }
            else{
                return "E";
            }
        }
        else{
            return "ERROR";
        }
    }
}
