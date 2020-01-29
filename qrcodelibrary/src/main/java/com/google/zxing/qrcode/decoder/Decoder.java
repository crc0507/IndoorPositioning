//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

import java.util.Hashtable;

public final class Decoder {
    private final ReedSolomonDecoder rsDecoder;

    public Decoder() {
        this.rsDecoder = new ReedSolomonDecoder(GF256.QR_CODE_FIELD);
    }

    public DecoderResult decode(boolean[][] image) throws ChecksumException, FormatException, NotFoundException {
        return this.decode((boolean[][])image, (Hashtable)null);
    }

    public DecoderResult decode(boolean[][] image, Hashtable hints) throws ChecksumException, FormatException, NotFoundException {
        int dimension = image.length;
        BitMatrix bits = new BitMatrix(dimension);

        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                if(image[i][j]) {
                    bits.set(j, i);
                }
            }
        }

        return this.decode(bits, hints);
    }

    public DecoderResult decode(BitMatrix bits) throws ChecksumException, FormatException, NotFoundException {
        return this.decode((BitMatrix)bits, (Hashtable)null);
    }

    public DecoderResult decode(BitMatrix bits, Hashtable hints) throws FormatException, ChecksumException {
        BitMatrixParser parser = new BitMatrixParser(bits);
        Version version = parser.readVersion();
        ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();
        byte[] codewords = parser.readCodewords();
        DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version, ecLevel);
        int totalBytes = 0;

        for(int resultBytes = 0; resultBytes < dataBlocks.length; ++resultBytes) {
            totalBytes += dataBlocks[resultBytes].getNumDataCodewords();
        }

        byte[] var16 = new byte[totalBytes];
        int resultOffset = 0;

        for(int j = 0; j < dataBlocks.length; ++j) {
            DataBlock dataBlock = dataBlocks[j];
            byte[] codewordBytes = dataBlock.getCodewords();
            int numDataCodewords = dataBlock.getNumDataCodewords();
            this.correctErrors(codewordBytes, numDataCodewords);

            for(int i = 0; i < numDataCodewords; ++i) {
                var16[resultOffset++] = codewordBytes[i];
            }
        }

        return DecodedBitStreamParser.decode(var16, version, ecLevel, hints);
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
