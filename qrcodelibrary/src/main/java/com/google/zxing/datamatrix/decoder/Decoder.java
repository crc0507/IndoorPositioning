//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.datamatrix.decoder.BitMatrixParser;
import com.google.zxing.datamatrix.decoder.DataBlock;
import com.google.zxing.datamatrix.decoder.DecodedBitStreamParser;
import com.google.zxing.datamatrix.decoder.Version;

public final class Decoder {
    private final ReedSolomonDecoder rsDecoder;

    public Decoder() {
        this.rsDecoder = new ReedSolomonDecoder(GF256.DATA_MATRIX_FIELD);
    }

    public DecoderResult decode(boolean[][] image) throws FormatException, ChecksumException {
        int dimension = image.length;
        BitMatrix bits = new BitMatrix(dimension);

        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                if(image[i][j]) {
                    bits.set(j, i);
                }
            }
        }

        return this.decode(bits);
    }

    public DecoderResult decode(BitMatrix bits) throws FormatException, ChecksumException {
        BitMatrixParser parser = new BitMatrixParser(bits);
        Version version = parser.readVersion(bits);
        byte[] codewords = parser.readCodewords();
        DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version);
        int totalBytes = 0;

        for(int resultBytes = 0; resultBytes < dataBlocks.length; ++resultBytes) {
            totalBytes += dataBlocks[resultBytes].getNumDataCodewords();
        }

        byte[] var14 = new byte[totalBytes];
        int resultOffset = 0;

        for(int j = 0; j < dataBlocks.length; ++j) {
            DataBlock dataBlock = dataBlocks[j];
            byte[] codewordBytes = dataBlock.getCodewords();
            int numDataCodewords = dataBlock.getNumDataCodewords();
            this.correctErrors(codewordBytes, numDataCodewords);

            for(int i = 0; i < numDataCodewords; ++i) {
                var14[resultOffset++] = codewordBytes[i];
            }
        }

        return DecodedBitStreamParser.decode(var14);
    }

    private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException {
        int numCodewords = codewordBytes.length;
        int[] codewordsInts = new int[numCodewords];

        int numECCodewords;
        for(numECCodewords = 0; numECCodewords < numCodewords; ++numECCodewords) {
            codewordsInts[numECCodewords] = codewordBytes[numECCodewords] & 255;
        }

        numECCodewords = codewordBytes.length - numDataCodewords;

        try {
            this.rsDecoder.decode(codewordsInts, numECCodewords);
        } catch (ReedSolomonException var7) {
            throw ChecksumException.getChecksumInstance();
        }

        for(int i = 0; i < numDataCodewords; ++i) {
            codewordBytes[i] = (byte)codewordsInts[i];
        }

    }
}
