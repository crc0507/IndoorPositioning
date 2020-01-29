//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.multi.MultipleBarcodeReader;
import java.util.Hashtable;
import java.util.Vector;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader {
    private static final int MIN_DIMENSION_TO_RECUR = 100;
    private final Reader delegate;

    public GenericMultipleBarcodeReader(Reader delegate) {
        this.delegate = delegate;
    }

    public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
        return this.decodeMultiple(image, (Hashtable)null);
    }

    public Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        Vector results = new Vector();
        this.doDecodeMultiple(image, hints, results, 0, 0);
        if(results.isEmpty()) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int numResults = results.size();
            Result[] resultArray = new Result[numResults];

            for(int i = 0; i < numResults; ++i) {
                resultArray[i] = (Result)results.elementAt(i);
            }

            return resultArray;
        }
    }

    private void doDecodeMultiple(BinaryBitmap image, Hashtable hints, Vector results, int xOffset, int yOffset) {
        Result result;
        try {
            result = this.delegate.decode(image, hints);
        } catch (ReaderException var19) {
            return;
        }

        boolean alreadyFound = false;

        for(int resultPoints = 0; resultPoints < results.size(); ++resultPoints) {
            Result width = (Result)results.elementAt(resultPoints);
            if(width.getText().equals(result.getText())) {
                alreadyFound = true;
                break;
            }
        }

        if(!alreadyFound) {
            results.addElement(translateResultPoints(result, xOffset, yOffset));
            ResultPoint[] var20 = result.getResultPoints();
            if(var20 != null && var20.length != 0) {
                int var21 = image.getWidth();
                int height = image.getHeight();
                float minX = (float)var21;
                float minY = (float)height;
                float maxX = 0.0F;
                float maxY = 0.0F;

                for(int i = 0; i < var20.length; ++i) {
                    ResultPoint point = var20[i];
                    float x = point.getX();
                    float y = point.getY();
                    if(x < minX) {
                        minX = x;
                    }

                    if(y < minY) {
                        minY = y;
                    }

                    if(x > maxX) {
                        maxX = x;
                    }

                    if(y > maxY) {
                        maxY = y;
                    }
                }

                if(minX > 100.0F) {
                    this.doDecodeMultiple(image.crop(0, 0, (int)minX, height), hints, results, xOffset, yOffset);
                }

                if(minY > 100.0F) {
                    this.doDecodeMultiple(image.crop(0, 0, var21, (int)minY), hints, results, xOffset, yOffset);
                }

                if(maxX < (float)(var21 - 100)) {
                    this.doDecodeMultiple(image.crop((int)maxX, 0, var21 - (int)maxX, height), hints, results, xOffset + (int)maxX, yOffset);
                }

                if(maxY < (float)(height - 100)) {
                    this.doDecodeMultiple(image.crop(0, (int)maxY, var21, height - (int)maxY), hints, results, xOffset, yOffset + (int)maxY);
                }

            }
        }
    }

    private static Result translateResultPoints(Result result, int xOffset, int yOffset) {
        ResultPoint[] oldResultPoints = result.getResultPoints();
        ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];

        for(int i = 0; i < oldResultPoints.length; ++i) {
            ResultPoint oldPoint = oldResultPoints[i];
            newResultPoints[i] = new ResultPoint(oldPoint.getX() + (float)xOffset, oldPoint.getY() + (float)yOffset);
        }

        return new Result(result.getText(), result.getRawBytes(), newResultPoints, result.getBarcodeFormat());
    }
}
