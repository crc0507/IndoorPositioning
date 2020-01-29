//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookAUResultParser;
import com.google.zxing.client.result.AddressBookDoCoMoResultParser;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.BizcardResultParser;
import com.google.zxing.client.result.BookmarkDoCoMoResultParser;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.EmailAddressResultParser;
import com.google.zxing.client.result.EmailDoCoMoResultParser;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.ExpandedProductResultParser;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.GeoResultParser;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ISBNResultParser;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.ProductResultParser;
import com.google.zxing.client.result.SMSMMSResultParser;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.SMSTOMMSTOResultParser;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.TelResultParser;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.URIResultParser;
import com.google.zxing.client.result.URLTOResultParser;
import com.google.zxing.client.result.VCardResultParser;
import com.google.zxing.client.result.VEventResultParser;
import com.google.zxing.client.result.WifiParsedResult;
import com.google.zxing.client.result.WifiResultParser;
import java.util.Hashtable;
import java.util.Vector;

public abstract class ResultParser {
    public ResultParser() {
    }

    public static ParsedResult parseResult(Result theResult) {
        URIParsedResult result;
        AddressBookParsedResult result1;
        EmailAddressParsedResult result2;
        CalendarParsedResult result3;
        TelParsedResult result4;
        SMSParsedResult result5;
        GeoParsedResult result6;
        WifiParsedResult result7;
        ISBNParsedResult result8;
        ProductParsedResult result9;
        ExpandedProductParsedResult result10;
        return (ParsedResult)((result = BookmarkDoCoMoResultParser.parse(theResult)) != null?result:((result1 = AddressBookDoCoMoResultParser.parse(theResult)) != null?result1:((result2 = EmailDoCoMoResultParser.parse(theResult)) != null?result2:((result1 = AddressBookAUResultParser.parse(theResult)) != null?result1:((result1 = VCardResultParser.parse(theResult)) != null?result1:((result1 = BizcardResultParser.parse(theResult)) != null?result1:((result3 = VEventResultParser.parse(theResult)) != null?result3:((result2 = EmailAddressResultParser.parse(theResult)) != null?result2:((result4 = TelResultParser.parse(theResult)) != null?result4:((result5 = SMSMMSResultParser.parse(theResult)) != null?result5:((result5 = SMSTOMMSTOResultParser.parse(theResult)) != null?result5:((result6 = GeoResultParser.parse(theResult)) != null?result6:((result7 = WifiResultParser.parse(theResult)) != null?result7:((result = URLTOResultParser.parse(theResult)) != null?result:((result = URIResultParser.parse(theResult)) != null?result:((result8 = ISBNResultParser.parse(theResult)) != null?result8:((result9 = ProductResultParser.parse(theResult)) != null?result9:((result10 = ExpandedProductResultParser.parse(theResult)) != null?result10:new TextParsedResult(theResult.getText(), (String)null)))))))))))))))))));
    }

    protected static void maybeAppend(String value, StringBuffer result) {
        if(value != null) {
            result.append('\n');
            result.append(value);
        }

    }

    protected static void maybeAppend(String[] value, StringBuffer result) {
        if(value != null) {
            for(int i = 0; i < value.length; ++i) {
                result.append('\n');
                result.append(value[i]);
            }
        }

    }

    protected static String[] maybeWrap(String value) {
        return value == null?null:new String[]{value};
    }

    protected static String unescapeBackslash(String escaped) {
        if(escaped != null) {
            int backslash = escaped.indexOf(92);
            if(backslash >= 0) {
                int max = escaped.length();
                StringBuffer unescaped = new StringBuffer(max - 1);
                unescaped.append(escaped.toCharArray(), 0, backslash);
                boolean nextIsEscaped = false;

                for(int i = backslash; i < max; ++i) {
                    char c = escaped.charAt(i);
                    if(!nextIsEscaped && c == 92) {
                        nextIsEscaped = true;
                    } else {
                        unescaped.append(c);
                        nextIsEscaped = false;
                    }
                }

                return unescaped.toString();
            }
        }

        return escaped;
    }

    private static String urlDecode(String escaped) {
        if(escaped == null) {
            return null;
        } else {
            char[] escapedArray = escaped.toCharArray();
            int first = findFirstEscape(escapedArray);
            if(first < 0) {
                return escaped;
            } else {
                int max = escapedArray.length;
                StringBuffer unescaped = new StringBuffer(max - 2);
                unescaped.append(escapedArray, 0, first);

                for(int i = first; i < max; ++i) {
                    char c = escapedArray[i];
                    if(c == 43) {
                        unescaped.append(' ');
                    } else if(c == 37) {
                        if(i >= max - 2) {
                            unescaped.append('%');
                        } else {
                            ++i;
                            int firstDigitValue = parseHexDigit(escapedArray[i]);
                            ++i;
                            int secondDigitValue = parseHexDigit(escapedArray[i]);
                            if(firstDigitValue < 0 || secondDigitValue < 0) {
                                unescaped.append('%');
                                unescaped.append(escapedArray[i - 1]);
                                unescaped.append(escapedArray[i]);
                            }

                            unescaped.append((char)((firstDigitValue << 4) + secondDigitValue));
                        }
                    } else {
                        unescaped.append(c);
                    }
                }

                return unescaped.toString();
            }
        }
    }

    private static int findFirstEscape(char[] escapedArray) {
        int max = escapedArray.length;

        for(int i = 0; i < max; ++i) {
            char c = escapedArray[i];
            if(c == 43 || c == 37) {
                return i;
            }
        }

        return -1;
    }

    private static int parseHexDigit(char c) {
        if(c >= 97) {
            if(c <= 102) {
                return 10 + (c - 97);
            }
        } else if(c >= 65) {
            if(c <= 70) {
                return 10 + (c - 65);
            }
        } else if(c >= 48 && c <= 57) {
            return c - 48;
        }

        return -1;
    }

    protected static boolean isStringOfDigits(String value, int length) {
        if(value == null) {
            return false;
        } else {
            int stringLength = value.length();
            if(length != stringLength) {
                return false;
            } else {
                for(int i = 0; i < length; ++i) {
                    char c = value.charAt(i);
                    if(c < 48 || c > 57) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    protected static boolean isSubstringOfDigits(String value, int offset, int length) {
        if(value == null) {
            return false;
        } else {
            int stringLength = value.length();
            int max = offset + length;
            if(stringLength < max) {
                return false;
            } else {
                for(int i = offset; i < max; ++i) {
                    char c = value.charAt(i);
                    if(c < 48 || c > 57) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    static Hashtable parseNameValuePairs(String uri) {
        int paramStart = uri.indexOf(63);
        if(paramStart < 0) {
            return null;
        } else {
            Hashtable result = new Hashtable(3);
            ++paramStart;

            int paramEnd;
            while((paramEnd = uri.indexOf(38, paramStart)) >= 0) {
                appendKeyValue(uri, paramStart, paramEnd, result);
                paramStart = paramEnd + 1;
            }

            appendKeyValue(uri, paramStart, uri.length(), result);
            return result;
        }
    }

    private static void appendKeyValue(String uri, int paramStart, int paramEnd, Hashtable result) {
        int separator = uri.indexOf(61, paramStart);
        if(separator >= 0) {
            String key = uri.substring(paramStart, separator);
            String value = uri.substring(separator + 1, paramEnd);
            value = urlDecode(value);
            result.put(key, value);
        }

    }

    static String[] matchPrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        Vector matches = null;
        int i = 0;
        int max = rawText.length();

        while(i < max) {
            i = rawText.indexOf(prefix, i);
            if(i < 0) {
                break;
            }

            i += prefix.length();
            int start = i;
            boolean done = false;

            while(!done) {
                i = rawText.indexOf(endChar, i);
                if(i < 0) {
                    i = rawText.length();
                    done = true;
                } else if(rawText.charAt(i - 1) == 92) {
                    ++i;
                } else {
                    if(matches == null) {
                        matches = new Vector(3);
                    }

                    String element = unescapeBackslash(rawText.substring(start, i));
                    if(trim) {
                        element = element.trim();
                    }

                    matches.addElement(element);
                    ++i;
                    done = true;
                }
            }
        }

        return matches != null && !matches.isEmpty()?toStringArray(matches):null;
    }

    static String matchSinglePrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        String[] matches = matchPrefixedField(prefix, rawText, endChar, trim);
        return matches == null?null:matches[0];
    }

    static String[] toStringArray(Vector strings) {
        int size = strings.size();
        String[] result = new String[size];

        for(int j = 0; j < size; ++j) {
            result[j] = (String)strings.elementAt(j);
        }

        return result;
    }
}
