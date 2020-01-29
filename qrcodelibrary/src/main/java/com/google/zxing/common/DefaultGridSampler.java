//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.PerspectiveTransform;

public final class DefaultGridSampler extends GridSampler {
    public DefaultGridSampler() {
    }

    public BitMatrix sampleGrid(BitMatrix image, int dimension, float p1ToX, float p1ToY, float p2ToX, float p2ToY, float p3ToX, float p3ToY, float p4ToX, float p4ToY, float p1FromX, float p1FromY, float p2FromX, float p2FromY, float p3FromX, float p3FromY, float p4FromX, float p4FromY) throws NotFoundException {
        PerspectiveTransform transform = PerspectiveTransform.quadrilateralToQuadrilateral(p1ToX, p1ToY, p2ToX, p2ToY, p3ToX, p3ToY, p4ToX, p4ToY, p1FromX, p1FromY, p2FromX, p2FromY, p3FromX, p3FromY, p4FromX, p4FromY);
        return this.sampleGrid(image, dimension, transform);
    }

    public BitMatrix sampleGrid(BitMatrix image, int dimension, PerspectiveTransform transform) throws NotFoundException {
        BitMatrix bits = new BitMatrix(dimension);
        float[] points = new float[dimension << 1];

        for(int y = 0; y < dimension; ++y) {
            int max = points.length;
            float iValue = (float)y + 0.5F;

            int aioobe;
            for(aioobe = 0; aioobe < max; aioobe += 2) {
                points[aioobe] = (float)(aioobe >> 1) + 0.5F;
                points[aioobe + 1] = iValue;
            }

            transform.transformPoints(points);
            checkAndNudgePoints(image, points);

            try {
                for(aioobe = 0; aioobe < max; aioobe += 2) {
                    if(image.get((int)points[aioobe], (int)points[aioobe + 1])) {
                        bits.set(aioobe >> 1, y);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException var10) {
                throw NotFoundException.getNotFoundInstance();
            }
        }

        return bits;
    }
}
