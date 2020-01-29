//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

abstract class AI013x0xDecoder extends AI01weightDecoder {
    private static final int headerSize = 5;
    private static final int weightSize = 15;

    AI013x0xDecoder(BitArray information) {
        super(information);
    }

    public String parseInformation() throws NotFoundException {
        if(this.information.size != 60) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            StringBuffer buf = new StringBuffer();
            this.encodeCompressedGtin(buf, 5);
            this.encodeCompressedWeight(buf, 45, 15);
            return buf.toString();
        }
    }
}
