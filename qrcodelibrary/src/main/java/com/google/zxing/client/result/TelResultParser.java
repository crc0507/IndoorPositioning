//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.TelParsedResult;

final class TelResultParser extends ResultParser {
    private TelResultParser() {
    }

    public static TelParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null || !rawText.startsWith("tel:") && !rawText.startsWith("TEL:")) {
            return null;
        } else {
            String telURI = rawText.startsWith("TEL:")?"tel:" + rawText.substring(4):rawText;
            int queryStart = rawText.indexOf(63, 4);
            String number = queryStart < 0?rawText.substring(4):rawText.substring(4, queryStart);
            return new TelParsedResult(number, telURI, (String)null);
        }
    }
}
