//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ResultParser;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

final class VCardResultParser extends ResultParser {
    private VCardResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("BEGIN:VCARD")) {
            String[] names = matchVCardPrefixedField("FN", rawText, true);
            if(names == null) {
                names = matchVCardPrefixedField("N", rawText, true);
                formatNames(names);
            }

            String[] phoneNumbers = matchVCardPrefixedField("TEL", rawText, true);
            String[] emails = matchVCardPrefixedField("EMAIL", rawText, true);
            String note = matchSingleVCardPrefixedField("NOTE", rawText, false);
            String[] addresses = matchVCardPrefixedField("ADR", rawText, true);
            if(addresses != null) {
                for(int org = 0; org < addresses.length; ++org) {
                    addresses[org] = formatAddress(addresses[org]);
                }
            }

            String var11 = matchSingleVCardPrefixedField("ORG", rawText, true);
            String birthday = matchSingleVCardPrefixedField("BDAY", rawText, true);
            if(!isLikeVCardDate(birthday)) {
                birthday = null;
            }

            String title = matchSingleVCardPrefixedField("TITLE", rawText, true);
            String url = matchSingleVCardPrefixedField("URL", rawText, true);
            return new AddressBookParsedResult(names, (String)null, phoneNumbers, emails, note, addresses, var11, birthday, title, url);
        } else {
            return null;
        }
    }

    private static String[] matchVCardPrefixedField(String prefix, String rawText, boolean trim) {
        Vector matches = null;
        int i = 0;
        int max = rawText.length();

        while(i < max) {
            i = rawText.indexOf(prefix, i);
            if(i < 0) {
                break;
            }

            if(i > 0 && rawText.charAt(i - 1) != 10) {
                ++i;
            } else {
                i += prefix.length();
                if(rawText.charAt(i) == 58 || rawText.charAt(i) == 59) {
                    int metadataStart;
                    for(metadataStart = i; rawText.charAt(i) != 58; ++i) {
                        ;
                    }

                    boolean quotedPrintable = false;
                    String quotedPrintableCharset = null;
                    int matchStart;
                    String element;
                    if(i > metadataStart) {
                        for(matchStart = metadataStart + 1; matchStart <= i; ++matchStart) {
                            if(rawText.charAt(matchStart) == 59 || rawText.charAt(matchStart) == 58) {
                                element = rawText.substring(metadataStart + 1, matchStart);
                                int equals = element.indexOf(61);
                                if(equals >= 0) {
                                    String key = element.substring(0, equals);
                                    String value = element.substring(equals + 1);
                                    if(key.equalsIgnoreCase("ENCODING")) {
                                        if(value.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                                            quotedPrintable = true;
                                        }
                                    } else if(key.equalsIgnoreCase("CHARSET")) {
                                        quotedPrintableCharset = value;
                                    }
                                }

                                metadataStart = matchStart;
                            }
                        }
                    }

                    ++i;
                    matchStart = i;

                    while((i = rawText.indexOf(10, i)) >= 0) {
                        if(i >= rawText.length() - 1 || rawText.charAt(i + 1) != 32 && rawText.charAt(i + 1) != 9) {
                            if(!quotedPrintable || rawText.charAt(i - 1) != 61 && rawText.charAt(i - 2) != 61) {
                                break;
                            }

                            ++i;
                        } else {
                            i += 2;
                        }
                    }

                    if(i < 0) {
                        i = max;
                    } else if(i > matchStart) {
                        if(matches == null) {
                            matches = new Vector(1);
                        }

                        if(rawText.charAt(i - 1) == 13) {
                            --i;
                        }

                        element = rawText.substring(matchStart, i);
                        if(trim) {
                            element = element.trim();
                        }

                        if(quotedPrintable) {
                            element = decodeQuotedPrintable(element, quotedPrintableCharset);
                        } else {
                            element = stripContinuationCRLF(element);
                        }

                        matches.addElement(element);
                        ++i;
                    } else {
                        ++i;
                    }
                }
            }
        }

        return matches != null && !matches.isEmpty()?toStringArray(matches):null;
    }

    private static String stripContinuationCRLF(String value) {
        int length = value.length();
        StringBuffer result = new StringBuffer(length);
        boolean lastWasLF = false;

        for(int i = 0; i < length; ++i) {
            if(lastWasLF) {
                lastWasLF = false;
            } else {
                char c = value.charAt(i);
                lastWasLF = false;
                switch(c) {
                case '\n':
                    lastWasLF = true;
                case '\r':
                    break;
                default:
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    private static String decodeQuotedPrintable(String value, String charset) {
        int length = value.length();
        StringBuffer result = new StringBuffer(length);
        ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();

        for(int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            switch(c) {
            case '\n':
            case '\r':
                break;
            case '=':
                if(i < length - 2) {
                    char nextChar = value.charAt(i + 1);
                    if(nextChar != 13 && nextChar != 10) {
                        char nextNextChar = value.charAt(i + 2);

                        try {
                            int iae = 16 * toHexValue(nextChar) + toHexValue(nextNextChar);
                            fragmentBuffer.write(iae);
                        } catch (IllegalArgumentException var10) {
                            ;
                        }

                        i += 2;
                    }
                }
                break;
            default:
                maybeAppendFragment(fragmentBuffer, charset, result);
                result.append(c);
            }
        }

        maybeAppendFragment(fragmentBuffer, charset, result);
        return result.toString();
    }

    private static int toHexValue(char c) {
        if(c >= 48 && c <= 57) {
            return c - 48;
        } else if(c >= 65 && c <= 70) {
            return c - 65 + 10;
        } else if(c >= 97 && c <= 102) {
            return c - 97 + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void maybeAppendFragment(ByteArrayOutputStream fragmentBuffer, String charset, StringBuffer result) {
        if(fragmentBuffer.size() > 0) {
            byte[] fragmentBytes = fragmentBuffer.toByteArray();
            String fragment;
            if(charset == null) {
                fragment = new String(fragmentBytes);
            } else {
                try {
                    fragment = new String(fragmentBytes, charset);
                } catch (UnsupportedEncodingException var6) {
                    fragment = new String(fragmentBytes);
                }
            }

            fragmentBuffer.reset();
            result.append(fragment);
        }

    }

    static String matchSingleVCardPrefixedField(String prefix, String rawText, boolean trim) {
        String[] values = matchVCardPrefixedField(prefix, rawText, trim);
        return values == null?null:values[0];
    }

    private static boolean isLikeVCardDate(String value) {
        return value == null?true:(isStringOfDigits(value, 8)?true:value.length() == 10 && value.charAt(4) == 45 && value.charAt(7) == 45 && isSubstringOfDigits(value, 0, 4) && isSubstringOfDigits(value, 5, 2) && isSubstringOfDigits(value, 8, 2));
    }

    private static String formatAddress(String address) {
        if(address == null) {
            return null;
        } else {
            int length = address.length();
            StringBuffer newAddress = new StringBuffer(length);

            for(int j = 0; j < length; ++j) {
                char c = address.charAt(j);
                if(c == 59) {
                    newAddress.append(' ');
                } else {
                    newAddress.append(c);
                }
            }

            return newAddress.toString().trim();
        }
    }

    private static void formatNames(String[] names) {
        if(names != null) {
            for(int i = 0; i < names.length; ++i) {
                String name = names[i];
                String[] components = new String[5];
                int start = 0;

                int end;
                int componentIndex;
                for(componentIndex = 0; (end = name.indexOf(59, start)) > 0; start = end + 1) {
                    components[componentIndex] = name.substring(start, end);
                    ++componentIndex;
                }

                components[componentIndex] = name.substring(start);
                StringBuffer newName = new StringBuffer(100);
                maybeAppendComponent(components, 3, newName);
                maybeAppendComponent(components, 1, newName);
                maybeAppendComponent(components, 2, newName);
                maybeAppendComponent(components, 0, newName);
                maybeAppendComponent(components, 4, newName);
                names[i] = newName.toString().trim();
            }
        }

    }

    private static void maybeAppendComponent(String[] components, int i, StringBuffer newName) {
        if(components[i] != null) {
            newName.append(' ');
            newName.append(components[i]);
        }

    }
}
