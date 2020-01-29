//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.pdf417;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.pdf417.decoder.Decoder;
import com.google.zxing.pdf417.detector.Detector;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.Hashtable;

public final class PDF417Reader implements Reader {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    public PDF417Reader() {
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return this.decode(image, (Hashtable)null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if(hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            BitMatrix detectorResult1 = QRCodeReader.extractPureBits(image.getBlackMatrix());
            decoderResult = this.decoder.decode(detectorResult1);
            points = NO_POINTS;
        } else {
            DetectorResult detectorResult = (new Detector(image)).detect();
            decoderResult = this.decoder.decode(detectorResult.getBits());
            points = detectorResult.getPoints();
        }

        return new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.PDF417);
    }

    public void reset() {
    }
}
