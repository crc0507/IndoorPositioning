//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public final class Code128Writer extends UPCEANWriter {
    public Code128Writer() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(format != BarcodeFormat.CODE_128) {
            throw new IllegalArgumentException("Can only encode CODE_128, but got " + format);
        } else {
            return super.encode(contents, format, width, height, hints);
        }
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if(length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        } else {
            int codeWidth = 35;

            int check;
            for(int result = 0; result < length; ++result) {
                int[] pos = Code128Reader.CODE_PATTERNS[contents.charAt(result) - 32];

                for(check = 0; check < pos.length; ++check) {
                    codeWidth += pos[check];
                }
            }

            byte[] var8 = new byte[codeWidth];
            int var9 = appendPattern(var8, 0, Code128Reader.CODE_PATTERNS[104], 1);
            check = 104;

            for(int i = 0; i < length; ++i) {
                check += (contents.charAt(i) - 32) * (i + 1);
                var9 += appendPattern(var8, var9, Code128Reader.CODE_PATTERNS[contents.charAt(i) - 32], 1);
            }

            check %= 103;
            var9 += appendPattern(var8, var9, Code128Reader.CODE_PATTERNS[check], 1);
            var9 += appendPattern(var8, var9, Code128Reader.CODE_PATTERNS[106], 1);
            return var8;
        }
    }
}
