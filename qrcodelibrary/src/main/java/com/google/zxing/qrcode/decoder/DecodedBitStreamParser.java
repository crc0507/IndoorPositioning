//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.qrcode.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

final class DecodedBitStreamParser {
    private static final char[] ALPHANUMERIC_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '$', '%', '*', '+', '-', '.', '/', ':'};

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(byte[] bytes, Version version, ErrorCorrectionLevel ecLevel, Hashtable hints) throws FormatException {
        BitSource bits = new BitSource(bytes);
        StringBuffer result = new StringBuffer(50);
        CharacterSetECI currentCharacterSetECI = null;
        boolean fc1InEffect = false;
        Vector byteSegments = new Vector(1);

        Mode mode;
        do {
            if(bits.available() < 4) {
                mode = Mode.TERMINATOR;
            } else {
                try {
                    mode = Mode.forBits(bits.readBits(4));
                } catch (IllegalArgumentException var11) {
                    throw FormatException.getFormatInstance();
                }
            }

            if(!mode.equals(Mode.TERMINATOR)) {
                if(!mode.equals(Mode.FNC1_FIRST_POSITION) && !mode.equals(Mode.FNC1_SECOND_POSITION)) {
                    if(mode.equals(Mode.STRUCTURED_APPEND)) {
                        bits.readBits(16);
                    } else {
                        int count;
                        if(mode.equals(Mode.ECI)) {
                            count = parseECIValue(bits);
                            currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(count);
                            if(currentCharacterSetECI == null) {
                                throw FormatException.getFormatInstance();
                            }
                        } else {
                            count = bits.readBits(mode.getCharacterCountBits(version));
                            if(mode.equals(Mode.NUMERIC)) {
                                decodeNumericSegment(bits, result, count);
                            } else if(mode.equals(Mode.ALPHANUMERIC)) {
                                decodeAlphanumericSegment(bits, result, count, fc1InEffect);
                            } else if(mode.equals(Mode.BYTE)) {
                                decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments, hints);
                            } else {
                                if(!mode.equals(Mode.KANJI)) {
                                    throw FormatException.getFormatInstance();
                                }

                                decodeKanjiSegment(bits, result, count);
                            }
                        }
                    }
                } else {
                    fc1InEffect = true;
                }
            }
        } while(!mode.equals(Mode.TERMINATOR));

        return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty()?null:byteSegments, ecLevel);
    }

    private static void decodeKanjiSegment(BitSource bits, StringBuffer result, int count) throws FormatException {
        byte[] buffer = new byte[2 * count];

        for(int offset = 0; count > 0; --count) {
            int uee = bits.readBits(13);
            int assembledTwoBytes = uee / 192 << 8 | uee % 192;
            if(assembledTwoBytes < 7936) {
                assembledTwoBytes += '腀';
            } else {
                assembledTwoBytes += '셀';
            }

            buffer[offset] = (byte)(assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte)assembledTwoBytes;
            offset += 2;
        }

        try {
            result.append(new String(buffer, "SJIS"));
        } catch (UnsupportedEncodingException var7) {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeByteSegment(BitSource bits, StringBuffer result, int count, CharacterSetECI currentCharacterSetECI, Vector byteSegments, Hashtable hints) throws FormatException {
        byte[] readBytes = new byte[count];
        if(count << 3 > bits.available()) {
            throw FormatException.getFormatInstance();
        } else {
            for(int encoding = 0; encoding < count; ++encoding) {
                readBytes[encoding] = (byte)bits.readBits(8);
            }

            String var10;
            if(currentCharacterSetECI == null) {
                var10 = StringUtils.guessEncoding(readBytes, hints);
            } else {
                var10 = currentCharacterSetECI.getEncodingName();
            }

            try {
                result.append(new String(readBytes, var10));
            } catch (UnsupportedEncodingException var9) {
                throw FormatException.getFormatInstance();
            }

            byteSegments.addElement(readBytes);
        }
    }

    private static char toAlphaNumericChar(int value) throws FormatException {
        if(value >= ALPHANUMERIC_CHARS.length) {
            throw FormatException.getFormatInstance();
        } else {
            return ALPHANUMERIC_CHARS[value];
        }
    }

    private static void decodeAlphanumericSegment(BitSource bits, StringBuffer result, int count, boolean fc1InEffect) throws FormatException {
        int start;
        int i;
        for(start = result.length(); count > 1; count -= 2) {
            i = bits.readBits(11);
            result.append(toAlphaNumericChar(i / 45));
            result.append(toAlphaNumericChar(i % 45));
        }

        if(count == 1) {
            result.append(toAlphaNumericChar(bits.readBits(6)));
        }

        if(fc1InEffect) {
            for(i = start; i < result.length(); ++i) {
                if(result.charAt(i) == 37) {
                    if(i < result.length() - 1 && result.charAt(i + 1) == 37) {
                        result.deleteCharAt(i + 1);
                    } else {
                        result.setCharAt(i, '\u001d');
                    }
                }
            }
        }

    }

    private static void decodeNumericSegment(BitSource bits, StringBuffer result, int count) throws FormatException {
        int digitBits;
        while(count >= 3) {
            digitBits = bits.readBits(10);
            if(digitBits >= 1000) {
                throw FormatException.getFormatInstance();
            }

            result.append(toAlphaNumericChar(digitBits / 100));
            result.append(toAlphaNumericChar(digitBits / 10 % 10));
            result.append(toAlphaNumericChar(digitBits % 10));
            count -= 3;
        }

        if(count == 2) {
            digitBits = bits.readBits(7);
            if(digitBits >= 100) {
                throw FormatException.getFormatInstance();
            }

            result.append(toAlphaNumericChar(digitBits / 10));
            result.append(toAlphaNumericChar(digitBits % 10));
        } else if(count == 1) {
            digitBits = bits.readBits(4);
            if(digitBits >= 10) {
                throw FormatException.getFormatInstance();
            }

            result.append(toAlphaNumericChar(digitBits));
        }

    }

    private static int parseECIValue(BitSource bits) {
        int firstByte = bits.readBits(8);
        if((firstByte & 128) == 0) {
            return firstByte & 127;
        } else {
            int secondThirdBytes;
            if((firstByte & 192) == 128) {
                secondThirdBytes = bits.readBits(8);
                return (firstByte & 63) << 8 | secondThirdBytes;
            } else if((firstByte & 224) == 192) {
                secondThirdBytes = bits.readBits(16);
                return (firstByte & 31) << 16 | secondThirdBytes;
            } else {
                throw new IllegalArgumentException("Bad ECI bits starting with byte " + firstByte);
            }
        }
    }
}
