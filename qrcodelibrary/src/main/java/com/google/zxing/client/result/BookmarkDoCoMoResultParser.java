//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AbstractDoCoMoResultParser;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.URIResultParser;

final class BookmarkDoCoMoResultParser extends AbstractDoCoMoResultParser {
    private BookmarkDoCoMoResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("MEBKM:")) {
            String title = matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
            String[] rawUri = matchDoCoMoPrefixedField("URL:", rawText, true);
            if(rawUri == null) {
                return null;
            } else {
                String uri = rawUri[0];
                return !URIResultParser.isBasicallyValidURI(uri)?null:new URIParsedResult(uri, title);
            }
        } else {
            return null;
        }
    }
}
