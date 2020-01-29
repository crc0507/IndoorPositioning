//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.pdf417.detector;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import java.util.Hashtable;

public final class Detector {
    private static final int MAX_AVG_VARIANCE = 107;
    private static final int MAX_INDIVIDUAL_VARIANCE = 204;
    private static final int SKEW_THRESHOLD = 2;
    private static final int[] START_PATTERN = new int[]{8, 1, 1, 1, 1, 1, 1, 3};
    private static final int[] START_PATTERN_REVERSE = new int[]{3, 1, 1, 1, 1, 1, 1, 8};
    private static final int[] STOP_PATTERN = new int[]{7, 1, 1, 3, 1, 1, 1, 2, 1};
    private static final int[] STOP_PATTERN_REVERSE = new int[]{1, 2, 1, 1, 1, 3, 1, 1, 7};
    private final BinaryBitmap image;

    public Detector(BinaryBitmap image) {
        this.image = image;
    }

    public DetectorResult detect() throws NotFoundException {
        return this.detect((Hashtable)null);
    }

    public DetectorResult detect(Hashtable hints) throws NotFoundException {
        BitMatrix matrix = this.image.getBlackMatrix();
        ResultPoint[] vertices = findVertices(matrix);
        if(vertices == null) {
            vertices = findVertices180(matrix);
            if(vertices != null) {
                correctCodeWordVertices(vertices, true);
            }
        } else {
            correctCodeWordVertices(vertices, false);
        }

        if(vertices == null) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            float moduleWidth = computeModuleWidth(vertices);
            if(moduleWidth < 1.0F) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                int dimension = computeDimension(vertices[4], vertices[6], vertices[5], vertices[7], moduleWidth);
                if(dimension < 1) {
                    throw NotFoundException.getNotFoundInstance();
                } else {
                    BitMatrix bits = sampleGrid(matrix, vertices[4], vertices[5], vertices[6], vertices[7], dimension);
                    return new DetectorResult(bits, new ResultPoint[]{vertices[4], vertices[5], vertices[6], vertices[7]});
                }
            }
        }
    }

    private static ResultPoint[] findVertices(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        ResultPoint[] result = new ResultPoint[8];
        boolean found = false;

        int i;
        int[] loc;
        for(i = 0; i < height; ++i) {
            loc = findGuardPattern(matrix, 0, i, width, false, START_PATTERN);
            if(loc != null) {
                result[0] = new ResultPoint((float)loc[0], (float)i);
                result[4] = new ResultPoint((float)loc[1], (float)i);
                found = true;
                break;
            }
        }

        if(found) {
            found = false;

            for(i = height - 1; i > 0; --i) {
                loc = findGuardPattern(matrix, 0, i, width, false, START_PATTERN);
                if(loc != null) {
                    result[1] = new ResultPoint((float)loc[0], (float)i);
                    result[5] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }

        if(found) {
            found = false;

            for(i = 0; i < height; ++i) {
                loc = findGuardPattern(matrix, 0, i, width, false, STOP_PATTERN);
                if(loc != null) {
                    result[2] = new ResultPoint((float)loc[1], (float)i);
                    result[6] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }

        if(found) {
            found = false;

            for(i = height - 1; i > 0; --i) {
                loc = findGuardPattern(matrix, 0, i, width, false, STOP_PATTERN);
                if(loc != null) {
                    result[3] = new ResultPoint((float)loc[1], (float)i);
                    result[7] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }

        return found?result:null;
    }

    private static ResultPoint[] findVertices180(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        int halfWidth = width >> 1;
        ResultPoint[] result = new ResultPoint[8];
        boolean found = false;

        int i;
        int[] loc;
        for(i = height - 1; i > 0; --i) {
            loc = findGuardPattern(matrix, halfWidth, i, halfWidth, true, START_PATTERN_REVERSE);
            if(loc != null) {
                result[0] = new ResultPoint((float)loc[1], (float)i);
                result[4] = new ResultPoint((float)loc[0], (float)i);
                found = true;
                break;
            }
        }

        if(found) {
            found = false;

            for(i = 0; i < height; ++i) {
                loc = findGuardPattern(matrix, halfWidth, i, halfWidth, true, START_PATTERN_REVERSE);
                if(loc != null) {
                    result[1] = new ResultPoint((float)loc[1], (float)i);
                    result[5] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }

        if(found) {
            found = false;

            for(i = height - 1; i > 0; --i) {
                loc = findGuardPattern(matrix, 0, i, halfWidth, false, STOP_PATTERN_REVERSE);
                if(loc != null) {
                    result[2] = new ResultPoint((float)loc[0], (float)i);
                    result[6] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }

        if(found) {
            found = false;

            for(i = 0; i < height; ++i) {
                loc = findGuardPattern(matrix, 0, i, halfWidth, false, STOP_PATTERN_REVERSE);
                if(loc != null) {
                    result[3] = new ResultPoint((float)loc[0], (float)i);
                    result[7] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }

        return found?result:null;
    }

    private static void correctCodeWordVertices(ResultPoint[] vertices, boolean upsideDown) {
        float skew = vertices[4].getY() - vertices[6].getY();
        if(upsideDown) {
            skew = -skew;
        }

        float length;
        float deltax;
        float deltay;
        float correction;
        if(skew > 2.0F) {
            length = vertices[4].getX() - vertices[0].getX();
            deltax = vertices[6].getX() - vertices[0].getX();
            deltay = vertices[6].getY() - vertices[0].getY();
            correction = length * deltay / deltax;
            vertices[4] = new ResultPoint(vertices[4].getX(), vertices[4].getY() + correction);
        } else if(-skew > 2.0F) {
            length = vertices[2].getX() - vertices[6].getX();
            deltax = vertices[2].getX() - vertices[4].getX();
            deltay = vertices[2].getY() - vertices[4].getY();
            correction = length * deltay / deltax;
            vertices[6] = new ResultPoint(vertices[6].getX(), vertices[6].getY() - correction);
        }

        skew = vertices[7].getY() - vertices[5].getY();
        if(upsideDown) {
            skew = -skew;
        }

        if(skew > 2.0F) {
            length = vertices[5].getX() - vertices[1].getX();
            deltax = vertices[7].getX() - vertices[1].getX();
            deltay = vertices[7].getY() - vertices[1].getY();
            correction = length * deltay / deltax;
            vertices[5] = new ResultPoint(vertices[5].getX(), vertices[5].getY() + correction);
        } else if(-skew > 2.0F) {
            length = vertices[3].getX() - vertices[7].getX();
            deltax = vertices[3].getX() - vertices[5].getX();
            deltay = vertices[3].getY() - vertices[5].getY();
            correction = length * deltay / deltax;
            vertices[7] = new ResultPoint(vertices[7].getX(), vertices[7].getY() - correction);
        }

    }

    private static float computeModuleWidth(ResultPoint[] vertices) {
        float pixels1 = ResultPoint.distance(vertices[0], vertices[4]);
        float pixels2 = ResultPoint.distance(vertices[1], vertices[5]);
        float moduleWidth1 = (pixels1 + pixels2) / 34.0F;
        float pixels3 = ResultPoint.distance(vertices[6], vertices[2]);
        float pixels4 = ResultPoint.distance(vertices[7], vertices[3]);
        float moduleWidth2 = (pixels3 + pixels4) / 36.0F;
        return (moduleWidth1 + moduleWidth2) / 2.0F;
    }

    private static int computeDimension(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft, ResultPoint bottomRight, float moduleWidth) {
        int topRowDimension = round(ResultPoint.distance(topLeft, topRight) / moduleWidth);
        int bottomRowDimension = round(ResultPoint.distance(bottomLeft, bottomRight) / moduleWidth);
        return ((topRowDimension + bottomRowDimension >> 1) + 8) / 17 * 17;
    }

    private static BitMatrix sampleGrid(BitMatrix matrix, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight, int dimension) throws NotFoundException {
        GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(matrix, dimension, 0.0F, 0.0F, (float)dimension, 0.0F, (float)dimension, (float)dimension, 0.0F, (float)dimension, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }

    private static int round(float d) {
        return (int)(d + 0.5F);
    }

    private static int[] findGuardPattern(BitMatrix matrix, int column, int row, int width, boolean whiteFirst, int[] pattern) {
        int patternLength = pattern.length;
        int[] counters = new int[patternLength];
        boolean isWhite = whiteFirst;
        int counterPosition = 0;
        int patternStart = column;

        for(int x = column; x < column + width; ++x) {
            boolean pixel = matrix.get(x, row);
            if(pixel ^ isWhite) {
                ++counters[counterPosition];
            } else {
                if(counterPosition == patternLength - 1) {
                    if(patternMatchVariance(counters, pattern, 204) < 107) {
                        return new int[]{patternStart, x};
                    }

                    patternStart += counters[0] + counters[1];

                    for(int y = 2; y < patternLength; ++y) {
                        counters[y - 2] = counters[y];
                    }

                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    --counterPosition;
                } else {
                    ++counterPosition;
                }

                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }

        return null;
    }

    private static int patternMatchVariance(int[] counters, int[] pattern, int maxIndividualVariance) {
        int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;

        int unitBarWidth;
        for(unitBarWidth = 0; unitBarWidth < numCounters; ++unitBarWidth) {
            total += counters[unitBarWidth];
            patternLength += pattern[unitBarWidth];
        }

        if(total < patternLength) {
            return 2147483647;
        } else {
            unitBarWidth = (total << 8) / patternLength;
            maxIndividualVariance = maxIndividualVariance * unitBarWidth >> 8;
            int totalVariance = 0;

            for(int x = 0; x < numCounters; ++x) {
                int counter = counters[x] << 8;
                int scaledPattern = pattern[x] * unitBarWidth;
                int variance = counter > scaledPattern?counter - scaledPattern:scaledPattern - counter;
                if(variance > maxIndividualVariance) {
                    return 2147483647;
                }

                totalVariance += variance;
            }

            return totalVariance / total;
        }
    }
}
