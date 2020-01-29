//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Detector {
    private static final Integer[] INTEGERS = new Integer[]{new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4)};
    private final BitMatrix image;
    private final WhiteRectangleDetector rectangleDetector;

    public Detector(BitMatrix image) {
        this.image = image;
        this.rectangleDetector = new WhiteRectangleDetector(image);
    }

    public DetectorResult detect() throws NotFoundException {
        ResultPoint[] cornerPoints = this.rectangleDetector.detect();
        ResultPoint pointA = cornerPoints[0];
        ResultPoint pointB = cornerPoints[1];
        ResultPoint pointC = cornerPoints[2];
        ResultPoint pointD = cornerPoints[3];
        Vector transitions = new Vector(4);
        transitions.addElement(this.transitionsBetween(pointA, pointB));
        transitions.addElement(this.transitionsBetween(pointA, pointC));
        transitions.addElement(this.transitionsBetween(pointB, pointD));
        transitions.addElement(this.transitionsBetween(pointC, pointD));
        Collections.insertionSort(transitions, new Detector.ResultPointsAndTransitionsComparator());
        Detector.ResultPointsAndTransitions lSideOne = (Detector.ResultPointsAndTransitions)transitions.elementAt(0);
        Detector.ResultPointsAndTransitions lSideTwo = (Detector.ResultPointsAndTransitions)transitions.elementAt(1);
        Hashtable pointCount = new Hashtable();
        increment(pointCount, lSideOne.getFrom());
        increment(pointCount, lSideOne.getTo());
        increment(pointCount, lSideTwo.getFrom());
        increment(pointCount, lSideTwo.getTo());
        ResultPoint maybeTopLeft = null;
        ResultPoint bottomLeft = null;
        ResultPoint maybeBottomRight = null;
        Enumeration points = pointCount.keys();

        while(points.hasMoreElements()) {
            ResultPoint corners = (ResultPoint)points.nextElement();
            Integer bottomRight = (Integer)pointCount.get(corners);
            if(bottomRight.intValue() == 2) {
                bottomLeft = corners;
            } else if(maybeTopLeft == null) {
                maybeTopLeft = corners;
            } else {
                maybeBottomRight = corners;
            }
        }

        if(maybeTopLeft != null && bottomLeft != null && maybeBottomRight != null) {
            ResultPoint[] var22 = new ResultPoint[]{maybeTopLeft, bottomLeft, maybeBottomRight};
            ResultPoint.orderBestPatterns(var22);
            ResultPoint var23 = var22[0];
            bottomLeft = var22[1];
            ResultPoint topLeft = var22[2];
            ResultPoint topRight;
            if(!pointCount.containsKey(pointA)) {
                topRight = pointA;
            } else if(!pointCount.containsKey(pointB)) {
                topRight = pointB;
            } else if(!pointCount.containsKey(pointC)) {
                topRight = pointC;
            } else {
                topRight = pointD;
            }

            int dimension = Math.min(this.transitionsBetween(topLeft, topRight).getTransitions(), this.transitionsBetween(var23, topRight).getTransitions());
            if((dimension & 1) == 1) {
                ++dimension;
            }

            dimension += 2;
            ResultPoint correctedTopRight = this.correctTopRight(bottomLeft, var23, topLeft, topRight, dimension);
            if(correctedTopRight == null) {
                correctedTopRight = topRight;
            }

            int dimension2 = Math.max(this.transitionsBetween(topLeft, correctedTopRight).getTransitions(), this.transitionsBetween(var23, correctedTopRight).getTransitions());
            ++dimension2;
            if((dimension2 & 1) == 1) {
                ++dimension2;
            }

            BitMatrix bits = sampleGrid(this.image, topLeft, bottomLeft, var23, correctedTopRight, dimension2);
            return new DetectorResult(bits, new ResultPoint[]{topLeft, bottomLeft, var23, correctedTopRight});
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private ResultPoint correctTopRight(ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topLeft, ResultPoint topRight, int dimension) {
        float corr = (float)distance(bottomLeft, bottomRight) / (float)dimension;
        int norm = distance(topLeft, topRight);
        float cos = (topRight.getX() - topLeft.getX()) / (float)norm;
        float sin = (topRight.getY() - topLeft.getY()) / (float)norm;
        ResultPoint c1 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        corr = (float)distance(bottomLeft, bottomRight) / (float)dimension;
        norm = distance(bottomRight, topRight);
        cos = (topRight.getX() - bottomRight.getX()) / (float)norm;
        sin = (topRight.getY() - bottomRight.getY()) / (float)norm;
        ResultPoint c2 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        if(!this.isValid(c1)) {
            return this.isValid(c2)?c2:null;
        } else if(!this.isValid(c2)) {
            return c1;
        } else {
            int l1 = Math.abs(this.transitionsBetween(topLeft, c1).getTransitions() - this.transitionsBetween(bottomRight, c1).getTransitions());
            int l2 = Math.abs(this.transitionsBetween(topLeft, c2).getTransitions() - this.transitionsBetween(bottomRight, c2).getTransitions());
            return l1 <= l2?c1:c2;
        }
    }

    private boolean isValid(ResultPoint p) {
        return p.getX() >= 0.0F && p.getX() < (float)this.image.width && p.getY() > 0.0F && p.getY() < (float)this.image.height;
    }

    private static int round(float d) {
        return (int)(d + 0.5F);
    }

    private static int distance(ResultPoint a, ResultPoint b) {
        return round((float)Math.sqrt((double)((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()))));
    }

    private static void increment(Hashtable table, ResultPoint key) {
        Integer value = (Integer)table.get(key);
        table.put(key, value == null?INTEGERS[1]:INTEGERS[value.intValue() + 1]);
    }

    private static BitMatrix sampleGrid(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topRight, int dimension) throws NotFoundException {
        GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, 0.5F, 0.5F, (float)dimension - 0.5F, 0.5F, (float)dimension - 0.5F, (float)dimension - 0.5F, 0.5F, (float)dimension - 0.5F, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }

    private Detector.ResultPointsAndTransitions transitionsBetween(ResultPoint from, ResultPoint to) {
        int fromX = (int)from.getX();
        int fromY = (int)from.getY();
        int toX = (int)to.getX();
        int toY = (int)to.getY();
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
        int transitions = 0;
        boolean inBlack = this.image.get(steep?fromY:fromX, steep?fromX:fromY);
        int x = fromX;

        for(int y = fromY; x != toX; x += xstep) {
            boolean isBlack = this.image.get(steep?y:x, steep?x:y);
            if(isBlack != inBlack) {
                ++transitions;
                inBlack = isBlack;
            }

            error += dy;
            if(error > 0) {
                if(y == toY) {
                    break;
                }

                y += ystep;
                error -= dx;
            }
        }

        return new Detector.ResultPointsAndTransitions(from, to, transitions);
    }

    private static class ResultPointsAndTransitionsComparator implements Comparator {
        private ResultPointsAndTransitionsComparator() {
        }

        public int compare(Object o1, Object o2) {
            return ((Detector.ResultPointsAndTransitions)o1).getTransitions() - ((Detector.ResultPointsAndTransitions)o2).getTransitions();
        }
    }

    private static class ResultPointsAndTransitions {
        private final ResultPoint from;
        private final ResultPoint to;
        private final int transitions;

        private ResultPointsAndTransitions(ResultPoint from, ResultPoint to, int transitions) {
            this.from = from;
            this.to = to;
            this.transitions = transitions;
        }

        public ResultPoint getFrom() {
            return this.from;
        }

        public ResultPoint getTo() {
            return this.to;
        }

        public int getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return this.from + "/" + this.to + '/' + this.transitions;
        }
    }
}
