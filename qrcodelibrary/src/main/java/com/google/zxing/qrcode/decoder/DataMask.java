//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.decoder;

import com.google.zxing.common.BitMatrix;

abstract class DataMask {
    private static final DataMask[] DATA_MASKS = new DataMask[]{new DataMask.DataMask000(), new DataMask.DataMask001(), new DataMask.DataMask010(), new DataMask.DataMask011(), new DataMask.DataMask100(), new DataMask.DataMask101(), new DataMask.DataMask110(), new DataMask.DataMask111()};

    private DataMask() {
    }

    final void unmaskBitMatrix(BitMatrix bits, int dimension) {
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                if(this.isMasked(i, j)) {
                    bits.flip(j, i);
                }
            }
        }

    }

    abstract boolean isMasked(int var1, int var2);

    static DataMask forReference(int reference) {
        if(reference >= 0 && reference <= 7) {
            return DATA_MASKS[reference];
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static class DataMask111 extends DataMask {
        private DataMask111() {
            super();
        }

        boolean isMasked(int i, int j) {
            return ((i + j & 1) + i * j % 3 & 1) == 0;
        }
    }

    private static class DataMask110 extends DataMask {
        private DataMask110() {
            super();
        }

        boolean isMasked(int i, int j) {
            int temp = i * j;
            return ((temp & 1) + temp % 3 & 1) == 0;
        }
    }

    private static class DataMask101 extends DataMask {
        private DataMask101() {
            super();
        }

        boolean isMasked(int i, int j) {
            int temp = i * j;
            return (temp & 1) + temp % 3 == 0;
        }
    }

    private static class DataMask100 extends DataMask {
        private DataMask100() {
            super();
        }

        boolean isMasked(int i, int j) {
            return ((i >>> 1) + j / 3 & 1) == 0;
        }
    }

    private static class DataMask011 extends DataMask {
        private DataMask011() {
            super();
        }

        boolean isMasked(int i, int j) {
            return (i + j) % 3 == 0;
        }
    }

    private static class DataMask010 extends DataMask {
        private DataMask010() {
            super();
        }

        boolean isMasked(int i, int j) {
            return j % 3 == 0;
        }
    }

    private static class DataMask001 extends DataMask {
        private DataMask001() {
            super();
        }

        boolean isMasked(int i, int j) {
            return (i & 1) == 0;
        }
    }

    private static class DataMask000 extends DataMask {
        private DataMask000() {
            super();
        }

        boolean isMasked(int i, int j) {
            return (i + j & 1) == 0;
        }
    }
}
