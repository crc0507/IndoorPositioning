//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public final class EAN8Writer extends UPCEANWriter {
    private static final int codeWidth = 67;

    public EAN8Writer() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(format != BarcodeFormat.EAN_8) {
            throw new IllegalArgumentException("Can only encode EAN_8, but got " + format);
        } else {
            return super.encode(contents, format, width, height, hints);
        }
    }

    public byte[] encode(String contents) {
        if(contents.length() != 8) {
            throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents.length());
        } else {
            byte[] result = new byte[67];
            byte pos = 0;
            int var6 = pos + appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);

            int i;
            int digit;
            for(i = 0; i <= 3; ++i) {
                digit = Integer.parseInt(contents.substring(i, i + 1));
                var6 += appendPattern(result, var6, UPCEANReader.L_PATTERNS[digit], 0);
            }

            var6 += appendPattern(result, var6, UPCEANReader.MIDDLE_PATTERN, 0);

            for(i = 4; i <= 7; ++i) {
                digit = Integer.parseInt(contents.substring(i, i + 1));
                var6 += appendPattern(result, var6, UPCEANReader.L_PATTERNS[digit], 1);
            }

            var6 += appendPattern(result, var6, UPCEANReader.START_END_PATTERN, 1);
            return result;
        }
    }
}
