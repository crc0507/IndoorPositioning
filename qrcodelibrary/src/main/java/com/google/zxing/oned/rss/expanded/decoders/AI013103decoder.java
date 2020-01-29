//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

final class AI013103decoder extends AI013x0xDecoder {
    AI013103decoder(BitArray information) {
        super(information);
    }

    protected void addWeightCode(StringBuffer buf, int weight) {
        buf.append("(3103)");
    }

    protected int checkWeight(int weight) {
        return weight;
    }
}
