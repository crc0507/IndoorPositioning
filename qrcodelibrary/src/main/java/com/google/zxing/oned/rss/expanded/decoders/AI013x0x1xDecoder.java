//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI013x0x1xDecoder extends AI01weightDecoder {
    private static final int headerSize = 8;
    private static final int weightSize = 20;
    private static final int dateSize = 16;
    private final String dateCode;
    private final String firstAIdigits;

    AI013x0x1xDecoder(BitArray information, String firstAIdigits, String dateCode) {
        super(information);
        this.dateCode = dateCode;
        this.firstAIdigits = firstAIdigits;
    }

    public String parseInformation() throws NotFoundException {
        if(this.information.size != 84) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            StringBuffer buf = new StringBuffer();
            this.encodeCompressedGtin(buf, 8);
            this.encodeCompressedWeight(buf, 48, 20);
            this.encodeCompressedDate(buf, 68);
            return buf.toString();
        }
    }

    private void encodeCompressedDate(StringBuffer buf, int currentPos) {
        int numericDate = this.generalDecoder.extractNumericValueFromBitArray(currentPos, 16);
        if(numericDate != '阀') {
            buf.append('(');
            buf.append(this.dateCode);
            buf.append(')');
            int day = numericDate % 32;
            numericDate /= 32;
            int month = numericDate % 12 + 1;
            numericDate /= 12;
            if(numericDate / 10 == 0) {
                buf.append('0');
            }

            buf.append(numericDate);
            if(month / 10 == 0) {
                buf.append('0');
            }

            buf.append(month);
            if(day / 10 == 0) {
                buf.append('0');
            }

            buf.append(day);
        }
    }

    protected void addWeightCode(StringBuffer buf, int weight) {
        int lastAI = weight / 100000;
        buf.append('(');
        buf.append(this.firstAIdigits);
        buf.append(lastAI);
        buf.append(')');
    }

    protected int checkWeight(int weight) {
        return weight % 100000;
    }
}
