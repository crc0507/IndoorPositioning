//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public final class EAN13Writer extends UPCEANWriter {
    private static final int codeWidth = 95;

    public EAN13Writer() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(format != BarcodeFormat.EAN_13) {
            throw new IllegalArgumentException("Can only encode EAN_13, but got " + format);
        } else {
            return super.encode(contents, format, width, height, hints);
        }
    }

    public byte[] encode(String contents) {
        if(contents.length() != 13) {
            throw new IllegalArgumentException("Requested contents should be 13 digits long, but got " + contents.length());
        } else {
            int firstDigit = Integer.parseInt(contents.substring(0, 1));
            int parities = EAN13Reader.FIRST_DIGIT_ENCODINGS[firstDigit];
            byte[] result = new byte[95];
            byte pos = 0;
            int var8 = pos + appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);

            int i;
            int digit;
            for(i = 1; i <= 6; ++i) {
                digit = Integer.parseInt(contents.substring(i, i + 1));
                if((parities >> 6 - i & 1) == 1) {
                    digit += 10;
                }

                var8 += appendPattern(result, var8, UPCEANReader.L_AND_G_PATTERNS[digit], 0);
            }

            var8 += appendPattern(result, var8, UPCEANReader.MIDDLE_PATTERN, 0);

            for(i = 7; i <= 12; ++i) {
                digit = Integer.parseInt(contents.substring(i, i + 1));
                var8 += appendPattern(result, var8, UPCEANReader.L_PATTERNS[digit], 1);
            }

            var8 += appendPattern(result, var8, UPCEANReader.START_END_PATTERN, 1);
            return result;
        }
    }
}
