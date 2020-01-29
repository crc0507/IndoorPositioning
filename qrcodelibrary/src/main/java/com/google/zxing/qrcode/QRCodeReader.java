//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.qrcode.detector.Detector;
import java.util.Hashtable;

public class QRCodeReader implements Reader {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    public QRCodeReader() {
    }

    protected Decoder getDecoder() {
        return this.decoder;
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return this.decode(image, (Hashtable)null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if(hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            BitMatrix result1 = extractPureBits(image.getBlackMatrix());
            decoderResult = this.decoder.decode(result1, hints);
            points = NO_POINTS;
        } else {
            DetectorResult result = (new Detector(image.getBlackMatrix())).detect(hints);
            decoderResult = this.decoder.decode(result.getBits(), hints);
            points = result.getPoints();
        }

        Result result2 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
        if(decoderResult.getByteSegments() != null) {
            result2.putMetadata(ResultMetadataType.BYTE_SEGMENTS, decoderResult.getByteSegments());
        }

        if(decoderResult.getECLevel() != null) {
            result2.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel().toString());
        }

        return result2;
    }

    public void reset() {
    }

    public static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
        int height = image.getHeight();
        int width = image.getWidth();
        int minDimension = Math.min(height, width);
        int[] leftTopBlack = image.getTopLeftOnBit();
        if(leftTopBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int x = leftTopBlack[0];

            int y;
            for(y = leftTopBlack[1]; x < minDimension && y < minDimension && image.get(x, y); ++y) {
                ++x;
            }

            if(x != minDimension && y != minDimension) {
                int moduleSize = x - leftTopBlack[0];
                if(moduleSize == 0) {
                    throw NotFoundException.getNotFoundInstance();
                } else {
                    int rowEndOfSymbol;
                    for(rowEndOfSymbol = width - 1; rowEndOfSymbol > x && !image.get(rowEndOfSymbol, y); --rowEndOfSymbol) {
                        ;
                    }

                    if(rowEndOfSymbol <= x) {
                        throw NotFoundException.getNotFoundInstance();
                    } else {
                        ++rowEndOfSymbol;
                        if((rowEndOfSymbol - x) % moduleSize != 0) {
                            throw NotFoundException.getNotFoundInstance();
                        } else {
                            int dimension = 1 + (rowEndOfSymbol - x) / moduleSize;
                            int backOffAmount = moduleSize == 1?1:moduleSize >> 1;
                            x -= backOffAmount;
                            y -= backOffAmount;
                            if(x + (dimension - 1) * moduleSize < width && y + (dimension - 1) * moduleSize < height) {
                                BitMatrix bits = new BitMatrix(dimension);

                                for(int i = 0; i < dimension; ++i) {
                                    int iOffset = y + i * moduleSize;

                                    for(int j = 0; j < dimension; ++j) {
                                        if(image.get(x + j * moduleSize, iOffset)) {
                                            bits.set(j, i);
                                        }
                                    }
                                }

                                return bits;
                            } else {
                                throw NotFoundException.getNotFoundInstance();
                            }
                        }
                    }
                }
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        }
    }
}
