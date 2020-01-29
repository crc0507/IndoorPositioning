//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix;

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
import com.google.zxing.datamatrix.decoder.Decoder;
import com.google.zxing.datamatrix.detector.Detector;
import java.util.Hashtable;

public final class DataMatrixReader implements Reader {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    public DataMatrixReader() {
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return this.decode(image, (Hashtable)null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if(hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            BitMatrix result1 = extractPureBits(image.getBlackMatrix());
            decoderResult = this.decoder.decode(result1);
            points = NO_POINTS;
        } else {
            DetectorResult result = (new Detector(image.getBlackMatrix())).detect();
            decoderResult = this.decoder.decode(result.getBits());
            points = result.getPoints();
        }

        Result result2 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.DATA_MATRIX);
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

    private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
        int height = image.getHeight();
        int width = image.getWidth();
        int minDimension = Math.min(height, width);
        int[] leftTopBlack = image.getTopLeftOnBit();
        if(leftTopBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int x = leftTopBlack[0];

            int y;
            for(y = leftTopBlack[1]; x < minDimension && y < minDimension && image.get(x, y); ++x) {
                ;
            }

            if(x == minDimension) {
                throw NotFoundException.getNotFoundInstance();
            } else {
                int moduleSize = x - leftTopBlack[0];

                int rowEndOfSymbol;
                for(rowEndOfSymbol = width - 1; rowEndOfSymbol >= 0 && !image.get(rowEndOfSymbol, y); --rowEndOfSymbol) {
                    ;
                }

                if(rowEndOfSymbol < 0) {
                    throw NotFoundException.getNotFoundInstance();
                } else {
                    ++rowEndOfSymbol;
                    if((rowEndOfSymbol - x) % moduleSize != 0) {
                        throw NotFoundException.getNotFoundInstance();
                    } else {
                        int dimension = 2 + (rowEndOfSymbol - x) / moduleSize;
                        y += moduleSize;
                        x -= moduleSize >> 1;
                        y -= moduleSize >> 1;
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
        }
    }
}
