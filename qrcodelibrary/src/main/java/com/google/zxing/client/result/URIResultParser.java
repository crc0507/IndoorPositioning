//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.URIParsedResult;

final class URIResultParser extends ResultParser {
    private URIResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("URL:")) {
            rawText = rawText.substring(4);
        }

        return !isBasicallyValidURI(rawText)?null:new URIParsedResult(rawText, (String)null);
    }

    static boolean isBasicallyValidURI(String uri) {
        if(uri != null && uri.indexOf(32) < 0 && uri.indexOf(10) < 0) {
            int period = uri.indexOf(46);
            if(period >= uri.length() - 2) {
                return false;
            } else {
                int colon = uri.indexOf(58);
                if(period < 0 && colon < 0) {
                    return false;
                } else {
                    if(colon >= 0) {
                        int i;
                        char c;
                        if(period >= 0 && period <= colon) {
                            if(colon >= uri.length() - 2) {
                                return false;
                            }

                            for(i = colon + 1; i < colon + 3; ++i) {
                                c = uri.charAt(i);
                                if(c < 48 || c > 57) {
                                    return false;
                                }
                            }
                        } else {
                            for(i = 0; i < colon; ++i) {
                                c = uri.charAt(i);
                                if((c < 97 || c > 122) && (c < 65 || c > 90)) {
                                    return false;
                                }
                            }
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
