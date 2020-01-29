//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.decoder;

import com.google.zxing.qrcode.decoder.Version.ECB;
import com.google.zxing.qrcode.decoder.Version.ECBlocks;

final class DataBlock {
    private final int numDataCodewords;
    private final byte[] codewords;

    private DataBlock(int numDataCodewords, byte[] codewords) {
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }

    static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version, ErrorCorrectionLevel ecLevel) {
        if(rawCodewords.length != version.getTotalCodewords()) {
            throw new IllegalArgumentException();
        } else {
            ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
            int totalBlocks = 0;
            ECB[] ecBlockArray = ecBlocks.getECBlocks();

            for(int result = 0; result < ecBlockArray.length; ++result) {
                totalBlocks += ecBlockArray[result].getCount();
            }

            DataBlock[] var16 = new DataBlock[totalBlocks];
            int numResultBlocks = 0;

            int shorterBlocksTotalCodewords;
            int shorterBlocksNumDataCodewords;
            int rawCodewordsOffset;
            int max;
            for(shorterBlocksTotalCodewords = 0; shorterBlocksTotalCodewords < ecBlockArray.length; ++shorterBlocksTotalCodewords) {
                ECB longerBlocksStartAt = ecBlockArray[shorterBlocksTotalCodewords];

                for(shorterBlocksNumDataCodewords = 0; shorterBlocksNumDataCodewords < longerBlocksStartAt.getCount(); ++shorterBlocksNumDataCodewords) {
                    rawCodewordsOffset = longerBlocksStartAt.getDataCodewords();
                    max = ecBlocks.getECCodewordsPerBlock() + rawCodewordsOffset;
                    var16[numResultBlocks++] = new DataBlock(rawCodewordsOffset, new byte[max]);
                }
            }

            shorterBlocksTotalCodewords = var16[0].codewords.length;

            int var17;
            for(var17 = var16.length - 1; var17 >= 0; --var17) {
                shorterBlocksNumDataCodewords = var16[var17].codewords.length;
                if(shorterBlocksNumDataCodewords == shorterBlocksTotalCodewords) {
                    break;
                }
            }

            ++var17;
            shorterBlocksNumDataCodewords = shorterBlocksTotalCodewords - ecBlocks.getECCodewordsPerBlock();
            rawCodewordsOffset = 0;

            int i;
            for(max = 0; max < shorterBlocksNumDataCodewords; ++max) {
                for(i = 0; i < numResultBlocks; ++i) {
                    var16[i].codewords[max] = rawCodewords[rawCodewordsOffset++];
                }
            }

            for(max = var17; max < numResultBlocks; ++max) {
                var16[max].codewords[shorterBlocksNumDataCodewords] = rawCodewords[rawCodewordsOffset++];
            }

            max = var16[0].codewords.length;

            for(i = shorterBlocksNumDataCodewords; i < max; ++i) {
                for(int j = 0; j < numResultBlocks; ++j) {
                    int iOffset = j < var17?i:i + 1;
                    var16[j].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
                }
            }

            return var16;
        }
    }

    int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    byte[] getCodewords() {
        return this.codewords;
    }
}
