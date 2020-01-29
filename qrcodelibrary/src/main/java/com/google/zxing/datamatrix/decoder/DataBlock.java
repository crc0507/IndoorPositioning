//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix.decoder;

import com.google.zxing.datamatrix.decoder.Version;
import com.google.zxing.datamatrix.decoder.Version.ECB;
import com.google.zxing.datamatrix.decoder.Version.ECBlocks;

final class DataBlock {
    private final int numDataCodewords;
    private final byte[] codewords;

    private DataBlock(int numDataCodewords, byte[] codewords) {
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }

    static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version) {
        ECBlocks ecBlocks = version.getECBlocks();
        int totalBlocks = 0;
        ECB[] ecBlockArray = ecBlocks.getECBlocks();

        for(int result = 0; result < ecBlockArray.length; ++result) {
            totalBlocks += ecBlockArray[result].getCount();
        }

        DataBlock[] var17 = new DataBlock[totalBlocks];
        int numResultBlocks = 0;

        int longerBlocksTotalCodewords;
        int shorterBlocksNumDataCodewords;
        int rawCodewordsOffset;
        int specialVersion;
        for(longerBlocksTotalCodewords = 0; longerBlocksTotalCodewords < ecBlockArray.length; ++longerBlocksTotalCodewords) {
            ECB longerBlocksNumDataCodewords = ecBlockArray[longerBlocksTotalCodewords];

            for(shorterBlocksNumDataCodewords = 0; shorterBlocksNumDataCodewords < longerBlocksNumDataCodewords.getCount(); ++shorterBlocksNumDataCodewords) {
                rawCodewordsOffset = longerBlocksNumDataCodewords.getDataCodewords();
                specialVersion = ecBlocks.getECCodewords() + rawCodewordsOffset;
                var17[numResultBlocks++] = new DataBlock(rawCodewordsOffset, new byte[specialVersion]);
            }
        }

        longerBlocksTotalCodewords = var17[0].codewords.length;
        int var18 = longerBlocksTotalCodewords - ecBlocks.getECCodewords();
        shorterBlocksNumDataCodewords = var18 - 1;
        rawCodewordsOffset = 0;

        int numLongerBlocks;
        for(specialVersion = 0; specialVersion < shorterBlocksNumDataCodewords; ++specialVersion) {
            for(numLongerBlocks = 0; numLongerBlocks < numResultBlocks; ++numLongerBlocks) {
                var17[numLongerBlocks].codewords[specialVersion] = rawCodewords[rawCodewordsOffset++];
            }
        }

        boolean var19 = version.getVersionNumber() == 24;
        numLongerBlocks = var19?8:numResultBlocks;

        int max;
        for(max = 0; max < numLongerBlocks; ++max) {
            var17[max].codewords[var18 - 1] = rawCodewords[rawCodewordsOffset++];
        }

        max = var17[0].codewords.length;

        for(int i = var18; i < max; ++i) {
            for(int j = 0; j < numResultBlocks; ++j) {
                int iOffset = var19 && j > 7?i - 1:i;
                var17[j].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
            }
        }

        if(rawCodewordsOffset != rawCodewords.length) {
            throw new IllegalArgumentException();
        } else {
            return var17;
        }
    }

    int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    byte[] getCodewords() {
        return this.codewords;
    }
}
