//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import java.util.Hashtable;

public final class QRCodeWriter implements Writer {
    private static final int QUIET_ZONE_SIZE = 4;

    public QRCodeWriter() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return this.encode(contents, format, width, height, (Hashtable)null);
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(contents != null && contents.length() != 0) {
            if(format != BarcodeFormat.QR_CODE) {
                throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
            } else if(width >= 0 && height >= 0) {
                ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
                if(hints != null) {
                    ErrorCorrectionLevel code = (ErrorCorrectionLevel)hints.get(EncodeHintType.ERROR_CORRECTION);
                    if(code != null) {
                        errorCorrectionLevel = code;
                    }
                }

                QRCode code1 = new QRCode();
                Encoder.encode(contents, errorCorrectionLevel, hints, code1);
                return renderResult(code1, width, height);
            } else {
                throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
            }
        } else {
            throw new IllegalArgumentException("Found empty contents");
        }
    }

    private static BitMatrix renderResult(QRCode code, int width, int height) {
        ByteMatrix input = code.getMatrix();
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + 8;
        int qrHeight = inputHeight + 8;
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);
        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        int leftPadding = (outputWidth - inputWidth * multiple) / 2;
        int topPadding = (outputHeight - inputHeight * multiple) / 2;
        BitMatrix output = new BitMatrix(outputWidth, outputHeight);
        int inputY = 0;

        for(int outputY = topPadding; inputY < inputHeight; outputY += multiple) {
            int inputX = 0;

            for(int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
                if(input.get(inputX, inputY) == 1) {
                    output.setRegion(outputX, outputY, multiple, multiple);
                }

                ++inputX;
            }

            ++inputY;
        }

        return output;
    }
}
