//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;

public final class HybridBinarizer extends GlobalHistogramBinarizer {
    private static final int MINIMUM_DIMENSION = 40;
    private BitMatrix matrix = null;

    public HybridBinarizer(LuminanceSource source) {
        super(source);
    }

    public BitMatrix getBlackMatrix() throws NotFoundException {
        this.binarizeEntireImage();
        return this.matrix;
    }

    public Binarizer createBinarizer(LuminanceSource source) {
        return new HybridBinarizer(source);
    }

    private void binarizeEntireImage() throws NotFoundException {
        if(this.matrix == null) {
            LuminanceSource source = this.getLuminanceSource();
            if(source.getWidth() >= 40 && source.getHeight() >= 40) {
                byte[] luminances = source.getMatrix();
                int width = source.getWidth();
                int height = source.getHeight();
                int subWidth = width >> 3;
                if((width & 7) != 0) {
                    ++subWidth;
                }

                int subHeight = height >> 3;
                if((height & 7) != 0) {
                    ++subHeight;
                }

                int[][] blackPoints = calculateBlackPoints(luminances, subWidth, subHeight, width, height);
                this.matrix = new BitMatrix(width, height);
                calculateThresholdForBlock(luminances, subWidth, subHeight, width, height, blackPoints, this.matrix);
            } else {
                this.matrix = super.getBlackMatrix();
            }
        }

    }

    private static void calculateThresholdForBlock(byte[] luminances, int subWidth, int subHeight, int width, int height, int[][] blackPoints, BitMatrix matrix) {
        for(int y = 0; y < subHeight; ++y) {
            int yoffset = y << 3;
            if(yoffset + 8 >= height) {
                yoffset = height - 8;
            }

            for(int x = 0; x < subWidth; ++x) {
                int xoffset = x << 3;
                if(xoffset + 8 >= width) {
                    xoffset = width - 8;
                }

                int left = x > 1?x:2;
                left = left < subWidth - 2?left:subWidth - 3;
                int top = y > 1?y:2;
                top = top < subHeight - 2?top:subHeight - 3;
                int sum = 0;

                int average;
                for(average = -2; average <= 2; ++average) {
                    int[] blackRow = blackPoints[top + average];
                    sum += blackRow[left - 2];
                    sum += blackRow[left - 1];
                    sum += blackRow[left];
                    sum += blackRow[left + 1];
                    sum += blackRow[left + 2];
                }

                average = sum / 25;
                threshold8x8Block(luminances, xoffset, yoffset, average, width, matrix);
            }
        }

    }

    private static void threshold8x8Block(byte[] luminances, int xoffset, int yoffset, int threshold, int stride, BitMatrix matrix) {
        for(int y = 0; y < 8; ++y) {
            int offset = (yoffset + y) * stride + xoffset;

            for(int x = 0; x < 8; ++x) {
                int pixel = luminances[offset + x] & 255;
                if(pixel < threshold) {
                    matrix.set(xoffset + x, yoffset + y);
                }
            }
        }

    }

    private static int[][] calculateBlackPoints(byte[] luminances, int subWidth, int subHeight, int width, int height) {
        int[][] blackPoints = new int[subHeight][subWidth];

        for(int y = 0; y < subHeight; ++y) {
            int yoffset = y << 3;
            if(yoffset + 8 >= height) {
                yoffset = height - 8;
            }

            for(int x = 0; x < subWidth; ++x) {
                int xoffset = x << 3;
                if(xoffset + 8 >= width) {
                    xoffset = width - 8;
                }

                int sum = 0;
                int min = 255;
                int max = 0;

                int average;
                for(average = 0; average < 8; ++average) {
                    int offset = (yoffset + average) * width + xoffset;

                    for(int xx = 0; xx < 8; ++xx) {
                        int pixel = luminances[offset + xx] & 255;
                        sum += pixel;
                        if(pixel < min) {
                            min = pixel;
                        }

                        if(pixel > max) {
                            max = pixel;
                        }
                    }
                }

                if(max - min > 24) {
                    average = sum >> 6;
                } else {
                    average = max == 0?1:min >> 1;
                }

                blackPoints[y][x] = average;
            }
        }

        return blackPoints;
    }
}
