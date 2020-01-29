//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI01392xDecoder extends AI01decoder {
    private static final int headerSize = 8;
    private static final int lastDigitSize = 2;

    AI01392xDecoder(BitArray information) {
        super(information);
    }

    public String parseInformation() throws NotFoundException {
        if(this.information.size < 48) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            StringBuffer buf = new StringBuffer();
            this.encodeCompressedGtin(buf, 8);
            int lastAIdigit = this.generalDecoder.extractNumericValueFromBitArray(48, 2);
            buf.append("(392");
            buf.append(lastAIdigit);
            buf.append(')');
            DecodedInformation decodedInformation = this.generalDecoder.decodeGeneralPurposeField(50, (String)null);
            buf.append(decodedInformation.getNewString());
            return buf.toString();
        }
    }
}
