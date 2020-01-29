//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix.decoder;

import com.google.zxing.FormatException;

public final class Version {
    private static final Version[] VERSIONS = buildVersions();
    private final int versionNumber;
    private final int symbolSizeRows;
    private final int symbolSizeColumns;
    private final int dataRegionSizeRows;
    private final int dataRegionSizeColumns;
    private final Version.ECBlocks ecBlocks;
    private final int totalCodewords;

    private Version(int versionNumber, int symbolSizeRows, int symbolSizeColumns, int dataRegionSizeRows, int dataRegionSizeColumns, Version.ECBlocks ecBlocks) {
        this.versionNumber = versionNumber;
        this.symbolSizeRows = symbolSizeRows;
        this.symbolSizeColumns = symbolSizeColumns;
        this.dataRegionSizeRows = dataRegionSizeRows;
        this.dataRegionSizeColumns = dataRegionSizeColumns;
        this.ecBlocks = ecBlocks;
        int total = 0;
        int ecCodewords = ecBlocks.getECCodewords();
        Version.ECB[] ecbArray = ecBlocks.getECBlocks();

        for(int i = 0; i < ecbArray.length; ++i) {
            Version.ECB ecBlock = ecbArray[i];
            total += ecBlock.getCount() * (ecBlock.getDataCodewords() + ecCodewords);
        }

        this.totalCodewords = total;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public int getSymbolSizeRows() {
        return this.symbolSizeRows;
    }

    public int getSymbolSizeColumns() {
        return this.symbolSizeColumns;
    }

    public int getDataRegionSizeRows() {
        return this.dataRegionSizeRows;
    }

    public int getDataRegionSizeColumns() {
        return this.dataRegionSizeColumns;
    }

    public int getTotalCodewords() {
        return this.totalCodewords;
    }

    Version.ECBlocks getECBlocks() {
        return this.ecBlocks;
    }

    public static Version getVersionForDimensions(int numRows, int numColumns) throws FormatException {
        if((numRows & 1) == 0 && (numColumns & 1) == 0) {
            int numVersions = VERSIONS.length;

            for(int i = 0; i < numVersions; ++i) {
                Version version = VERSIONS[i];
                if(version.symbolSizeRows == numRows && version.symbolSizeColumns == numColumns) {
                    return version;
                }
            }

            throw FormatException.getFormatInstance();
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    public String toString() {
        return String.valueOf(this.versionNumber);
    }

    private static Version[] buildVersions() {
        return new Version[]{new Version(1, 10, 10, 8, 8, new Version.ECBlocks(5, new Version.ECB(1, 3))), new Version(2, 12, 12, 10, 10, new Version.ECBlocks(7, new Version.ECB(1, 5))), new Version(3, 14, 14, 12, 12, new Version.ECBlocks(10, new Version.ECB(1, 8))), new Version(4, 16, 16, 14, 14, new Version.ECBlocks(12, new Version.ECB(1, 12))), new Version(5, 18, 18, 16, 16, new Version.ECBlocks(14, new Version.ECB(1, 18))), new Version(6, 20, 20, 18, 18, new Version.ECBlocks(18, new Version.ECB(1, 22))), new Version(7, 22, 22, 20, 20, new Version.ECBlocks(20, new Version.ECB(1, 30))), new Version(8, 24, 24, 22, 22, new Version.ECBlocks(24, new Version.ECB(1, 36))), new Version(9, 26, 26, 24, 24, new Version.ECBlocks(28, new Version.ECB(1, 44))), new Version(10, 32, 32, 14, 14, new Version.ECBlocks(36, new Version.ECB(1, 62))), new Version(11, 36, 36, 16, 16, new Version.ECBlocks(42, new Version.ECB(1, 86))), new Version(12, 40, 40, 18, 18, new Version.ECBlocks(48, new Version.ECB(1, 114))), new Version(13, 44, 44, 20, 20, new Version.ECBlocks(56, new Version.ECB(1, 144))), new Version(14, 48, 48, 22, 22, new Version.ECBlocks(68, new Version.ECB(1, 174))), new Version(15, 52, 52, 24, 24, new Version.ECBlocks(42, new Version.ECB(2, 102))), new Version(16, 64, 64, 14, 14, new Version.ECBlocks(56, new Version.ECB(2, 140))), new Version(17, 72, 72, 16, 16, new Version.ECBlocks(36, new Version.ECB(4, 92))), new Version(18, 80, 80, 18, 18, new Version.ECBlocks(48, new Version.ECB(4, 114))), new Version(19, 88, 88, 20, 20, new Version.ECBlocks(56, new Version.ECB(4, 144))), new Version(20, 96, 96, 22, 22, new Version.ECBlocks(68, new Version.ECB(4, 174))), new Version(21, 104, 104, 24, 24, new Version.ECBlocks(56, new Version.ECB(6, 136))), new Version(22, 120, 120, 18, 18, new Version.ECBlocks(68, new Version.ECB(6, 175))), new Version(23, 132, 132, 20, 20, new Version.ECBlocks(62, new Version.ECB(8, 163))), new Version(24, 144, 144, 22, 22, new Version.ECBlocks(62, new Version.ECB(8, 156), new Version.ECB(2, 155))), new Version(25, 8, 18, 6, 16, new Version.ECBlocks(7, new Version.ECB(1, 5))), new Version(26, 8, 32, 6, 14, new Version.ECBlocks(11, new Version.ECB(1, 10))), new Version(27, 12, 26, 10, 24, new Version.ECBlocks(14, new Version.ECB(1, 16))), new Version(28, 12, 36, 10, 16, new Version.ECBlocks(18, new Version.ECB(1, 22))), new Version(29, 16, 36, 10, 16, new Version.ECBlocks(24, new Version.ECB(1, 32))), new Version(30, 16, 48, 14, 22, new Version.ECBlocks(28, new Version.ECB(1, 49)))};
    }

    static final class ECB {
        private final int count;
        private final int dataCodewords;

        private ECB(int count, int dataCodewords) {
            this.count = count;
            this.dataCodewords = dataCodewords;
        }

        int getCount() {
            return this.count;
        }

        int getDataCodewords() {
            return this.dataCodewords;
        }
    }

    static final class ECBlocks {
        private final int ecCodewords;
        private final Version.ECB[] ecBlocks;

        private ECBlocks(int ecCodewords, Version.ECB ecBlocks) {
            this.ecCodewords = ecCodewords;
            this.ecBlocks = new Version.ECB[]{ecBlocks};
        }

        private ECBlocks(int ecCodewords, Version.ECB ecBlocks1, Version.ECB ecBlocks2) {
            this.ecCodewords = ecCodewords;
            this.ecBlocks = new Version.ECB[]{ecBlocks1, ecBlocks2};
        }

        int getECCodewords() {
            return this.ecCodewords;
        }

        Version.ECB[] getECBlocks() {
            return this.ecBlocks;
        }
    }
}
