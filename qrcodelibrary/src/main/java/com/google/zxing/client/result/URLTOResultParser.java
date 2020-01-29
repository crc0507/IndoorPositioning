//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.URIParsedResult;

final class URLTOResultParser {
    private URLTOResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null || !rawText.startsWith("urlto:") && !rawText.startsWith("URLTO:")) {
            return null;
        } else {
            int titleEnd = rawText.indexOf(58, 6);
            if(titleEnd < 0) {
                return null;
            } else {
                String title = titleEnd <= 6?null:rawText.substring(6, titleEnd);
                String uri = rawText.substring(titleEnd + 1);
                return new URIParsedResult(uri, title);
            }
        }
    }
}
