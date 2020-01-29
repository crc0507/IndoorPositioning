//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.encoder;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.ECI;
import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.decoder.Version.ECBlocks;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

public final class Encoder {
    private static final int[] ALPHANUMERIC_TABLE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1};
    static final String DEFAULT_BYTE_MODE_ENCODING = "UTF-8";

    private Encoder() {
    }

    private static int calculateMaskPenalty(ByteMatrix matrix) {
        byte penalty = 0;
        int penalty1 = penalty + MaskUtil.applyMaskPenaltyRule1(matrix);
        penalty1 += MaskUtil.applyMaskPenaltyRule2(matrix);
        penalty1 += MaskUtil.applyMaskPenaltyRule3(matrix);
        penalty1 += MaskUtil.applyMaskPenaltyRule4(matrix);
        return penalty1;
    }

    public static void encode(String content, ErrorCorrectionLevel ecLevel, QRCode qrCode) throws WriterException {
        encode(content, ecLevel, (Hashtable)null, qrCode);
    }

    public static void encode(String content, ErrorCorrectionLevel ecLevel, Hashtable hints, QRCode qrCode) throws WriterException {
        String encoding = hints == null?null:(String)hints.get(EncodeHintType.CHARACTER_SET);
        if(encoding == null) {
            encoding = "UTF-8";
        }

        Mode mode = chooseMode(content, encoding);
        BitArray dataBits = new BitArray();
        appendBytes(content, mode, dataBits, encoding);
        int numInputBytes = dataBits.getSizeInBytes();
        initQRCode(numInputBytes, ecLevel, mode, qrCode);
        BitArray headerAndDataBits = new BitArray();
        if(mode == Mode.BYTE && !"UTF-8".equals(encoding)) {
            CharacterSetECI numLetters = CharacterSetECI.getCharacterSetECIByName(encoding);
            if(numLetters != null) {
                appendECI(numLetters, headerAndDataBits);
            }
        }

        appendModeInfo(mode, headerAndDataBits);
        int numLetters1 = mode.equals(Mode.BYTE)?dataBits.getSizeInBytes():content.length();
        appendLengthInfo(numLetters1, qrCode.getVersion(), mode, headerAndDataBits);
        headerAndDataBits.appendBitArray(dataBits);
        terminateBits(qrCode.getNumDataBytes(), headerAndDataBits);
        BitArray finalBits = new BitArray();
        interleaveWithECBytes(headerAndDataBits, qrCode.getNumTotalBytes(), qrCode.getNumDataBytes(), qrCode.getNumRSBlocks(), finalBits);
        ByteMatrix matrix = new ByteMatrix(qrCode.getMatrixWidth(), qrCode.getMatrixWidth());
        qrCode.setMaskPattern(chooseMaskPattern(finalBits, qrCode.getECLevel(), qrCode.getVersion(), matrix));
        MatrixUtil.buildMatrix(finalBits, qrCode.getECLevel(), qrCode.getVersion(), qrCode.getMaskPattern(), matrix);
        qrCode.setMatrix(matrix);
        if(!qrCode.isValid()) {
            throw new WriterException("Invalid QR code: " + qrCode.toString());
        }
    }

    static int getAlphanumericCode(int code) {
        return code < ALPHANUMERIC_TABLE.length?ALPHANUMERIC_TABLE[code]:-1;
    }

    public static Mode chooseMode(String content) {
        return chooseMode(content, (String)null);
    }

    public static Mode chooseMode(String content, String encoding) {
        if("Shift_JIS".equals(encoding)) {
            return isOnlyDoubleByteKanji(content)?Mode.KANJI:Mode.BYTE;
        } else {
            boolean hasNumeric = false;
            boolean hasAlphanumeric = false;

            for(int i = 0; i < content.length(); ++i) {
                char c = content.charAt(i);
                if(c >= 48 && c <= 57) {
                    hasNumeric = true;
                } else {
                    if(getAlphanumericCode(c) == -1) {
                        return Mode.BYTE;
                    }

                    hasAlphanumeric = true;
                }
            }

            if(hasAlphanumeric) {
                return Mode.ALPHANUMERIC;
            } else if(hasNumeric) {
                return Mode.NUMERIC;
            } else {
                return Mode.BYTE;
            }
        }
    }

    private static boolean isOnlyDoubleByteKanji(String content) {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException var5) {
            return false;
        }

        int length = bytes.length;
        if(length % 2 != 0) {
            return false;
        } else {
            for(int i = 0; i < length; i += 2) {
                int byte1 = bytes[i] & 255;
                if((byte1 < 129 || byte1 > 159) && (byte1 < 224 || byte1 > 235)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static int chooseMaskPattern(BitArray bits, ErrorCorrectionLevel ecLevel, int version, ByteMatrix matrix) throws WriterException {
        int minPenalty = 2147483647;
        int bestMaskPattern = -1;

        for(int maskPattern = 0; maskPattern < 8; ++maskPattern) {
            MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
            int penalty = calculateMaskPenalty(matrix);
            if(penalty < minPenalty) {
                minPenalty = penalty;
                bestMaskPattern = maskPattern;
            }
        }

        return bestMaskPattern;
    }

    private static void initQRCode(int numInputBytes, ErrorCorrectionLevel ecLevel, Mode mode, QRCode qrCode) throws WriterException {
        qrCode.setECLevel(ecLevel);
        qrCode.setMode(mode);

        for(int versionNum = 1; versionNum <= 40; ++versionNum) {
            Version version = Version.getVersionForNumber(versionNum);
            int numBytes = version.getTotalCodewords();
            ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
            int numEcBytes = ecBlocks.getTotalECCodewords();
            int numRSBlocks = ecBlocks.getNumBlocks();
            int numDataBytes = numBytes - numEcBytes;
            if(numDataBytes >= numInputBytes + 3) {
                qrCode.setVersion(versionNum);
                qrCode.setNumTotalBytes(numBytes);
                qrCode.setNumDataBytes(numDataBytes);
                qrCode.setNumRSBlocks(numRSBlocks);
                qrCode.setNumECBytes(numEcBytes);
                qrCode.setMatrixWidth(version.getDimensionForVersion());
                return;
            }
        }

        throw new WriterException("Cannot find proper rs block info (input data too big?)");
    }

    static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
        int capacity = numDataBytes << 3;
        if(bits.getSize() > capacity) {
            throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity);
        } else {
            int numBitsInLastByte;
            for(numBitsInLastByte = 0; numBitsInLastByte < 4 && bits.getSize() < capacity; ++numBitsInLastByte) {
                bits.appendBit(false);
            }

            numBitsInLastByte = bits.getSize() & 7;
            int numPaddingBytes;
            if(numBitsInLastByte > 0) {
                for(numPaddingBytes = numBitsInLastByte; numPaddingBytes < 8; ++numPaddingBytes) {
                    bits.appendBit(false);
                }
            }

            numPaddingBytes = numDataBytes - bits.getSizeInBytes();

            for(int i = 0; i < numPaddingBytes; ++i) {
                bits.appendBits((i & 1) == 0?236:17, 8);
            }

            if(bits.getSize() != capacity) {
                throw new WriterException("Bits size does not equal capacity");
            }
        }
    }

    static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks, int blockID, int[] numDataBytesInBlock, int[] numECBytesInBlock) throws WriterException {
        if(blockID >= numRSBlocks) {
            throw new WriterException("Block ID too large");
        } else {
            int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
            int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
            int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
            int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
            int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
            int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
            int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
            int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
            if(numEcBytesInGroup1 != numEcBytesInGroup2) {
                throw new WriterException("EC bytes mismatch");
            } else if(numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
                throw new WriterException("RS blocks mismatch");
            } else if(numTotalBytes != (numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1 + (numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2) {
                throw new WriterException("Total bytes mismatch");
            } else {
                if(blockID < numRsBlocksInGroup1) {
                    numDataBytesInBlock[0] = numDataBytesInGroup1;
                    numECBytesInBlock[0] = numEcBytesInGroup1;
                } else {
                    numDataBytesInBlock[0] = numDataBytesInGroup2;
                    numECBytesInBlock[0] = numEcBytesInGroup2;
                }

            }
        }
    }

    static void interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes, int numRSBlocks, BitArray result) throws WriterException {
        if(bits.getSizeInBytes() != numDataBytes) {
            throw new WriterException("Number of bits and data bytes does not match");
        } else {
            int dataBytesOffset = 0;
            int maxNumDataBytes = 0;
            int maxNumEcBytes = 0;
            Vector blocks = new Vector(numRSBlocks);

            int i;
            for(i = 0; i < numRSBlocks; ++i) {
                int[] j = new int[1];
                int[] ecBytes = new int[1];
                getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, j, ecBytes);
                int size = j[0];
                byte[] dataBytes = new byte[size];
                bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
                byte[] ecBytes1 = generateECBytes(dataBytes, ecBytes[0]);
                blocks.addElement(new BlockPair(dataBytes, ecBytes1));
                maxNumDataBytes = Math.max(maxNumDataBytes, size);
                maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes1.length);
                dataBytesOffset += j[0];
            }

            if(numDataBytes != dataBytesOffset) {
                throw new WriterException("Data bytes does not match offset");
            } else {
                int var15;
                byte[] var16;
                for(i = 0; i < maxNumDataBytes; ++i) {
                    for(var15 = 0; var15 < blocks.size(); ++var15) {
                        var16 = ((BlockPair)blocks.elementAt(var15)).getDataBytes();
                        if(i < var16.length) {
                            result.appendBits(var16[i], 8);
                        }
                    }
                }

                for(i = 0; i < maxNumEcBytes; ++i) {
                    for(var15 = 0; var15 < blocks.size(); ++var15) {
                        var16 = ((BlockPair)blocks.elementAt(var15)).getErrorCorrectionBytes();
                        if(i < var16.length) {
                            result.appendBits(var16[i], 8);
                        }
                    }
                }

                if(numTotalBytes != result.getSizeInBytes()) {
                    throw new WriterException("Interleaving error: " + numTotalBytes + " and " + result.getSizeInBytes() + " differ.");
                }
            }
        }
    }

    static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
        int numDataBytes = dataBytes.length;
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];

        for(int ecBytes = 0; ecBytes < numDataBytes; ++ecBytes) {
            toEncode[ecBytes] = dataBytes[ecBytes] & 255;
        }

        (new ReedSolomonEncoder(GF256.QR_CODE_FIELD)).encode(toEncode, numEcBytesInBlock);
        byte[] var6 = new byte[numEcBytesInBlock];

        for(int i = 0; i < numEcBytesInBlock; ++i) {
            var6[i] = (byte)toEncode[numDataBytes + i];
        }

        return var6;
    }

    static void appendModeInfo(Mode mode, BitArray bits) {
        bits.appendBits(mode.getBits(), 4);
    }

    static void appendLengthInfo(int numLetters, int version, Mode mode, BitArray bits) throws WriterException {
        int numBits = mode.getCharacterCountBits(Version.getVersionForNumber(version));
        if(numLetters > (1 << numBits) - 1) {
            throw new WriterException(numLetters + "is bigger than" + ((1 << numBits) - 1));
        } else {
            bits.appendBits(numLetters, numBits);
        }
    }

    static void appendBytes(String content, Mode mode, BitArray bits, String encoding) throws WriterException {
        if(mode.equals(Mode.NUMERIC)) {
            appendNumericBytes(content, bits);
        } else if(mode.equals(Mode.ALPHANUMERIC)) {
            appendAlphanumericBytes(content, bits);
        } else if(mode.equals(Mode.BYTE)) {
            append8BitBytes(content, bits, encoding);
        } else {
            if(!mode.equals(Mode.KANJI)) {
                throw new WriterException("Invalid mode: " + mode);
            }

            appendKanjiBytes(content, bits);
        }

    }

    static void appendNumericBytes(String content, BitArray bits) {
        int length = content.length();
        int i = 0;

        while(i < length) {
            int num1 = content.charAt(i) - 48;
            int num2;
            if(i + 2 < length) {
                num2 = content.charAt(i + 1) - 48;
                int num3 = content.charAt(i + 2) - 48;
                bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
                i += 3;
            } else if(i + 1 < length) {
                num2 = content.charAt(i + 1) - 48;
                bits.appendBits(num1 * 10 + num2, 7);
                i += 2;
            } else {
                bits.appendBits(num1, 4);
                ++i;
            }
        }

    }

    static void appendAlphanumericBytes(String content, BitArray bits) throws WriterException {
        int length = content.length();
        int i = 0;

        while(i < length) {
            int code1 = getAlphanumericCode(content.charAt(i));
            if(code1 == -1) {
                throw new WriterException();
            }

            if(i + 1 < length) {
                int code2 = getAlphanumericCode(content.charAt(i + 1));
                if(code2 == -1) {
                    throw new WriterException();
                }

                bits.appendBits(code1 * 45 + code2, 11);
                i += 2;
            } else {
                bits.appendBits(code1, 6);
                ++i;
            }
        }

    }

    static void append8BitBytes(String content, BitArray bits, String encoding) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes(encoding);
        } catch (UnsupportedEncodingException var5) {
            throw new WriterException(var5.toString());
        }

        for(int i = 0; i < bytes.length; ++i) {
            bits.appendBits(bytes[i], 8);
        }

    }

    static void appendKanjiBytes(String content, BitArray bits) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException var10) {
            throw new WriterException(var10.toString());
        }

        int length = bytes.length;

        for(int i = 0; i < length; i += 2) {
            int byte1 = bytes[i] & 255;
            int byte2 = bytes[i + 1] & 255;
            int code = byte1 << 8 | byte2;
            int subtracted = -1;
            if(code >= '腀' && code <= '\u9ffc') {
                subtracted = code - '腀';
            } else if(code >= '\ue040' && code <= '\uebbf') {
                subtracted = code - '셀';
            }

            if(subtracted == -1) {
                throw new WriterException("Invalid byte sequence");
            }

            int encoded = (subtracted >> 8) * 192 + (subtracted & 255);
            bits.appendBits(encoded, 13);
        }

    }

    private static void appendECI(ECI eci, BitArray bits) {
        bits.appendBits(Mode.ECI.getBits(), 4);
        bits.appendBits(eci.getValue(), 8);
    }
}
