//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.Vector;

final class DecodedBitStreamParser {
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final int ALPHA = 0;
    private static final int LOWER = 1;
    private static final int MIXED = 2;
    private static final int PUNCT = 3;
    private static final int PUNCT_SHIFT = 4;
    private static final int PL = 25;
    private static final int LL = 27;
    private static final int AS = 27;
    private static final int ML = 28;
    private static final int AL = 28;
    private static final int PS = 29;
    private static final int PAL = 29;
    private static final char[] PUNCT_CHARS = new char[]{';', '<', '>', '@', '[', '\\', '}', '_', '`', '~', '!', '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\''};
    private static final char[] MIXED_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^'};
    private static final String[] EXP900 = new String[]{"000000000000000000000000000000000000000000001", "000000000000000000000000000000000000000000900", "000000000000000000000000000000000000000810000", "000000000000000000000000000000000000729000000", "000000000000000000000000000000000656100000000", "000000000000000000000000000000590490000000000", "000000000000000000000000000531441000000000000", "000000000000000000000000478296900000000000000", "000000000000000000000430467210000000000000000", "000000000000000000387420489000000000000000000", "000000000000000348678440100000000000000000000", "000000000000313810596090000000000000000000000", "000000000282429536481000000000000000000000000", "000000254186582832900000000000000000000000000", "000228767924549610000000000000000000000000000", "205891132094649000000000000000000000000000000"};

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(int[] codewords) throws FormatException {
        StringBuffer result = new StringBuffer(100);
        byte codeIndex = 1;
        int var4 = codeIndex + 1;

        for(int code = codewords[codeIndex]; var4 < codewords[0]; code = codewords[var4++]) {
            switch(code) {
                case 900:
                    var4 = textCompaction(codewords, var4, result);
                    break;
                case 901:
                    var4 = byteCompaction(code, codewords, var4, result);
                    break;
                case 902:
                    var4 = numericCompaction(codewords, var4, result);
                    break;
                case 913:
                    var4 = byteCompaction(code, codewords, var4, result);
                    break;
                case 924:
                    var4 = byteCompaction(code, codewords, var4, result);
                    break;
                default:
                    --var4;
                    var4 = textCompaction(codewords, var4, result);
            }

            if(var4 >= codewords.length) {
                throw FormatException.getFormatInstance();
            }
        }

        return new DecoderResult((byte[])null, result.toString(), (Vector)null, (ErrorCorrectionLevel)null);
    }

    private static int textCompaction(int[] codewords, int codeIndex, StringBuffer result) {
        int[] textCompactionData = new int[codewords[0] << 1];
        int[] byteCompactionData = new int[codewords[0] << 1];
        int index = 0;
        boolean end = false;

        while(codeIndex < codewords[0] && !end) {
            int code = codewords[codeIndex++];
            if(code < 900) {
                textCompactionData[index] = code / 30;
                textCompactionData[index + 1] = code % 30;
                index += 2;
            } else {
                switch(code) {
                    case 900:
                        --codeIndex;
                        end = true;
                        break;
                    case 901:
                        --codeIndex;
                        end = true;
                        break;
                    case 902:
                        --codeIndex;
                        end = true;
                        break;
                    case 913:
                        textCompactionData[index] = 913;
                        byteCompactionData[index] = code;
                        ++index;
                        break;
                    case 924:
                        --codeIndex;
                        end = true;
                }
            }
        }

        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return codeIndex;
    }

    private static void decodeTextCompaction(int[] textCompactionData, int[] byteCompactionData, int length, StringBuffer result) {
        byte subMode = 0;
        byte priorToShiftMode = 0;

        for(int i = 0; i < length; ++i) {
            int subModeCh = textCompactionData[i];
            char ch = 0;
            switch(subMode) {
                case 0:
                    if(subModeCh < 26) {
                        ch = (char)(65 + subModeCh);
                    } else if(subModeCh == 26) {
                        ch = 32;
                    } else if(subModeCh == 27) {
                        subMode = 1;
                    } else if(subModeCh == 28) {
                        subMode = 2;
                    } else if(subModeCh == 29) {
                        priorToShiftMode = subMode;
                        subMode = 4;
                    } else if(subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                    }
                    break;
                case 1:
                    if(subModeCh < 26) {
                        ch = (char)(97 + subModeCh);
                    } else if(subModeCh == 26) {
                        ch = 32;
                    } else if(subModeCh == 28) {
                        subMode = 0;
                    } else if(subModeCh == 28) {
                        subMode = 2;
                    } else if(subModeCh == 29) {
                        priorToShiftMode = subMode;
                        subMode = 4;
                    } else if(subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                    }
                    break;
                case 2:
                    if(subModeCh < 25) {
                        ch = MIXED_CHARS[subModeCh];
                    } else if(subModeCh == 25) {
                        subMode = 3;
                    } else if(subModeCh == 26) {
                        ch = 32;
                    } else if(subModeCh != 27) {
                        if(subModeCh == 28) {
                            subMode = 0;
                        } else if(subModeCh == 29) {
                            priorToShiftMode = subMode;
                            subMode = 4;
                        } else if(subModeCh == 913) {
                            result.append((char)byteCompactionData[i]);
                        }
                    }
                    break;
                case 3:
                    if(subModeCh < 29) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else if(subModeCh == 29) {
                        subMode = 0;
                    } else if(subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                    }
                    break;
                case 4:
                    subMode = priorToShiftMode;
                    if(subModeCh < 29) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else if(subModeCh == 29) {
                        subMode = 0;
                    }
            }

            if(ch != 0) {
                result.append(ch);
            }
        }

    }

    private static int byteCompaction(int mode, int[] codewords, int codeIndex, StringBuffer result) {
        int count;
        long value;
        int j;
        if(mode == 901) {
            count = 0;
            value = 0L;
            char[] end = new char[6];
            int[] code = new int[6];
            boolean decodedData = false;

            label121:
            while(true) {
                do {
                    do {
                        if(codeIndex >= codewords[0] || decodedData) {
                            for(j = count / 5 * 5; j < count; ++j) {
                                result.append((char)code[j]);
                            }
                            break label121;
                        }

                        j = codewords[codeIndex++];
                        if(j < 900) {
                            code[count] = j;
                            ++count;
                            value = 900L * value + (long)j;
                        } else if(j == 900 || j == 901 || j == 902 || j == 924 || j == 928 || j == 923 || j == 922) {
                            --codeIndex;
                            decodedData = true;
                        }
                    } while(count % 5 != 0);
                } while(count <= 0);

                for(int j1 = 0; j1 < 6; ++j1) {
                    end[5 - j1] = (char)((int)(value % 256L));
                    value >>= 8;
                }

                result.append(end);
                count = 0;
            }
        } else if(mode == 924) {
            count = 0;
            value = 0L;
            boolean var12 = false;

            while(true) {
                do {
                    do {
                        if(codeIndex >= codewords[0] || var12) {
                            return codeIndex;
                        }

                        int var13 = codewords[codeIndex++];
                        if(var13 < 900) {
                            ++count;
                            value = 900L * value + (long)var13;
                        } else if(var13 == 900 || var13 == 901 || var13 == 902 || var13 == 924 || var13 == 928 || var13 == 923 || var13 == 922) {
                            --codeIndex;
                            var12 = true;
                        }
                    } while(count % 5 != 0);
                } while(count <= 0);

                char[] var14 = new char[6];

                for(j = 0; j < 6; ++j) {
                    var14[5 - j] = (char)((int)(value & 255L));
                    value >>= 8;
                }

                result.append(var14);
            }
        }

        return codeIndex;
    }

    private static int numericCompaction(int[] codewords, int codeIndex, StringBuffer result) {
        int count = 0;
        boolean end = false;
        int[] numericCodewords = new int[15];

        while(codeIndex < codewords[0] && !end) {
            int code = codewords[codeIndex++];
            if(codeIndex == codewords[0]) {
                end = true;
            }

            if(code < 900) {
                numericCodewords[count] = code;
                ++count;
            } else if(code == 900 || code == 901 || code == 924 || code == 928 || code == 923 || code == 922) {
                --codeIndex;
                end = true;
            }

            if(count % 15 == 0 || code == 902 || end) {
                String s = decodeBase900toBase10(numericCodewords, count);
                result.append(s);
                count = 0;
            }
        }

        return codeIndex;
    }

    private static String decodeBase900toBase10(int[] codewords, int count) {
        StringBuffer accum = null;

        for(int result = 0; result < count; ++result) {
            StringBuffer i = multiply(EXP900[count - result - 1], codewords[result]);
            if(accum == null) {
                accum = i;
            } else {
                accum = add(accum.toString(), i.toString());
            }
        }

        String var5 = null;

        for(int var6 = 0; var6 < accum.length(); ++var6) {
            if(accum.charAt(var6) == 49) {
                var5 = accum.toString().substring(var6 + 1);
                break;
            }
        }

        if(var5 == null) {
            var5 = accum.toString();
        }

        return var5;
    }

    private static StringBuffer multiply(String value1, int value2) {
        StringBuffer result = new StringBuffer(value1.length());

        int hundreds;
        for(hundreds = 0; hundreds < value1.length(); ++hundreds) {
            result.append('0');
        }

        hundreds = value2 / 100;
        int tens = value2 / 10 % 10;
        int ones = value2 % 10;

        int j;
        for(j = 0; j < ones; ++j) {
            result = add(result.toString(), value1);
        }

        for(j = 0; j < tens; ++j) {
            result = add(result.toString(), (value1 + '0').substring(1));
        }

        for(j = 0; j < hundreds; ++j) {
            result = add(result.toString(), (value1 + "00").substring(2));
        }

        return result;
    }

    private static StringBuffer add(String value1, String value2) {
        StringBuffer temp1 = new StringBuffer(5);
        StringBuffer temp2 = new StringBuffer(5);
        StringBuffer result = new StringBuffer(value1.length());

        int carry;
        for(carry = 0; carry < value1.length(); ++carry) {
            result.append('0');
        }

        carry = 0;

        for(int i = value1.length() - 3; i > -1; i -= 3) {
            temp1.setLength(0);
            temp1.append(value1.charAt(i));
            temp1.append(value1.charAt(i + 1));
            temp1.append(value1.charAt(i + 2));
            temp2.setLength(0);
            temp2.append(value2.charAt(i));
            temp2.append(value2.charAt(i + 1));
            temp2.append(value2.charAt(i + 2));
            int intValue1 = Integer.parseInt(temp1.toString());
            int intValue2 = Integer.parseInt(temp2.toString());
            int sumval = (intValue1 + intValue2 + carry) % 1000;
            carry = (intValue1 + intValue2 + carry) / 1000;
            result.setCharAt(i + 2, (char)(sumval % 10 + 48));
            result.setCharAt(i + 1, (char)(sumval / 10 % 10 + 48));
            result.setCharAt(i, (char)(sumval / 100 + 48));
        }

        return result;
    }
}
