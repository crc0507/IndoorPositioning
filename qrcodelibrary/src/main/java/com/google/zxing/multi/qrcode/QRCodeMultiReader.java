//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.multi.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.multi.qrcode.detector.MultiDetector;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.Hashtable;
import java.util.Vector;

public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {
    private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];

    public QRCodeMultiReader() {
    }

    public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
        return this.decodeMultiple(image, (Hashtable)null);
    }

    public Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        Vector results = new Vector();
        DetectorResult[] detectorResult = (new MultiDetector(image.getBlackMatrix())).detectMulti(hints);

        for(int resultArray = 0; resultArray < detectorResult.length; ++resultArray) {
            try {
                DecoderResult i = this.getDecoder().decode(detectorResult[resultArray].getBits());
                ResultPoint[] points = detectorResult[resultArray].getPoints();
                Result result = new Result(i.getText(), i.getRawBytes(), points, BarcodeFormat.QR_CODE);
                if(i.getByteSegments() != null) {
                    result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, i.getByteSegments());
                }

                if(i.getECLevel() != null) {
                    result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, i.getECLevel().toString());
                }

                results.addElement(result);
            } catch (ReaderException var9) {
                ;
            }
        }

        if(results.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        } else {
            Result[] var10 = new Result[results.size()];

            for(int var11 = 0; var11 < results.size(); ++var11) {
                var10[var11] = (Result)results.elementAt(var11);
            }

            return var10;
        }
    }
}
