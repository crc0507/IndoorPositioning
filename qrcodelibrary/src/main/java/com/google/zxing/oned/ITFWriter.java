//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public final class ITFWriter extends UPCEANWriter {
    public ITFWriter() {
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if(format != BarcodeFormat.ITF) {
            throw new IllegalArgumentException("Can only encode ITF, but got " + format);
        } else {
            return super.encode(contents, format, width, height, hints);
        }
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if(length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        } else {
            byte[] result = new byte[9 + 9 * length];
            int[] start = new int[]{1, 1, 1, 1};
            int pos = appendPattern(result, 0, start, 1);

            for(int end = 0; end < length; end += 2) {
                int one = Character.digit(contents.charAt(end), 10);
                int two = Character.digit(contents.charAt(end + 1), 10);
                int[] encoding = new int[18];

                for(int j = 0; j < 5; ++j) {
                    encoding[j << 1] = ITFReader.PATTERNS[one][j];
                    encoding[(j << 1) + 1] = ITFReader.PATTERNS[two][j];
                }

                pos += appendPattern(result, pos, encoding, 1);
            }

            int[] var11 = new int[]{3, 1, 1};
            int var10000 = pos + appendPattern(result, pos, var11, 1);
            return result;
        }
    }
}
