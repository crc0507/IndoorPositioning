//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

final class CurrentParsingState {
    int position = 0;
    private int encoding = 1;
    private static final int NUMERIC = 1;
    private static final int ALPHA = 2;
    private static final int ISO_IEC_646 = 4;

    CurrentParsingState() {
    }

    boolean isAlpha() {
        return this.encoding == 2;
    }

    boolean isNumeric() {
        return this.encoding == 1;
    }

    boolean isIsoIec646() {
        return this.encoding == 4;
    }

    void setNumeric() {
        this.encoding = 1;
    }

    void setAlpha() {
        this.encoding = 2;
    }

    void setIsoIec646() {
        this.encoding = 4;
    }
}
