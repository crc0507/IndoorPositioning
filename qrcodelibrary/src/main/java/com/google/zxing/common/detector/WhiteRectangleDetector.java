//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

public final class WhiteRectangleDetector {
    private static final int INIT_SIZE = 40;
    private static final int CORR = 1;
    private final BitMatrix image;
    private final int height;
    private final int width;

    public WhiteRectangleDetector(BitMatrix image) {
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
    }

    public ResultPoint[] detect() throws NotFoundException {
        int left = this.width - 40 >> 1;
        int right = this.width + 40 >> 1;
        int up = this.height - 40 >> 1;
        int down = this.height + 40 >> 1;
        boolean sizeExceeded = false;
        boolean aBlackPointFoundOnBorder = true;
        boolean atLeastOneBlackPointFoundOnBorder = false;

        while(aBlackPointFoundOnBorder) {
            aBlackPointFoundOnBorder = false;
            boolean maxSize = true;

            while(maxSize && right < this.width) {
                maxSize = this.containsBlackPoint(up, down, right, false);
                if(maxSize) {
                    ++right;
                    aBlackPointFoundOnBorder = true;
                }
            }

            if(right >= this.width) {
                sizeExceeded = true;
                break;
            }

            boolean z = true;

            while(z && down < this.height) {
                z = this.containsBlackPoint(left, right, down, true);
                if(z) {
                    ++down;
                    aBlackPointFoundOnBorder = true;
                }
            }

            if(down >= this.height) {
                sizeExceeded = true;
                break;
            }

            boolean t = true;

            while(t && left >= 0) {
                t = this.containsBlackPoint(up, down, left, false);
                if(t) {
                    --left;
                    aBlackPointFoundOnBorder = true;
                }
            }

            if(left < 0) {
                sizeExceeded = true;
                break;
            }

            boolean x = true;

            while(x && up >= 0) {
                x = this.containsBlackPoint(left, right, up, true);
                if(x) {
                    --up;
                    aBlackPointFoundOnBorder = true;
                }
            }

            if(up < 0) {
                sizeExceeded = true;
                break;
            }

            if(aBlackPointFoundOnBorder) {
                atLeastOneBlackPointFoundOnBorder = true;
            }
        }

        if(!sizeExceeded && atLeastOneBlackPointFoundOnBorder) {
            int var14 = right - left;
            ResultPoint var15 = null;

            for(int var16 = 1; var16 < var14; ++var16) {
                var15 = this.getBlackPointOnSegment((float)left, (float)(down - var16), (float)(left + var16), (float)down);
                if(var15 != null) {
                    break;
                }
            }

            if(var15 == null) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                ResultPoint var17 = null;

                for(int var18 = 1; var18 < var14; ++var18) {
                    var17 = this.getBlackPointOnSegment((float)left, (float)(up + var18), (float)(left + var18), (float)up);
                    if(var17 != null) {
                        break;
                    }
                }

                if(var17 == null) {
                    throw NotFoundException.getNotFoundInstance();
                } else {
                    ResultPoint var19 = null;

                    for(int y = 1; y < var14; ++y) {
                        var19 = this.getBlackPointOnSegment((float)right, (float)(up + y), (float)(right - y), (float)up);
                        if(var19 != null) {
                            break;
                        }
                    }

                    if(var19 == null) {
                        throw NotFoundException.getNotFoundInstance();
                    } else {
                        ResultPoint var20 = null;

                        for(int i = 1; i < var14; ++i) {
                            var20 = this.getBlackPointOnSegment((float)right, (float)(down - i), (float)(right - i), (float)down);
                            if(var20 != null) {
                                break;
                            }
                        }

                        if(var20 == null) {
                            throw NotFoundException.getNotFoundInstance();
                        } else {
                            return this.centerEdges(var20, var15, var19, var17);
                        }
                    }
                }
            }
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private static int round(float d) {
        return (int)(d + 0.5F);
    }

    private ResultPoint getBlackPointOnSegment(float aX, float aY, float bX, float bY) {
        int dist = distanceL2(aX, aY, bX, bY);
        float xStep = (bX - aX) / (float)dist;
        float yStep = (bY - aY) / (float)dist;

        for(int i = 0; i < dist; ++i) {
            int x = round(aX + (float)i * xStep);
            int y = round(aY + (float)i * yStep);
            if(this.image.get(x, y)) {
                return new ResultPoint((float)x, (float)y);
            }
        }

        return null;
    }

    private static int distanceL2(float aX, float aY, float bX, float bY) {
        float xDiff = aX - bX;
        float yDiff = aY - bY;
        return round((float)Math.sqrt((double)(xDiff * xDiff + yDiff * yDiff)));
    }

    private ResultPoint[] centerEdges(ResultPoint y, ResultPoint z, ResultPoint x, ResultPoint t) {
        float yi = y.getX();
        float yj = y.getY();
        float zi = z.getX();
        float zj = z.getY();
        float xi = x.getX();
        float xj = x.getY();
        float ti = t.getX();
        float tj = t.getY();
        return yi < (float)(this.width / 2)?new ResultPoint[]{new ResultPoint(ti - 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj + 1.0F), new ResultPoint(xi - 1.0F, xj - 1.0F), new ResultPoint(yi + 1.0F, yj - 1.0F)}:new ResultPoint[]{new ResultPoint(ti + 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj - 1.0F), new ResultPoint(xi - 1.0F, xj + 1.0F), new ResultPoint(yi - 1.0F, yj - 1.0F)};
    }

    private boolean containsBlackPoint(int a, int b, int fixed, boolean horizontal) {
        int y;
        if(horizontal) {
            for(y = a; y <= b; ++y) {
                if(this.image.get(y, fixed)) {
                    return true;
                }
            }
        } else {
            for(y = a; y <= b; ++y) {
                if(this.image.get(fixed, y)) {
                    return true;
                }
            }
        }

        return false;
    }
}
