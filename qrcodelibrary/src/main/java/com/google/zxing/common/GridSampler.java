//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DefaultGridSampler;
import com.google.zxing.common.PerspectiveTransform;

public abstract class GridSampler {
    private static GridSampler gridSampler = new DefaultGridSampler();

    public GridSampler() {
    }

    public static void setGridSampler(GridSampler newGridSampler) {
        if(newGridSampler == null) {
            throw new IllegalArgumentException();
        } else {
            gridSampler = newGridSampler;
        }
    }

    public static GridSampler getInstance() {
        return gridSampler;
    }

    public abstract BitMatrix sampleGrid(BitMatrix var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18) throws NotFoundException;

    public BitMatrix sampleGrid(BitMatrix image, int dimension, PerspectiveTransform transform) throws NotFoundException {
        throw new IllegalStateException();
    }

    protected static void checkAndNudgePoints(BitMatrix image, float[] points) throws NotFoundException {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean nudged = true;
        int offset = 0;

        while(true) {
            int x;
            int y;
            if(offset < points.length && nudged) {
                x = (int)points[offset];
                y = (int)points[offset + 1];
                if(x >= -1 && x <= width && y >= -1 && y <= height) {
                    nudged = false;
                    if(x == -1) {
                        points[offset] = 0.0F;
                        nudged = true;
                    } else if(x == width) {
                        points[offset] = (float)(width - 1);
                        nudged = true;
                    }

                    if(y == -1) {
                        points[offset + 1] = 0.0F;
                        nudged = true;
                    } else if(y == height) {
                        points[offset + 1] = (float)(height - 1);
                        nudged = true;
                    }

                    offset += 2;
                    continue;
                }

                throw NotFoundException.getNotFoundInstance();
            }

            nudged = true;
            offset = points.length - 2;

            while(true) {
                if(offset >= 0 && nudged) {
                    x = (int)points[offset];
                    y = (int)points[offset + 1];
                    if(x >= -1 && x <= width && y >= -1 && y <= height) {
                        nudged = false;
                        if(x == -1) {
                            points[offset] = 0.0F;
                            nudged = true;
                        } else if(x == width) {
                            points[offset] = (float)(width - 1);
                            nudged = true;
                        }

                        if(y == -1) {
                            points[offset + 1] = 0.0F;
                            nudged = true;
                        } else if(y == height) {
                            points[offset + 1] = (float)(height - 1);
                            nudged = true;
                        }

                        offset -= 2;
                        continue;
                    }

                    throw NotFoundException.getNotFoundInstance();
                }

                return;
            }
        }
    }
}
