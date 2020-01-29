//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AbstractDoCoMoResultParser;
import com.google.zxing.client.result.EmailAddressParsedResult;

final class EmailDoCoMoResultParser extends AbstractDoCoMoResultParser {
    private static final char[] ATEXT_SYMBOLS = new char[]{'@', '.', '!', '#', '$', '%', '&', '\'', '*', '+', '-', '/', '=', '?', '^', '_', '`', '{', '|', '}', '~'};

    EmailDoCoMoResultParser() {
    }

    public static EmailAddressParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("MATMSG:")) {
            String[] rawTo = matchDoCoMoPrefixedField("TO:", rawText, true);
            if(rawTo == null) {
                return null;
            } else {
                String to = rawTo[0];
                if(!isBasicallyValidEmailAddress(to)) {
                    return null;
                } else {
                    String subject = matchSingleDoCoMoPrefixedField("SUB:", rawText, false);
                    String body = matchSingleDoCoMoPrefixedField("BODY:", rawText, false);
                    return new EmailAddressParsedResult(to, subject, body, "mailto:" + to);
                }
            }
        } else {
            return null;
        }
    }

    static boolean isBasicallyValidEmailAddress(String email) {
        if(email == null) {
            return false;
        } else {
            boolean atFound = false;

            for(int i = 0; i < email.length(); ++i) {
                char c = email.charAt(i);
                if((c < 97 || c > 122) && (c < 65 || c > 90) && (c < 48 || c > 57) && !isAtextSymbol(c)) {
                    return false;
                }

                if(c == 64) {
                    if(atFound) {
                        return false;
                    }

                    atFound = true;
                }
            }

            return atFound;
        }
    }

    private static boolean isAtextSymbol(char c) {
        for(int i = 0; i < ATEXT_SYMBOLS.length; ++i) {
            if(c == ATEXT_SYMBOLS[i]) {
                return true;
            }
        }

        return false;
    }
}
