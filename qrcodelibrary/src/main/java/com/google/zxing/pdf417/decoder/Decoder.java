//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;

public final class Decoder {
    private static final int MAX_ERRORS = 3;
    private static final int MAX_EC_CODEWORDS = 512;

    public Decoder() {
    }

    public DecoderResult decode(boolean[][] image) throws FormatException {
        int dimension = image.length;
        BitMatrix bits = new BitMatrix(dimension);

        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                if(image[j][i]) {
                    bits.set(j, i);
                }
            }
        }

        return this.decode(bits);
    }

    public DecoderResult decode(BitMatrix bits) throws FormatException {
        BitMatrixParser parser = new BitMatrixParser(bits);
        int[] codewords = parser.readCodewords();
        if(codewords != null && codewords.length != 0) {
            int ecLevel = parser.getECLevel();
            int numECCodewords = 1 << ecLevel + 1;
            int[] erasures = parser.getErasures();
            correctErrors(codewords, erasures, numECCodewords);
            verifyCodewordCount(codewords, numECCodewords);
            return DecodedBitStreamParser.decode(codewords);
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static void verifyCodewordCount(int[] codewords, int numECCodewords) throws FormatException {
        if(codewords.length < 4) {
            throw FormatException.getFormatInstance();
        } else {
            int numberOfCodewords = codewords[0];
            if(numberOfCodewords > codewords.length) {
                throw FormatException.getFormatInstance();
            } else {
                if(numberOfCodewords == 0) {
                    if(numECCodewords >= codewords.length) {
                        throw FormatException.getFormatInstance();
                    }

                    codewords[0] = codewords.length - numECCodewords;
                }

            }
        }
    }

    private static int correctErrors(int[] codewords, int[] erasures, int numECCodewords) throws FormatException {
        if((erasures == null || erasures.length <= numECCodewords / 2 + 3) && numECCodewords >= 0 && numECCodewords <= 512) {
            byte result = 0;
            if(erasures != null) {
                int numErasures = erasures.length;
                if(result > 0) {
                    numErasures -= result;
                }

                if(numErasures > 3) {
                    throw FormatException.getFormatInstance();
                }
            }

            return result;
        } else {
            throw FormatException.getFormatInstance();
        }
    }
}
