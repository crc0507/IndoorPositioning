//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;

import java.util.Hashtable;
import java.util.Vector;

public class FinderPatternFinder {
    private static final int CENTER_QUORUM = 2;
    protected static final int MIN_SKIP = 3;
    protected static final int MAX_MODULES = 57;
    private static final int INTEGER_MATH_SHIFT = 8;
    private final BitMatrix image;
    private final Vector possibleCenters;
    private boolean hasSkipped;
    private final int[] crossCheckStateCount;
    private final ResultPointCallback resultPointCallback;

    public FinderPatternFinder(BitMatrix image) {
        this(image, (ResultPointCallback)null);
    }

    public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new Vector();
        this.crossCheckStateCount = new int[5];
        this.resultPointCallback = resultPointCallback;
    }

    protected BitMatrix getImage() {
        return this.image;
    }

    protected Vector getPossibleCenters() {
        return this.possibleCenters;
    }

    FinderPatternInfo find(Hashtable hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        int maxI = this.image.getHeight();
        int maxJ = this.image.getWidth();
        int iSkip = 3 * maxI / 228;//扫描密度  int iSkip = (3 * maxI) / (4 * MAX_MODULES);
        if(iSkip < 3 || tryHarder) {
            iSkip = 3;
        }

        boolean done = false;
        int[] stateCount = new int[5];
    //这一行中将连续的相同颜色的像素个数计入数组中，数组长度为5位，即去找黑\白\黑\白\黑的图像
    // 填满5位后检测这5位中像素个数是否比例为1:1:3:1:1（可以有50%的误差范围），如果满足条件就说明找到了定位符的大概位置
        for(int patternInfo = iSkip - 1; patternInfo < maxI && !done; patternInfo += iSkip) {
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            stateCount[3] = 0;
            stateCount[4] = 0;
            int currentState = 0;
            // 开始检测到黑色计入数组[0]，直到检测到白色之前都将数组[0]的值+1；检测到白色了就开始在数组[1]中计数，以此类推。
            for(int confirmed = 0; confirmed < maxJ; ++confirmed) {
                if(this.image.get(confirmed, patternInfo)) {
                    if((currentState & 1) == 1) {
                        ++currentState;
                    }

                    ++stateCount[currentState];
                } else if((currentState & 1) == 0) {
                    if(currentState == 4) {
                        if(foundPatternCross(stateCount)) {
                            boolean confirmed1 = this.handlePossibleCenter(stateCount, patternInfo, confirmed);
                            if(confirmed1) {
                                iSkip = 2;
                                if(this.hasSkipped) {
                                    done = this.haveMultiplyConfirmedCenters();
                                } else {
                                    int rowSkip = this.findRowSkip();
                                    if(rowSkip > stateCount[2]) {
                                        patternInfo += rowSkip - stateCount[2] - iSkip;
                                        confirmed = maxJ - 1;
                                    }
                                }

                                currentState = 0;
                                stateCount[0] = 0;
                                stateCount[1] = 0;
                                stateCount[2] = 0;
                                stateCount[3] = 0;
                                stateCount[4] = 0;
                            } else {
                                stateCount[0] = stateCount[2];
                                stateCount[1] = stateCount[3];
                                stateCount[2] = stateCount[4];
                                stateCount[3] = 1;
                                stateCount[4] = 0;
                                currentState = 3;
                            }
                        } else {
                            stateCount[0] = stateCount[2];
                            stateCount[1] = stateCount[3];
                            stateCount[2] = stateCount[4];
                            stateCount[3] = 1;
                            stateCount[4] = 0;
                            currentState = 3;
                        }
                    } else {
                        ++currentState;
                        ++stateCount[currentState];
                    }
                } else {
                    ++stateCount[currentState];
                }
            }
//将这个图像交给handlePossibleCenter方法去找到定位符的中心点，
            if(foundPatternCross(stateCount)) {
                boolean var14 = this.handlePossibleCenter(stateCount, patternInfo, maxJ);
                if(var14) {
                    iSkip = stateCount[0];
                    if(this.hasSkipped) {
                        done = this.haveMultiplyConfirmedCenters();
                    }
                }
            }
        }

        FinderPattern[] var13 = this.selectBestPatterns();
        ResultPoint.orderBestPatterns(var13);
        return new FinderPatternInfo(var13);
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return (float)(end - stateCount[4] - stateCount[3]) - (float)stateCount[2] / 2.0F;
    }
//满足1:1:3:1:1，允许50%的误差
    protected static boolean foundPatternCross(int[] stateCount) {
        int totalModuleSize = 0;
        int moduleSize;
        int maxVariance;
        for(moduleSize = 0; moduleSize < 5; ++moduleSize) {
            maxVariance = stateCount[moduleSize];
            if(maxVariance == 0) {
                return false;
            }

            totalModuleSize += maxVariance;
        }

        if(totalModuleSize < 7) {
            return false;
        } else {
            moduleSize = (totalModuleSize << 8) / 7;
            maxVariance = moduleSize / 2;
            //<< 左移运算符，num << 1,相当于num乘以2
            //>>右移运算符，num >> 1,相当于num除以2
            return Math.abs(moduleSize - (stateCount[0] << 8)) < maxVariance && Math.abs(moduleSize - (stateCount[1] << 8)) < maxVariance && Math.abs(3 * moduleSize - (stateCount[2] << 8)) < 3 * maxVariance && Math.abs(moduleSize - (stateCount[3] << 8)) < maxVariance && Math.abs(moduleSize - (stateCount[4] << 8)) < maxVariance;
        }
    }

    private int[] getCrossCheckStateCount() {
        this.crossCheckStateCount[0] = 0;
        this.crossCheckStateCount[1] = 0;
        this.crossCheckStateCount[2] = 0;
        this.crossCheckStateCount[3] = 0;
        this.crossCheckStateCount[4] = 0;
        return this.crossCheckStateCount;
    }

    private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxI = image.getHeight();
        int[] stateCount = this.getCrossCheckStateCount();
        int i;
        for(i = startI; i >= 0 && image.get(centerJ, i); --i) {
            ++stateCount[2];
        }

        if(i < 0) {
            return 0.0F / 0.0F;
        } else {
            while(i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
                ++stateCount[1];
                --i;
            }

            if(i >= 0 && stateCount[1] <= maxCount) {
                while(i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
                    ++stateCount[0];
                    --i;
                }

                if(stateCount[0] > maxCount) {
                    return 0.0F / 0.0F;
                } else {
                    for(i = startI + 1; i < maxI && image.get(centerJ, i); ++i) {
                        ++stateCount[2];
                    }

                    if(i == maxI) {
                        return 0.0F / 0.0F;
                    } else {
                        while(i < maxI && !image.get(centerJ, i) && stateCount[3] < maxCount) {
                            ++stateCount[3];
                            ++i;
                        }

                        if(i != maxI && stateCount[3] < maxCount) {
                            while(i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
                                ++stateCount[4];
                                ++i;
                            }

                            if(stateCount[4] >= maxCount) {
                                return 0.0F / 0.0F;
                            } else {
                                int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
                                return 5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal?0.0F / 0.0F:(foundPatternCross(stateCount)?centerFromEnd(stateCount, i):0.0F / 0.0F);
                            }
                        } else {
                            return 0.0F / 0.0F;
                        }
                    }
                }
            } else {
                return 0.0F / 0.0F;
            }
        }
    }

    private float crossCheckHorizontal(int startJ, int centerI, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxJ = image.getWidth();
        int[] stateCount = this.getCrossCheckStateCount();

        int j;
        for(j = startJ; j >= 0 && image.get(j, centerI); --j) {
            ++stateCount[2];
        }

        if(j < 0) {
            return 0.0F / 0.0F;
        } else {
            while(j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
                ++stateCount[1];
                --j;
            }

            if(j >= 0 && stateCount[1] <= maxCount) {
                while(j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
                    ++stateCount[0];
                    --j;
                }

                if(stateCount[0] > maxCount) {
                    return 0.0F / 0.0F;
                } else {
                    for(j = startJ + 1; j < maxJ && image.get(j, centerI); ++j) {
                        ++stateCount[2];
                    }

                    if(j == maxJ) {
                        return 0.0F / 0.0F;
                    } else {
                        while(j < maxJ && !image.get(j, centerI) && stateCount[3] < maxCount) {
                            ++stateCount[3];
                            ++j;
                        }

                        if(j != maxJ && stateCount[3] < maxCount) {
                            while(j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
                                ++stateCount[4];
                                ++j;
                            }

                            if(stateCount[4] >= maxCount) {
                                return 0.0F / 0.0F;
                            } else {
                                int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
                                return 5 * Math.abs(stateCountTotal - originalStateCountTotal) >= originalStateCountTotal?0.0F / 0.0F:(foundPatternCross(stateCount)?centerFromEnd(stateCount, j):0.0F / 0.0F);
                            }
                        } else {
                            return 0.0F / 0.0F;
                        }
                    }
                }
            } else {
                return 0.0F / 0.0F;
            }
        }
    }
    // 方法是先从垂直方向检测是否满足定位符的条件，如满足就定出Y轴的中心点坐标值，
    // 然后用这个坐标值去再次检测水平方向是否满足定位符条件，如满足就定出X轴的中心点坐标值。
    protected boolean handlePossibleCenter(int[] stateCount, int i, int j) {
        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = this.crossCheckVertical(i, (int)centerJ, stateCount[2], stateCountTotal);
        if(!Float.isNaN(centerI)) {
            centerJ = this.crossCheckHorizontal((int)centerJ, (int)centerI, stateCount[2], stateCountTotal);
            if(!Float.isNaN(centerJ)) {
                float estimatedModuleSize = (float)stateCountTotal / 7.0F;
                boolean found = false;
                int max = this.possibleCenters.size();

                for(int point = 0; point < max; ++point) {
                    FinderPattern center = (FinderPattern)this.possibleCenters.elementAt(point);
                    if(center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                        center.incrementCount();
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    FinderPattern var12 = new FinderPattern(centerJ, centerI, estimatedModuleSize);
                    this.possibleCenters.addElement(var12);
                    if(this.resultPointCallback != null) {
                        this.resultPointCallback.foundPossibleResultPoint(var12);
                    }
                }

                return true;
            }
        }

        return false;
    }

    private int findRowSkip() {
        int max = this.possibleCenters.size();
        if(max <= 1) {
            return 0;
        } else {
            FinderPattern firstConfirmedCenter = null;

            for(int i = 0; i < max; ++i) {
                FinderPattern center = (FinderPattern)this.possibleCenters.elementAt(i);
                if(center.getCount() >= 2) {
                    if(firstConfirmedCenter != null) {
                        this.hasSkipped = true;
                        return (int)(Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY())) / 2;
                    }

                    firstConfirmedCenter = center;
                }
            }

            return 0;
        }
    }

    private boolean haveMultiplyConfirmedCenters() {
        int confirmedCount = 0;
        float totalModuleSize = 0.0F;
        int max = this.possibleCenters.size();

        for(int average = 0; average < max; ++average) {
            FinderPattern totalDeviation = (FinderPattern)this.possibleCenters.elementAt(average);
            if(totalDeviation.getCount() >= 2) {
                ++confirmedCount;
                totalModuleSize += totalDeviation.getEstimatedModuleSize();
            }
        }

        if(confirmedCount < 3) {
            return false;
        } else {
            float var8 = totalModuleSize / (float)max;
            float var9 = 0.0F;

            for(int i = 0; i < max; ++i) {
                FinderPattern pattern = (FinderPattern)this.possibleCenters.elementAt(i);
                var9 += Math.abs(pattern.getEstimatedModuleSize() - var8);
            }

            return var9 <= 0.05F * totalModuleSize;
        }
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        int startSize = this.possibleCenters.size();
        if(startSize < 3) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            float totalModuleSize;
            float average;
            if(startSize > 3) {
                totalModuleSize = 0.0F;
                average = 0.0F;

                float stdDev;
                for(int average1 = 0; average1 < startSize; ++average1) {
                    stdDev = ((FinderPattern)this.possibleCenters.elementAt(average1)).getEstimatedModuleSize();
                    totalModuleSize += stdDev;
                    average += stdDev * stdDev;
                }

                float var10 = totalModuleSize / (float)startSize;
                stdDev = (float)Math.sqrt((double)(average / (float)startSize - var10 * var10));
                Collections.insertionSort(this.possibleCenters, new FinderPatternFinder.FurthestFromAverageComparator(var10));
                float limit = Math.max(0.2F * var10, stdDev);

                for(int i = 0; i < this.possibleCenters.size() && this.possibleCenters.size() > 3; ++i) {
                    FinderPattern pattern = (FinderPattern)this.possibleCenters.elementAt(i);
                    if(Math.abs(pattern.getEstimatedModuleSize() - var10) > limit) {
                        this.possibleCenters.removeElementAt(i);
                        --i;
                    }
                }
            }

            if(this.possibleCenters.size() > 3) {
                totalModuleSize = 0.0F;

                for(int var9 = 0; var9 < this.possibleCenters.size(); ++var9) {
                    totalModuleSize += ((FinderPattern)this.possibleCenters.elementAt(var9)).getEstimatedModuleSize();
                }

                average = totalModuleSize / (float)this.possibleCenters.size();
                Collections.insertionSort(this.possibleCenters, new FinderPatternFinder.CenterComparator(average));
                this.possibleCenters.setSize(3);
            }

            return new FinderPattern[]{(FinderPattern)this.possibleCenters.elementAt(0), (FinderPattern)this.possibleCenters.elementAt(1), (FinderPattern)this.possibleCenters.elementAt(2)};
        }
    }

    private static class CenterComparator implements Comparator {
        private final float average;

        public CenterComparator(float f) {
            this.average = f;
        }

        public int compare(Object center1, Object center2) {
            if(((FinderPattern)center2).getCount() != ((FinderPattern)center1).getCount()) {
                return ((FinderPattern)center2).getCount() - ((FinderPattern)center1).getCount();
            } else {
                float dA = Math.abs(((FinderPattern)center2).getEstimatedModuleSize() - this.average);
                float dB = Math.abs(((FinderPattern)center1).getEstimatedModuleSize() - this.average);
                return dA < dB?1:(dA == dB?0:-1);
            }
        }
    }

    private static class FurthestFromAverageComparator implements Comparator {
        private final float average;

        public FurthestFromAverageComparator(float f) {
            this.average = f;
        }

        public int compare(Object center1, Object center2) {
            float dA = Math.abs(((FinderPattern)center2).getEstimatedModuleSize() - this.average);
            float dB = Math.abs(((FinderPattern)center1).getEstimatedModuleSize() - this.average);
            return dA < dB?-1:(dA == dB?0:1);
        }
    }
}
