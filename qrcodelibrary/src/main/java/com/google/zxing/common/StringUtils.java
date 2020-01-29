//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.DecodeHintType;
import java.util.Hashtable;

public final class StringUtils {
    private static final String PLATFORM_DEFAULT_ENCODING = System.getProperty("file.encoding");
    public static final String SHIFT_JIS = "SJIS";
    private static final String EUC_JP = "EUC_JP";
    private static final String UTF8 = "UTF-8";
    private static final String ISO88591 = "ISO8859_1";
    private static final boolean ASSUME_SHIFT_JIS;

    private StringUtils() {
    }

    public static String guessEncoding(byte[] bytes, Hashtable hints) {
        if(hints != null) {
            String length = (String)hints.get(DecodeHintType.CHARACTER_SET);
            if(length != null) {
                return length;
            }
        }

        if(bytes.length > 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
            return "UTF-8";
        } else {
            int var15 = bytes.length;
            boolean canBeISO88591 = true;
            boolean canBeShiftJIS = true;
            boolean canBeUTF8 = true;
            int utf8BytesLeft = 0;
            int maybeDoubleByteCount = 0;
            int maybeSingleByteKatakanaCount = 0;
            boolean sawLatin1Supplement = false;
            boolean sawUTF8Start = false;
            boolean lastWasPossibleDoubleByteStart = false;

            for(int i = 0; i < var15 && (canBeISO88591 || canBeShiftJIS || canBeUTF8); ++i) {
                int value = bytes[i] & 255;
                int nextValue;
                if(value >= 128 && value <= 191) {
                    if(utf8BytesLeft > 0) {
                        --utf8BytesLeft;
                    }
                } else {
                    if(utf8BytesLeft > 0) {
                        canBeUTF8 = false;
                    }

                    if(value >= 192 && value <= 253) {
                        sawUTF8Start = true;

                        for(nextValue = value; (nextValue & 64) != 0; nextValue <<= 1) {
                            ++utf8BytesLeft;
                        }
                    }
                }

                if((value == 194 || value == 195) && i < var15 - 1) {
                    nextValue = bytes[i + 1] & 255;
                    if(nextValue <= 191 && (value == 194 && nextValue >= 160 || value == 195 && nextValue >= 128)) {
                        sawLatin1Supplement = true;
                    }
                }

                if(value >= 127 && value <= 159) {
                    canBeISO88591 = false;
                }

                if(value >= 161 && value <= 223 && !lastWasPossibleDoubleByteStart) {
                    ++maybeSingleByteKatakanaCount;
                }

                if(!lastWasPossibleDoubleByteStart && (value >= 240 && value <= 255 || value == 128 || value == 160)) {
                    canBeShiftJIS = false;
                }

                if(value >= 129 && value <= 159 || value >= 224 && value <= 239) {
                    if(lastWasPossibleDoubleByteStart) {
                        lastWasPossibleDoubleByteStart = false;
                    } else {
                        lastWasPossibleDoubleByteStart = true;
                        if(i >= bytes.length - 1) {
                            canBeShiftJIS = false;
                        } else {
                            nextValue = bytes[i + 1] & 255;
                            if(nextValue >= 64 && nextValue <= 252) {
                                ++maybeDoubleByteCount;
                            } else {
                                canBeShiftJIS = false;
                            }
                        }
                    }
                } else {
                    lastWasPossibleDoubleByteStart = false;
                }
            }

            if(utf8BytesLeft > 0) {
                canBeUTF8 = false;
            }

            return canBeShiftJIS && ASSUME_SHIFT_JIS?"SJIS":(canBeUTF8 && sawUTF8Start?"UTF-8":(canBeShiftJIS && (maybeDoubleByteCount >= 3 || 20 * maybeSingleByteKatakanaCount > var15)?"SJIS":(!sawLatin1Supplement && canBeISO88591?"ISO8859_1":PLATFORM_DEFAULT_ENCODING)));
        }
    }

    static {
        ASSUME_SHIFT_JIS = "SJIS".equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING) || "EUC_JP".equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING);
    }
}
