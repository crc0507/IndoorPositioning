//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public final class Code39Writer extends UPCEANWriter {
    public Code39Writer() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(format != BarcodeFormat.CODE_39) {
            throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
        } else {
            return super.encode(contents, format, width, height, hints);
        }
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if(length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        } else {
            int[] widths = new int[9];
            int codeWidth = 25 + length;

            int pos;
            for(int result = 0; result < length; ++result) {
                pos = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(result));
                toIntArray(Code39Reader.CHARACTER_ENCODINGS[pos], widths);

                for(int narrowWhite = 0; narrowWhite < widths.length; ++narrowWhite) {
                    codeWidth += widths[narrowWhite];
                }
            }

            byte[] var10 = new byte[codeWidth];
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
            pos = appendPattern(var10, 0, widths, 1);
            int[] var11 = new int[]{1};
            pos += appendPattern(var10, pos, var11, 0);

            for(int i = length - 1; i >= 0; --i) {
                int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i));
                toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
                pos += appendPattern(var10, pos, widths, 1);
                pos += appendPattern(var10, pos, var11, 0);
            }

            toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
            int var10000 = pos + appendPattern(var10, pos, widths, 1);
            return var10;
        }
    }

    private static void toIntArray(int a, int[] toReturn) {
        for(int i = 0; i < 9; ++i) {
            int temp = a & 1 << i;
            toReturn[i] = temp == 0?1:2;
        }

    }
}
