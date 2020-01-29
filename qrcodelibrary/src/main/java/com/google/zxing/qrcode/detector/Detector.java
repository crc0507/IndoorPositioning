//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.PerspectiveTransform;
import com.google.zxing.qrcode.decoder.Version;

import java.util.Hashtable;

public class Detector {
    private final BitMatrix image;
    private ResultPointCallback resultPointCallback;

    public Detector(BitMatrix image) {
        this.image = image;
    }

    protected BitMatrix getImage() {
        return this.image;
    }

    protected ResultPointCallback getResultPointCallback() {
        return this.resultPointCallback;
    }

    public DetectorResult detect() throws NotFoundException, FormatException {
        return this.detect((Hashtable)null);
    }

    public DetectorResult detect(Hashtable hints) throws NotFoundException, FormatException {
        this.resultPointCallback = hints == null?null:(ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
        FinderPatternFinder finder = new FinderPatternFinder(this.image, this.resultPointCallback);
        FinderPatternInfo info = finder.find(hints);
        return this.processFinderPatternInfo(info);
    }

    protected DetectorResult processFinderPatternInfo(FinderPatternInfo info) throws NotFoundException, FormatException {
        FinderPattern topLeft = info.getTopLeft();
        FinderPattern topRight = info.getTopRight();
        FinderPattern bottomLeft = info.getBottomLeft();
        float moduleSize = this.calculateModuleSize(topLeft, topRight, bottomLeft);
        if(moduleSize < 1.0F) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int dimension = computeDimension(topLeft, topRight, bottomLeft, moduleSize);
            Version provisionalVersion = Version.getProvisionalVersionForDimension(dimension);
            int modulesBetweenFPCenters = provisionalVersion.getDimensionForVersion() - 7;
            AlignmentPattern alignmentPattern = null;
            if(provisionalVersion.getAlignmentPatternCenters().length > 0) {
                float transform = topRight.getX() - topLeft.getX() + bottomLeft.getX();
                float bits = topRight.getY() - topLeft.getY() + bottomLeft.getY();
                float points = 1.0F - 3.0F / (float)modulesBetweenFPCenters;
                int estAlignmentX = (int)(topLeft.getX() + points * (transform - topLeft.getX()));
                int estAlignmentY = (int)(topLeft.getY() + points * (bits - topLeft.getY()));
                int i = 4;

                while(i <= 16) {
                    try {
                        alignmentPattern = this.findAlignmentInRegion(moduleSize, estAlignmentX, estAlignmentY, (float)i);
                        break;
                    } catch (NotFoundException var17) {
                        i <<= 1;
                    }
                }
            }

            PerspectiveTransform transform1 = this.createTransform(topLeft, topRight, bottomLeft, alignmentPattern, dimension);
            BitMatrix bits1 = sampleGrid(this.image, transform1, dimension);
            ResultPoint[] points1;
            if(alignmentPattern == null) {
                points1 = new ResultPoint[]{bottomLeft, topLeft, topRight};
            } else {
                points1 = new ResultPoint[]{bottomLeft, topLeft, topRight, alignmentPattern};
            }

            return new DetectorResult(bits1, points1);
        }
    }

    public PerspectiveTransform createTransform(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft, ResultPoint alignmentPattern, int dimension) {
        float dimMinusThree = (float)dimension - 3.5F;
        float bottomRightX;
        float bottomRightY;
        float sourceBottomRightX;
        float sourceBottomRightY;
        if(alignmentPattern != null) {
            bottomRightX = alignmentPattern.getX();
            bottomRightY = alignmentPattern.getY();
            sourceBottomRightX = sourceBottomRightY = dimMinusThree - 3.0F;
        } else {
            bottomRightX = topRight.getX() - topLeft.getX() + bottomLeft.getX();
            bottomRightY = topRight.getY() - topLeft.getY() + bottomLeft.getY();
            sourceBottomRightY = dimMinusThree;
            sourceBottomRightX = dimMinusThree;
        }

        return PerspectiveTransform.quadrilateralToQuadrilateral(3.5F, 3.5F, dimMinusThree, 3.5F, sourceBottomRightX, sourceBottomRightY, 3.5F, dimMinusThree, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRightX, bottomRightY, bottomLeft.getX(), bottomLeft.getY());
    }

    private static BitMatrix sampleGrid(BitMatrix image, PerspectiveTransform transform, int dimension) throws NotFoundException {
        GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, transform);
    }

    protected static int computeDimension(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft, float moduleSize) throws NotFoundException {
        int tltrCentersDimension = round(ResultPoint.distance(topLeft, topRight) / moduleSize);
        int tlblCentersDimension = round(ResultPoint.distance(topLeft, bottomLeft) / moduleSize);
        int dimension = (tltrCentersDimension + tlblCentersDimension >> 1) + 7;
        switch(dimension & 3) {
            case 0:
                ++dimension;
            case 1:
            default:
                break;
            case 2:
                --dimension;
                break;
            case 3:
                throw NotFoundException.getNotFoundInstance();
        }

        return dimension;
    }

    protected float calculateModuleSize(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft) {
        return (this.calculateModuleSizeOneWay(topLeft, topRight) + this.calculateModuleSizeOneWay(topLeft, bottomLeft)) / 2.0F;
    }

    private float calculateModuleSizeOneWay(ResultPoint pattern, ResultPoint otherPattern) {
        float moduleSizeEst1 = this.sizeOfBlackWhiteBlackRunBothWays((int)pattern.getX(), (int)pattern.getY(), (int)otherPattern.getX(), (int)otherPattern.getY());
        float moduleSizeEst2 = this.sizeOfBlackWhiteBlackRunBothWays((int)otherPattern.getX(), (int)otherPattern.getY(), (int)pattern.getX(), (int)pattern.getY());
        return Float.isNaN(moduleSizeEst1)?moduleSizeEst2 / 7.0F:(Float.isNaN(moduleSizeEst2)?moduleSizeEst1 / 7.0F:(moduleSizeEst1 + moduleSizeEst2) / 14.0F);
    }

    private float sizeOfBlackWhiteBlackRunBothWays(int fromX, int fromY, int toX, int toY) {
        float result = this.sizeOfBlackWhiteBlackRun(fromX, fromY, toX, toY);
        float scale = 1.0F;
        int otherToX = fromX - (toX - fromX);
        if(otherToX < 0) {
            scale = (float)fromX / (float)(fromX - otherToX);
            otherToX = 0;
        } else if(otherToX > this.image.getWidth()) {
            scale = (float)(this.image.getWidth() - fromX) / (float)(otherToX - fromX);
            otherToX = this.image.getWidth();
        }

        int otherToY = (int)((float)fromY - (float)(toY - fromY) * scale);
        scale = 1.0F;
        if(otherToY < 0) {
            scale = (float)fromY / (float)(fromY - otherToY);
            otherToY = 0;
        } else if(otherToY > this.image.getHeight()) {
            scale = (float)(this.image.getHeight() - fromY) / (float)(otherToY - fromY);
            otherToY = this.image.getHeight();
        }

        otherToX = (int)((float)fromX + (float)(otherToX - fromX) * scale);
        result += this.sizeOfBlackWhiteBlackRun(fromX, fromY, otherToX, otherToY);
        return result;
    }

    private float sizeOfBlackWhiteBlackRun(int fromX, int fromY, int toX, int toY) {
        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        int dx;
        if(steep) {
            dx = fromX;
            fromX = fromY;
            fromY = dx;
            dx = toX;
            toX = toY;
            toY = dx;
        }

        dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = -dx >> 1;
        int ystep = fromY < toY?1:-1;
        int xstep = fromX < toX?1:-1;
        int state = 0;
        int diffX = fromX;

        int diffY;
        for(diffY = fromY; diffX != toX; diffX += xstep) {
            int realX = steep?diffY:diffX;
            int realY = steep?diffX:diffY;
            if(state == 1) {
                if(this.image.get(realX, realY)) {
                    ++state;
                }
            } else if(!this.image.get(realX, realY)) {
                ++state;
            }

            if(state == 3) {
                int diffX1 = diffX - fromX;
                int diffY1 = diffY - fromY;
                if(xstep < 0) {
                    ++diffX1;
                }

                return (float)Math.sqrt((double)(diffX1 * diffX1 + diffY1 * diffY1));
            }

            error += dy;
            if(error > 0) {
                if(diffY == toY) {
                    break;
                }

                diffY += ystep;
                error -= dx;
            }
        }

        diffX = toX - fromX;
        diffY = toY - fromY;
        return (float)Math.sqrt((double)(diffX * diffX + diffY * diffY));
    }

    protected AlignmentPattern findAlignmentInRegion(float overallEstModuleSize, int estAlignmentX, int estAlignmentY, float allowanceFactor) throws NotFoundException {
        int allowance = (int)(allowanceFactor * overallEstModuleSize);
        int alignmentAreaLeftX = Math.max(0, estAlignmentX - allowance);
        int alignmentAreaRightX = Math.min(this.image.getWidth() - 1, estAlignmentX + allowance);
        if((float)(alignmentAreaRightX - alignmentAreaLeftX) < overallEstModuleSize * 3.0F) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int alignmentAreaTopY = Math.max(0, estAlignmentY - allowance);
            int alignmentAreaBottomY = Math.min(this.image.getHeight() - 1, estAlignmentY + allowance);
            if((float)(alignmentAreaBottomY - alignmentAreaTopY) < overallEstModuleSize * 3.0F) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                AlignmentPatternFinder alignmentFinder = new AlignmentPatternFinder(this.image, alignmentAreaLeftX, alignmentAreaTopY, alignmentAreaRightX - alignmentAreaLeftX, alignmentAreaBottomY - alignmentAreaTopY, overallEstModuleSize, this.resultPointCallback);
                return alignmentFinder.find();
            }
        }
    }

    private static int round(float d) {
        return (int)(d + 0.5F);
    }
}
