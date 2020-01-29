//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common.reedsolomon;

import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.GF256Poly;
import java.util.Vector;

public final class ReedSolomonEncoder {
    private final GF256 field;
    private final Vector cachedGenerators;

    public ReedSolomonEncoder(GF256 field) {
        if(!GF256.QR_CODE_FIELD.equals(field)) {
            throw new IllegalArgumentException("Only QR Code is supported at this time");
        } else {
            this.field = field;
            this.cachedGenerators = new Vector();
            this.cachedGenerators.addElement(new GF256Poly(field, new int[]{1}));
        }
    }

    private GF256Poly buildGenerator(int degree) {
        if(degree >= this.cachedGenerators.size()) {
            GF256Poly lastGenerator = (GF256Poly)this.cachedGenerators.elementAt(this.cachedGenerators.size() - 1);

            for(int d = this.cachedGenerators.size(); d <= degree; ++d) {
                GF256Poly nextGenerator = lastGenerator.multiply(new GF256Poly(this.field, new int[]{1, this.field.exp(d - 1)}));
                this.cachedGenerators.addElement(nextGenerator);
                lastGenerator = nextGenerator;
            }
        }

        return (GF256Poly)this.cachedGenerators.elementAt(degree);
    }

    public void encode(int[] toEncode, int ecBytes) {
        if(ecBytes == 0) {
            throw new IllegalArgumentException("No error correction bytes");
        } else {
            int dataBytes = toEncode.length - ecBytes;
            if(dataBytes <= 0) {
                throw new IllegalArgumentException("No data bytes provided");
            } else {
                GF256Poly generator = this.buildGenerator(ecBytes);
                int[] infoCoefficients = new int[dataBytes];
                System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
                GF256Poly info = new GF256Poly(this.field, infoCoefficients);
                info = info.multiplyByMonomial(ecBytes, 1);
                GF256Poly remainder = info.divide(generator)[1];
                int[] coefficients = remainder.getCoefficients();
                int numZeroCoefficients = ecBytes - coefficients.length;

                for(int i = 0; i < numZeroCoefficients; ++i) {
                    toEncode[dataBytes + i] = 0;
                }

                System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
            }
        }
    }
}
