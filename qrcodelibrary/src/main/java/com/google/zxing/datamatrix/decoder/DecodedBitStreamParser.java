//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.datamatrix.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

final class DecodedBitStreamParser {
    private static final char[] C40_BASIC_SET_CHARS = new char[]{'*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] C40_SHIFT2_SET_CHARS = new char[]{'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_'};
    private static final char[] TEXT_BASIC_SET_CHARS = new char[]{'*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] TEXT_SHIFT3_SET_CHARS = new char[]{'\'', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '{', '|', '}', '~', '\u007f'};
    private static final int PAD_ENCODE = 0;
    private static final int ASCII_ENCODE = 1;
    private static final int C40_ENCODE = 2;
    private static final int TEXT_ENCODE = 3;
    private static final int ANSIX12_ENCODE = 4;
    private static final int EDIFACT_ENCODE = 5;
    private static final int BASE256_ENCODE = 6;

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(byte[] bytes) throws FormatException {
        BitSource bits = new BitSource(bytes);
        StringBuffer result = new StringBuffer(100);
        StringBuffer resultTrailer = new StringBuffer(0);
        Vector byteSegments = new Vector(1);
        int mode = 1;

        do {
            if(mode == 1) {
                mode = decodeAsciiSegment(bits, result, resultTrailer);
            } else {
                switch(mode) {
                case 2:
                    decodeC40Segment(bits, result);
                    break;
                case 3:
                    decodeTextSegment(bits, result);
                    break;
                case 4:
                    decodeAnsiX12Segment(bits, result);
                    break;
                case 5:
                    decodeEdifactSegment(bits, result);
                    break;
                case 6:
                    decodeBase256Segment(bits, result, byteSegments);
                    break;
                default:
                    throw FormatException.getFormatInstance();
                }

                mode = 1;
            }
        } while(mode != 0 && bits.available() > 0);

        if(resultTrailer.length() > 0) {
            result.append(resultTrailer.toString());
        }

        return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty()?null:byteSegments, (ErrorCorrectionLevel)null);
    }

    private static int decodeAsciiSegment(BitSource bits, StringBuffer result, StringBuffer resultTrailer) throws FormatException {
        boolean upperShift = false;

        do {
            int oneByte = bits.readBits(8);
            if(oneByte == 0) {
                throw FormatException.getFormatInstance();
            }

            if(oneByte <= 128) {
                oneByte = upperShift?oneByte + 128:oneByte;
                upperShift = false;
                result.append((char)(oneByte - 1));
                return 1;
            }

            if(oneByte == 129) {
                return 0;
            }

            if(oneByte <= 229) {
                int value = oneByte - 130;
                if(value < 10) {
                    result.append('0');
                }

                result.append(value);
            } else {
                if(oneByte == 230) {
                    return 2;
                }

                if(oneByte == 231) {
                    return 6;
                }

                if(oneByte != 232 && oneByte != 233 && oneByte != 234) {
                    if(oneByte == 235) {
                        upperShift = true;
                    } else if(oneByte == 236) {
                        result.append("[)>\u001e05\u001d");
                        resultTrailer.insert(0, "\u001e\u0004");
                    } else if(oneByte == 237) {
                        result.append("[)>\u001e06\u001d");
                        resultTrailer.insert(0, "\u001e\u0004");
                    } else {
                        if(oneByte == 238) {
                            return 4;
                        }

                        if(oneByte == 239) {
                            return 3;
                        }

                        if(oneByte == 240) {
                            return 5;
                        }

                        if(oneByte != 241 && oneByte >= 242) {
                            throw FormatException.getFormatInstance();
                        }
                    }
                }
            }
        } while(bits.available() > 0);

        return 1;
    }

    private static void decodeC40Segment(BitSource bits, StringBuffer result) throws FormatException {
        boolean upperShift = false;
        int[] cValues = new int[3];

        do {
            if(bits.available() == 8) {
                return;
            }

            int firstByte = bits.readBits(8);
            if(firstByte == 254) {
                return;
            }

            parseTwoBytes(firstByte, bits.readBits(8), cValues);
            int shift = 0;

            for(int i = 0; i < 3; ++i) {
                int cValue = cValues[i];
                switch(shift) {
                case 0:
                    if(cValue < 3) {
                        shift = cValue + 1;
                    } else if(upperShift) {
                        result.append((char)(C40_BASIC_SET_CHARS[cValue] + 128));
                        upperShift = false;
                    } else {
                        result.append(C40_BASIC_SET_CHARS[cValue]);
                    }
                    break;
                case 1:
                    if(upperShift) {
                        result.append((char)(cValue + 128));
                        upperShift = false;
                    } else {
                        result.append(cValue);
                    }

                    shift = 0;
                    break;
                case 2:
                    if(cValue < 27) {
                        if(upperShift) {
                            result.append((char)(C40_SHIFT2_SET_CHARS[cValue] + 128));
                            upperShift = false;
                        } else {
                            result.append(C40_SHIFT2_SET_CHARS[cValue]);
                        }
                    } else {
                        if(cValue == 27) {
                            throw FormatException.getFormatInstance();
                        }

                        if(cValue != 30) {
                            throw FormatException.getFormatInstance();
                        }

                        upperShift = true;
                    }

                    shift = 0;
                    break;
                case 3:
                    if(upperShift) {
                        result.append((char)(cValue + 224));
                        upperShift = false;
                    } else {
                        result.append((char)(cValue + 96));
                    }

                    shift = 0;
                    break;
                default:
                    throw FormatException.getFormatInstance();
                }
            }
        } while(bits.available() > 0);

    }

    private static void decodeTextSegment(BitSource bits, StringBuffer result) throws FormatException {
        boolean upperShift = false;
        int[] cValues = new int[3];

        do {
            if(bits.available() == 8) {
                return;
            }

            int firstByte = bits.readBits(8);
            if(firstByte == 254) {
                return;
            }

            parseTwoBytes(firstByte, bits.readBits(8), cValues);
            int shift = 0;

            for(int i = 0; i < 3; ++i) {
                int cValue = cValues[i];
                switch(shift) {
                case 0:
                    if(cValue < 3) {
                        shift = cValue + 1;
                    } else if(upperShift) {
                        result.append((char)(TEXT_BASIC_SET_CHARS[cValue] + 128));
                        upperShift = false;
                    } else {
                        result.append(TEXT_BASIC_SET_CHARS[cValue]);
                    }
                    break;
                case 1:
                    if(upperShift) {
                        result.append((char)(cValue + 128));
                        upperShift = false;
                    } else {
                        result.append(cValue);
                    }

                    shift = 0;
                    break;
                case 2:
                    if(cValue < 27) {
                        if(upperShift) {
                            result.append((char)(C40_SHIFT2_SET_CHARS[cValue] + 128));
                            upperShift = false;
                        } else {
                            result.append(C40_SHIFT2_SET_CHARS[cValue]);
                        }
                    } else {
                        if(cValue == 27) {
                            throw FormatException.getFormatInstance();
                        }

                        if(cValue != 30) {
                            throw FormatException.getFormatInstance();
                        }

                        upperShift = true;
                    }

                    shift = 0;
                    break;
                case 3:
                    if(upperShift) {
                        result.append((char)(TEXT_SHIFT3_SET_CHARS[cValue] + 128));
                        upperShift = false;
                    } else {
                        result.append(TEXT_SHIFT3_SET_CHARS[cValue]);
                    }

                    shift = 0;
                    break;
                default:
                    throw FormatException.getFormatInstance();
                }
            }
        } while(bits.available() > 0);

    }

    private static void decodeAnsiX12Segment(BitSource bits, StringBuffer result) throws FormatException {
        int[] cValues = new int[3];

        do {
            if(bits.available() == 8) {
                return;
            }

            int firstByte = bits.readBits(8);
            if(firstByte == 254) {
                return;
            }

            parseTwoBytes(firstByte, bits.readBits(8), cValues);

            for(int i = 0; i < 3; ++i) {
                int cValue = cValues[i];
                if(cValue == 0) {
                    result.append('\r');
                } else if(cValue == 1) {
                    result.append('*');
                } else if(cValue == 2) {
                    result.append('>');
                } else if(cValue == 3) {
                    result.append(' ');
                } else if(cValue < 14) {
                    result.append((char)(cValue + 44));
                } else {
                    if(cValue >= 40) {
                        throw FormatException.getFormatInstance();
                    }

                    result.append((char)(cValue + 51));
                }
            }
        } while(bits.available() > 0);

    }

    private static void parseTwoBytes(int firstByte, int secondByte, int[] result) {
        int fullBitValue = (firstByte << 8) + secondByte - 1;
        int temp = fullBitValue / 1600;
        result[0] = temp;
        fullBitValue -= temp * 1600;
        temp = fullBitValue / 40;
        result[1] = temp;
        result[2] = fullBitValue - temp * 40;
    }

    private static void decodeEdifactSegment(BitSource bits, StringBuffer result) {
        boolean unlatch = false;

        do {
            if(bits.available() <= 16) {
                return;
            }

            for(int i = 0; i < 4; ++i) {
                int edifactValue = bits.readBits(6);
                if(edifactValue == 11111) {
                    unlatch = true;
                }

                if(!unlatch) {
                    if((edifactValue & 32) == 0) {
                        edifactValue |= 64;
                    }

                    result.append(edifactValue);
                }
            }
        } while(!unlatch && bits.available() > 0);

    }

    private static void decodeBase256Segment(BitSource bits, StringBuffer result, Vector byteSegments) throws FormatException {
        int d1 = bits.readBits(8);
        int count;
        if(d1 == 0) {
            count = bits.available() / 8;
        } else if(d1 < 250) {
            count = d1;
        } else {
            count = 250 * (d1 - 249) + bits.readBits(8);
        }

        byte[] bytes = new byte[count];

        for(int uee = 0; uee < count; ++uee) {
            if(bits.available() < 8) {
                throw FormatException.getFormatInstance();
            }

            bytes[uee] = unrandomize255State(bits.readBits(8), uee);
        }

        byteSegments.addElement(bytes);

        try {
            result.append(new String(bytes, "ISO8859_1"));
        } catch (UnsupportedEncodingException var7) {
            throw new RuntimeException("Platform does not support required encoding: " + var7);
        }
    }

    private static byte unrandomize255State(int randomizedBase256Codeword, int base256CodewordPosition) {
        int pseudoRandomNumber = 149 * base256CodewordPosition % 255 + 1;
        int tempVariable = randomizedBase256Codeword - pseudoRandomNumber;
        return (byte)(tempVariable >= 0?tempVariable:tempVariable + 256);
    }
}
