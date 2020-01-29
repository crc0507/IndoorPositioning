//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.qrcode.detector.FinderPatternFinder;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.util.Hashtable;
import java.util.Vector;

final class MultiFinderPatternFinder extends FinderPatternFinder {
    private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
    private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0F;
    private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0F;
    private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05F;
    private static final float DIFF_MODSIZE_CUTOFF = 0.5F;

    MultiFinderPatternFinder(BitMatrix image) {
        super(image);
    }

    MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        super(image, resultPointCallback);
    }

    private FinderPattern[][] selectBestPatterns() throws NotFoundException {
        Vector possibleCenters = this.getPossibleCenters();
        int size = possibleCenters.size();
        if(size < 3) {
            throw NotFoundException.getNotFoundInstance();
        } else if(size == 3) {
            return new FinderPattern[][]{{(FinderPattern)possibleCenters.elementAt(0), (FinderPattern)possibleCenters.elementAt(1), (FinderPattern)possibleCenters.elementAt(2)}};
        } else {
            Collections.insertionSort(possibleCenters, new MultiFinderPatternFinder.ModuleSizeComparator());
            Vector results = new Vector();

            for(int resultArray = 0; resultArray < size - 2; ++resultArray) {
                FinderPattern i = (FinderPattern)possibleCenters.elementAt(resultArray);
                if(i != null) {
                    for(int i2 = resultArray + 1; i2 < size - 1; ++i2) {
                        FinderPattern p2 = (FinderPattern)possibleCenters.elementAt(i2);
                        if(p2 != null) {
                            float vModSize12 = (i.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(i.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                            float vModSize12A = Math.abs(i.getEstimatedModuleSize() - p2.getEstimatedModuleSize());
                            if(vModSize12A > 0.5F && vModSize12 >= 0.05F) {
                                break;
                            }

                            for(int i3 = i2 + 1; i3 < size; ++i3) {
                                FinderPattern p3 = (FinderPattern)possibleCenters.elementAt(i3);
                                if(p3 != null) {
                                    float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                                    float vModSize23A = Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize());
                                    if(vModSize23A > 0.5F && vModSize23 >= 0.05F) {
                                        break;
                                    }

                                    FinderPattern[] test = new FinderPattern[]{i, p2, p3};
                                    ResultPoint.orderBestPatterns(test);
                                    FinderPatternInfo info = new FinderPatternInfo(test);
                                    float dA = ResultPoint.distance(info.getTopLeft(), info.getBottomLeft());
                                    float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                                    float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                                    float estimatedModuleCount = (dA + dB) / i.getEstimatedModuleSize() / 2.0F;
                                    if(estimatedModuleCount <= 180.0F && estimatedModuleCount >= 9.0F) {
                                        float vABBC = Math.abs((dA - dB) / Math.min(dA, dB));
                                        if(vABBC < 0.1F) {
                                            float dCpy = (float)Math.sqrt((double)(dA * dA + dB * dB));
                                            float vPyC = Math.abs((dC - dCpy) / Math.min(dC, dCpy));
                                            if(vPyC < 0.1F) {
                                                results.addElement(test);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(results.isEmpty()) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                FinderPattern[][] var23 = new FinderPattern[results.size()][];

                for(int var24 = 0; var24 < results.size(); ++var24) {
                    var23[var24] = (FinderPattern[])((FinderPattern[])results.elementAt(var24));
                }

                return var23;
            }
        }
    }

    public FinderPatternInfo[] findMulti(Hashtable hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        BitMatrix image = this.getImage();
        int maxI = image.getHeight();
        int maxJ = image.getWidth();
        int iSkip = (int)((float)maxI / 228.0F * 3.0F);
        if(iSkip < 3 || tryHarder) {
            iSkip = 3;
        }

        int[] stateCount = new int[5];

        int resultArray;
        for(int patternInfo = iSkip - 1; patternInfo < maxI; patternInfo += iSkip) {
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            stateCount[3] = 0;
            stateCount[4] = 0;
            int result = 0;

            for(resultArray = 0; resultArray < maxJ; ++resultArray) {
                if(image.get(resultArray, patternInfo)) {
                    if((result & 1) == 1) {
                        ++result;
                    }

                    ++stateCount[result];
                } else if((result & 1) != 0) {
                    ++stateCount[result];
                } else if(result == 4) {
                    if(!foundPatternCross(stateCount)) {
                        stateCount[0] = stateCount[2];
                        stateCount[1] = stateCount[3];
                        stateCount[2] = stateCount[4];
                        stateCount[3] = 1;
                        stateCount[4] = 0;
                        result = 3;
                    } else {
                        boolean i = this.handlePossibleCenter(stateCount, patternInfo, resultArray);
                        if(!i) {
                            do {
                                ++resultArray;
                            } while(resultArray < maxJ && !image.get(resultArray, patternInfo));

                            --resultArray;
                        }

                        result = 0;
                        stateCount[0] = 0;
                        stateCount[1] = 0;
                        stateCount[2] = 0;
                        stateCount[3] = 0;
                        stateCount[4] = 0;
                    }
                } else {
                    ++result;
                    ++stateCount[result];
                }
            }

            if(foundPatternCross(stateCount)) {
                this.handlePossibleCenter(stateCount, patternInfo, maxJ);
            }
        }

        FinderPattern[][] var12 = this.selectBestPatterns();
        Vector var13 = new Vector();

        for(resultArray = 0; resultArray < var12.length; ++resultArray) {
            FinderPattern[] var14 = var12[resultArray];
            ResultPoint.orderBestPatterns(var14);
            var13.addElement(new FinderPatternInfo(var14));
        }

        if(var13.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        } else {
            FinderPatternInfo[] var16 = new FinderPatternInfo[var13.size()];

            for(int var15 = 0; var15 < var13.size(); ++var15) {
                var16[var15] = (FinderPatternInfo)var13.elementAt(var15);
            }

            return var16;
        }
    }

    private static class ModuleSizeComparator implements Comparator {
        private ModuleSizeComparator() {
        }

        public int compare(Object center1, Object center2) {
            float value = ((FinderPattern)center2).getEstimatedModuleSize() - ((FinderPattern)center1).getEstimatedModuleSize();
            return (double)value < 0.0D?-1:((double)value > 0.0D?1:0);
        }
    }
}
