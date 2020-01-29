//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

abstract class DecodedObject {
    protected final int newPosition;

    DecodedObject(int newPosition) {
        this.newPosition = newPosition;
    }

    int getNewPosition() {
        return this.newPosition;
    }
}
