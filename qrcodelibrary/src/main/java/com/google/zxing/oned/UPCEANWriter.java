//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public abstract class UPCEANWriter implements Writer {
    public UPCEANWriter() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return this.encode(contents, format, width, height, (Hashtable)null);
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(contents != null && contents.length() != 0) {
            if(width >= 0 && height >= 0) {
                byte[] code = this.encode(contents);
                return renderResult(code, width, height);
            } else {
                throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
            }
        } else {
            throw new IllegalArgumentException("Found empty contents");
        }
    }

    private static BitMatrix renderResult(byte[] code, int width, int height) {
        int inputWidth = code.length;
        int fullWidth = inputWidth + (UPCEANReader.START_END_PATTERN.length << 1);
        int outputWidth = Math.max(width, fullWidth);
        int outputHeight = Math.max(1, height);
        int multiple = outputWidth / fullWidth;
        int leftPadding = (outputWidth - inputWidth * multiple) / 2;
        BitMatrix output = new BitMatrix(outputWidth, outputHeight);
        int inputX = 0;

        for(int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
            if(code[inputX] == 1) {
                output.setRegion(outputX, 0, multiple, outputHeight);
            }

            ++inputX;
        }

        return output;
    }

    protected static int appendPattern(byte[] target, int pos, int[] pattern, int startColor) {
        if(startColor != 0 && startColor != 1) {
            throw new IllegalArgumentException("startColor must be either 0 or 1, but got: " + startColor);
        } else {
            byte color = (byte)startColor;
            int numAdded = 0;

            for(int i = 0; i < pattern.length; ++i) {
                for(int j = 0; j < pattern[i]; ++j) {
                    target[pos] = color;
                    ++pos;
                    ++numAdded;
                }

                color = (byte)(color ^ 1);
            }

            return numAdded;
        }
    }

    public abstract byte[] encode(String var1);
}
