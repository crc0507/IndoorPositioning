//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.common.BitArray;

public final class BitMatrix {
    public final int width;
    public final int height;
    public final int rowSize;
    public final int[] bits;

    public BitMatrix(int dimension) {
        this(dimension, dimension);
    }

    public BitMatrix(int width, int height) {
        if(width >= 1 && height >= 1) {
            this.width = width;
            this.height = height;
            this.rowSize = width + 31 >> 5;
            this.bits = new int[this.rowSize * height];
        } else {
            throw new IllegalArgumentException("Both dimensions must be greater than 0");
        }
    }

    public boolean get(int x, int y) {
        int offset = y * this.rowSize + (x >> 5);
        return (this.bits[offset] >>> (x & 31) & 1) != 0;
    }

    public void set(int x, int y) {
        int offset = y * this.rowSize + (x >> 5);
        this.bits[offset] |= 1 << (x & 31);
    }

    public void flip(int x, int y) {
        int offset = y * this.rowSize + (x >> 5);
        this.bits[offset] ^= 1 << (x & 31);
    }

    public void clear() {
        int max = this.bits.length;

        for(int i = 0; i < max; ++i) {
            this.bits[i] = 0;
        }

    }

    public void setRegion(int left, int top, int width, int height) {
        if(top >= 0 && left >= 0) {
            if(height >= 1 && width >= 1) {
                int right = left + width;
                int bottom = top + height;
                if(bottom <= this.height && right <= this.width) {
                    for(int y = top; y < bottom; ++y) {
                        int offset = y * this.rowSize;

                        for(int x = left; x < right; ++x) {
                            this.bits[offset + (x >> 5)] |= 1 << (x & 31);
                        }
                    }

                } else {
                    throw new IllegalArgumentException("The region must fit inside the matrix");
                }
            } else {
                throw new IllegalArgumentException("Height and width must be at least 1");
            }
        } else {
            throw new IllegalArgumentException("Left and top must be nonnegative");
        }
    }

    public BitArray getRow(int y, BitArray row) {
        if(row == null || row.getSize() < this.width) {
            row = new BitArray(this.width);
        }

        int offset = y * this.rowSize;

        for(int x = 0; x < this.rowSize; ++x) {
            row.setBulk(x << 5, this.bits[offset + x]);
        }

        return row;
    }

    public int[] getTopLeftOnBit() {
        int bitsOffset;
        for(bitsOffset = 0; bitsOffset < this.bits.length && this.bits[bitsOffset] == 0; ++bitsOffset) {
            ;
        }

        if(bitsOffset == this.bits.length) {
            return null;
        } else {
            int y = bitsOffset / this.rowSize;
            int x = bitsOffset % this.rowSize << 5;
            int theBits = this.bits[bitsOffset];

            int bit;
            for(bit = 0; theBits << 31 - bit == 0; ++bit) {
                ;
            }

            x += bit;
            return new int[]{x, y};
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if(!(o instanceof BitMatrix)) {
            return false;
        } else {
            BitMatrix other = (BitMatrix)o;
            if(this.width == other.width && this.height == other.height && this.rowSize == other.rowSize && this.bits.length == other.bits.length) {
                for(int i = 0; i < this.bits.length; ++i) {
                    if(this.bits[i] != other.bits[i]) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        int hash = this.width;
        hash = 31 * hash + this.width;
        hash = 31 * hash + this.height;
        hash = 31 * hash + this.rowSize;

        for(int i = 0; i < this.bits.length; ++i) {
            hash = 31 * hash + this.bits[i];
        }

        return hash;
    }

    public String toString() {
        StringBuffer result = new StringBuffer(this.height * (this.width + 1));

        for(int y = 0; y < this.height; ++y) {
            for(int x = 0; x < this.width; ++x) {
                result.append(this.get(x, y)?"X ":"  ");
            }

            result.append('\n');
        }

        return result.toString();
    }
}
