//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common.reedsolomon;

import com.google.zxing.common.reedsolomon.GF256Poly;

public final class GF256 {
    public static final GF256 QR_CODE_FIELD = new GF256(285);
    public static final GF256 DATA_MATRIX_FIELD = new GF256(301);
    private final int[] expTable = new int[256];
    private final int[] logTable = new int[256];
    private final GF256Poly zero;
    private final GF256Poly one;

    private GF256(int primitive) {
        int x = 1;

        int i;
        for(i = 0; i < 256; ++i) {
            this.expTable[i] = x;
            x <<= 1;
            if(x >= 256) {
                x ^= primitive;
            }
        }

        for(i = 0; i < 255; this.logTable[this.expTable[i]] = i++) {
            ;
        }

        this.zero = new GF256Poly(this, new int[]{0});
        this.one = new GF256Poly(this, new int[]{1});
    }

    GF256Poly getZero() {
        return this.zero;
    }

    GF256Poly getOne() {
        return this.one;
    }

    GF256Poly buildMonomial(int degree, int coefficient) {
        if(degree < 0) {
            throw new IllegalArgumentException();
        } else if(coefficient == 0) {
            return this.zero;
        } else {
            int[] coefficients = new int[degree + 1];
            coefficients[0] = coefficient;
            return new GF256Poly(this, coefficients);
        }
    }

    static int addOrSubtract(int a, int b) {
        return a ^ b;
    }

    int exp(int a) {
        return this.expTable[a];
    }

    int log(int a) {
        if(a == 0) {
            throw new IllegalArgumentException();
        } else {
            return this.logTable[a];
        }
    }

    int inverse(int a) {
        if(a == 0) {
            throw new ArithmeticException();
        } else {
            return this.expTable[255 - this.logTable[a]];
        }
    }

    int multiply(int a, int b) {
        if(a != 0 && b != 0) {
            int logSum = this.logTable[a] + this.logTable[b];
            return this.expTable[(logSum & 255) + (logSum >>> 8)];
        } else {
            return 0;
        }
    }
}
