//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common.reedsolomon;

import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.GF256Poly;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

public final class ReedSolomonDecoder {
    private final GF256 field;

    public ReedSolomonDecoder(GF256 field) {
        this.field = field;
    }

    public void decode(int[] received, int twoS) throws ReedSolomonException {
        GF256Poly poly = new GF256Poly(this.field, received);
        int[] syndromeCoefficients = new int[twoS];
        boolean dataMatrix = this.field.equals(GF256.DATA_MATRIX_FIELD);
        boolean noError = true;

        for(int syndrome = 0; syndrome < twoS; ++syndrome) {
            int sigmaOmega = poly.evaluateAt(this.field.exp(dataMatrix?syndrome + 1:syndrome));
            syndromeCoefficients[syndromeCoefficients.length - 1 - syndrome] = sigmaOmega;
            if(sigmaOmega != 0) {
                noError = false;
            }
        }

        if(!noError) {
            GF256Poly var15 = new GF256Poly(this.field, syndromeCoefficients);
            GF256Poly[] var16 = this.runEuclideanAlgorithm(this.field.buildMonomial(twoS, 1), var15, twoS);
            GF256Poly sigma = var16[0];
            GF256Poly omega = var16[1];
            int[] errorLocations = this.findErrorLocations(sigma);
            int[] errorMagnitudes = this.findErrorMagnitudes(omega, errorLocations, dataMatrix);

            for(int i = 0; i < errorLocations.length; ++i) {
                int position = received.length - 1 - this.field.log(errorLocations[i]);
                if(position < 0) {
                    throw new ReedSolomonException("Bad error location");
                }

                received[position] = GF256.addOrSubtract(received[position], errorMagnitudes[i]);
            }

        }
    }

    private GF256Poly[] runEuclideanAlgorithm(GF256Poly a, GF256Poly b, int R) throws ReedSolomonException {
        GF256Poly rLast;
        if(a.getDegree() < b.getDegree()) {
            rLast = a;
            a = b;
            b = rLast;
        }

        rLast = a;
        GF256Poly r = b;
        GF256Poly sLast = this.field.getOne();
        GF256Poly s = this.field.getZero();
        GF256Poly tLast = this.field.getZero();

        GF256Poly t;
        GF256Poly sigma;
        GF256Poly omega;
        for(t = this.field.getOne(); r.getDegree() >= R / 2; t = omega.multiply(t).addOrSubtract(sigma)) {
            GF256Poly sigmaTildeAtZero = rLast;
            GF256Poly inverse = sLast;
            sigma = tLast;
            rLast = r;
            sLast = s;
            tLast = t;
            if(r.isZero()) {
                throw new ReedSolomonException("r_{i-1} was zero");
            }

            r = sigmaTildeAtZero;
            omega = this.field.getZero();
            int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());

            int degreeDiff;
            int scale;
            for(int dltInverse = this.field.inverse(denominatorLeadingTerm); r.getDegree() >= rLast.getDegree() && !r.isZero(); r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale))) {
                degreeDiff = r.getDegree() - rLast.getDegree();
                scale = this.field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
                omega = omega.addOrSubtract(this.field.buildMonomial(degreeDiff, scale));
            }

            s = omega.multiply(s).addOrSubtract(inverse);
        }

        int sigmaTildeAtZero1 = t.getCoefficient(0);
        if(sigmaTildeAtZero1 == 0) {
            throw new ReedSolomonException("sigmaTilde(0) was zero");
        } else {
            int inverse1 = this.field.inverse(sigmaTildeAtZero1);
            sigma = t.multiply(inverse1);
            omega = r.multiply(inverse1);
            return new GF256Poly[]{sigma, omega};
        }
    }

    private int[] findErrorLocations(GF256Poly errorLocator) throws ReedSolomonException {
        int numErrors = errorLocator.getDegree();
        if(numErrors == 1) {
            return new int[]{errorLocator.getCoefficient(1)};
        } else {
            int[] result = new int[numErrors];
            int e = 0;

            for(int i = 1; i < 256 && e < numErrors; ++i) {
                if(errorLocator.evaluateAt(i) == 0) {
                    result[e] = this.field.inverse(i);
                    ++e;
                }
            }

            if(e != numErrors) {
                throw new ReedSolomonException("Error locator degree does not match number of roots");
            } else {
                return result;
            }
        }
    }

    private int[] findErrorMagnitudes(GF256Poly errorEvaluator, int[] errorLocations, boolean dataMatrix) {
        int s = errorLocations.length;
        int[] result = new int[s];

        for(int i = 0; i < s; ++i) {
            int xiInverse = this.field.inverse(errorLocations[i]);
            int denominator = 1;

            for(int j = 0; j < s; ++j) {
                if(i != j) {
                    int term = this.field.multiply(errorLocations[j], xiInverse);
                    int termPlus1 = (term & 1) == 0?term | 1:term & -2;
                    denominator = this.field.multiply(denominator, termPlus1);
                }
            }

            result[i] = this.field.multiply(errorEvaluator.evaluateAt(xiInverse), this.field.inverse(denominator));
            if(dataMatrix) {
                result[i] = this.field.multiply(result[i], xiInverse);
            }
        }

        return result;
    }
}
