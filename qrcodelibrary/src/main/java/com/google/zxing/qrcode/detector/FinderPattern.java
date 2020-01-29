//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class FinderPattern extends ResultPoint {
    private final float estimatedModuleSize;
    private int count;

    FinderPattern(float posX, float posY, float estimatedModuleSize) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
        this.count = 1;
    }

    public float getEstimatedModuleSize() {
        return this.estimatedModuleSize;
    }

    int getCount() {
        return this.count;
    }

    void incrementCount() {
        ++this.count;
    }

    boolean aboutEquals(float moduleSize, float i, float j) {
        if(Math.abs(i - this.getY()) <= moduleSize && Math.abs(j - this.getX()) <= moduleSize) {
            float moduleSizeDiff = Math.abs(moduleSize - this.estimatedModuleSize);
            return moduleSizeDiff <= 1.0F || moduleSizeDiff / this.estimatedModuleSize <= 1.0F;
        } else {
            return false;
        }
    }
}
